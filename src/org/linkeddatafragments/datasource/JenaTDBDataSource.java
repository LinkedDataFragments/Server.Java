package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import java.io.File;

/**
 * Experimental Jena TDB-backed data source of Basic Linked Data Fragments.
 * 
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 */
public class JenaTDBDataSource extends DataSource {
    private final Dataset tdb;
    private final String sparql = "CONSTRUCT { ?s ?p ?o } " +
                                    "WHERE { ?s ?p ?o } " +
                                    "ORDER BY ?s ?p ?o";
    
    private final Query query = QueryFactory.create(sparql, Syntax.syntaxSPARQL_11);
    
    @Override
    public TriplePatternFragment getFragment(Resource subject, Property predicate, RDFNode object, long offset, long limit) {
        checkBoundaries(offset, limit);
        
        tdb.begin(ReadWrite.READ);
        
        Model model = tdb.getDefaultModel();
        QuerySolutionMap map = new QuerySolutionMap();
        map.add("s", subject);
        map.add("p", predicate);
        map.add("o", object);
        
        query.setOffset(offset);
        query.setLimit(limit);

        QueryExecution qexec = QueryExecutionFactory.create(query, model, map);
        Model triples = ModelFactory.createDefaultModel();
        qexec.execConstruct(triples);

        // Try to get an estimate
        long size = triples.size();
        long estimate = -1;

        GraphStatisticsHandler stats = model.getGraph().getStatisticsHandler();
        if (stats != null) {
            estimate = stats.getStatistic(subject.asNode(), predicate.asNode(), object.asNode());
        }
        // No estimate or incorrect
        if (estimate < offset + size) {
            estimate = (size == limit) ? offset + size + 1 : offset + size;
        }
        
        tdb.end();

        if (triples.isEmpty()) {
            return new TriplePatternFragmentBase();
        } else {
            return new TriplePatternFragmentBase(triples, estimate);
        }
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
