package org.linkeddatafragments.datasource;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import java.io.File;

/**
 * Experimental Jena TDB-backed data source of Basic Linked Data Fragments.
 *
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 */
public class JenaTDBDataSource extends DataSource {
    private final Dataset tdb;
    private final String sparql = "CONSTRUCT WHERE { ?s ?p ?o } " +
                                    "ORDER BY ?s ?p ?o";

    private final String count = "SELECT (COUNT(?s) AS ?count) WHERE { ?s ?p ?o }";

    private final Query query = QueryFactory.create(sparql, Syntax.syntaxSPARQL_11);
    private final Query countQuery = QueryFactory.create(count, Syntax.syntaxSPARQL_11);

    @Override
    public TriplePatternFragment getFragment(Resource subject, Property predicate, RDFNode object, long offset, long limit) {
        checkBoundaries(offset, limit);

        Model model = tdb.getDefaultModel();
        QuerySolutionMap map = new QuerySolutionMap();
        if (subject != null) {
            map.add("s", subject);
        }
        if (predicate != null) {
            map.add("p", predicate);
        }
        if (object != null) {
            map.add("o", object);
        }

        query.setOffset(offset);
        query.setLimit(limit);

        Model triples = ModelFactory.createDefaultModel();

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model, map)) {
            qexec.execConstruct(triples);
        }

        if (triples.isEmpty()) {
            return new TriplePatternFragmentBase();
        }

        // Try to get an estimate
        long size = triples.size();
        long estimate = -1;

        try (QueryExecution qexec = QueryExecutionFactory.create(countQuery, model, map)) {
            ResultSet results = qexec.execSelect();
            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution() ;
                Literal literal = soln.getLiteral("count");
                estimate = literal.getLong();
            }
        }

        /*GraphStatisticsHandler stats = model.getGraph().getStatisticsHandler();
        if (stats != null) {
            Node s = (subject != null) ? subject.asNode() : null;
            Node p = (predicate != null) ? predicate.asNode() : null;
            Node o = (object != null) ? object.asNode() : null;
            estimate = stats.getStatistic(s, p, o);
        }*/

        // No estimate or incorrect
        if (estimate < offset + size) {
            estimate = (size == limit) ? offset + size + 1 : offset + size;
        }
        return new TriplePatternFragmentBase(triples, estimate);
    }


    /**
     * Constructor
     *
     * @param title
     * @param description
     * @param tdbdir directory used for TDB backing
     */
    public JenaTDBDataSource(String title, String description, File tdbdir) {
        super(title, description);
        this.tdb = TDBFactory.createDataset(tdbdir.getAbsolutePath());
    }
}
