package org.linkeddatafragments.datasource.tdb;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;
import java.io.File;

import org.linkeddatafragments.datasource.AbstractRequestProcessorForTriplePatterns;
import org.linkeddatafragments.datasource.IFragmentRequestProcessor;
import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.tpf.TriplePatternElement;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequest;

/**
 * Implementation of {@link IFragmentRequestProcessor} that processes
 * {@link TriplePatternFragmentRequest}s over data stored in Jena TDB.
 *
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class JenaTDBBasedRequestProcessorForTPFs
    extends AbstractRequestProcessorForTriplePatterns<RDFNode,String>
{
    private final Dataset tdb;
    private final String sparql = "CONSTRUCT WHERE { ?s ?p ?o } " +
                                    "ORDER BY ?s ?p ?o";

    private final String count = "SELECT (COUNT(?s) AS ?count) WHERE { ?s ?p ?o }";

    private final Query query = QueryFactory.create(sparql, Syntax.syntaxSPARQL_11);
    private final Query countQuery = QueryFactory.create(count, Syntax.syntaxSPARQL_11);

    @Override
    protected Worker getTPFSpecificWorker(
            final TriplePatternFragmentRequest<RDFNode,String> request )
                                                throws IllegalArgumentException
    {
        return new Worker( request );
    }


    protected class Worker
       extends AbstractRequestProcessorForTriplePatterns.Worker<RDFNode,String>
    {
        public Worker( final TriplePatternFragmentRequest<RDFNode,String> req )
        {
            super( req );
        }

        @Override
        protected LinkedDataFragment createFragment(
                          final TriplePatternElement<RDFNode,String> subject,
                          final TriplePatternElement<RDFNode,String> predicate,
                          final TriplePatternElement<RDFNode,String> object,
                          final long offset,
                          final long limit )
        {
            // FIXME: The following algorithm is incorrect for cases in which
            //        the requested triple pattern contains a specific variable
            //        multiple times (e.g., ?x foaf:knows ?x ).

            Model model = tdb.getDefaultModel();
            QuerySolutionMap map = new QuerySolutionMap();
            if ( ! subject.isVariable() ) {
                map.add("s", subject.asTerm());
            }
            if ( ! predicate.isVariable() ) {
                map.add("p", predicate.asTerm());
            }
            if ( ! object.isVariable() ) {
                map.add("o", object.asTerm());
            }

            query.setOffset(offset);
            query.setLimit(limit);

            Model triples = ModelFactory.createDefaultModel();

            try (QueryExecution qexec = QueryExecutionFactory.create(query, model, map)) {
                qexec.execConstruct(triples);
            }

            if (triples.isEmpty()) {
                return createEmptyTriplePatternFragment();
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

            // create the fragment
            final boolean isLastPage = ( estimate < offset + limit );
            return createTriplePatternFragment( triples, estimate, isLastPage );
        }

    } // end of class Worker


    /**
     * Constructor
     *
     * @param tdbdir directory used for TDB backing
     */
    public JenaTDBBasedRequestProcessorForTPFs(File tdbdir) {
        this.tdb = TDBFactory.createDataset(tdbdir.getAbsolutePath());
    }
}
