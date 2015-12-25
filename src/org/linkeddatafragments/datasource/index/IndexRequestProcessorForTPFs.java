package org.linkeddatafragments.datasource.index;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import java.util.HashMap;
import java.util.Map;

import org.linkeddatafragments.datasource.AbstractRequestProcessorForTriplePatterns;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.IFragmentRequestProcessor;
import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.tpf.TriplePatternElement;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequest;

/**
 * Implementation of {@link IFragmentRequestProcessor} that processes
 * {@link TriplePatternFragmentRequest}s over an index that provides
 * an overview of all available datasets.
 *
 * @author Miel Vander Sande
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class IndexRequestProcessorForTPFs
    extends AbstractRequestProcessorForTriplePatterns<RDFNode,String>
{
    final static String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    final static String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    final static String DC = "http://purl.org/dc/terms/";
    final static String VOID = "http://rdfs.org/ns/void#";

    private final Model model;

    public IndexRequestProcessorForTPFs(
                               final String baseUrl,
                               final HashMap<String, IDataSource> datasources )
    {
        this.model = ModelFactory.createDefaultModel();

        for (Map.Entry<String, IDataSource> entry : datasources.entrySet()) {
            String datasourceName = entry.getKey();
            IDataSource datasource = entry.getValue();

            Resource datasourceUrl = new ResourceImpl(baseUrl + "/" + datasourceName);

            model.add(datasourceUrl, new PropertyImpl(RDF + "type"), VOID + "Dataset");
            model.add(datasourceUrl, new PropertyImpl(RDFS + "label"), datasource.getTitle());
            model.add(datasourceUrl, new PropertyImpl(DC + "title"), datasource.getTitle());
            model.add(datasourceUrl, new PropertyImpl(DC + "description"), datasource.getDescription());
        }
    }

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
                                  final TriplePatternElement<RDFNode,String> s,
                                  final TriplePatternElement<RDFNode,String> p,
                                  final TriplePatternElement<RDFNode,String> o,
                                  final long offset,
                                  final long limit )
        {
            // FIXME: The following algorithm is incorrect for cases in which
            //        the requested triple pattern contains a specific variable
            //        multiple times (e.g., ?x foaf:knows ?x ).
            // see https://github.com/LinkedDataFragments/Server.Java/issues/25

            final Resource subject   = s.isVariable() ? null
                                                      : s.asTerm().asResource();
            final Property predicate = p.isVariable() ? null
                                                      : ResourceFactory.createProperty(p.asTerm().asResource().getURI());
            final RDFNode object     = o.isVariable() ? null
                                                      : o.asTerm();

            StmtIterator listStatements = model.listStatements(subject, predicate, object);
            Model result = ModelFactory.createDefaultModel();

            long index = 0;
            while (listStatements.hasNext() && index < offset) {
                listStatements.next();
                index++;
            }

            while (listStatements.hasNext() && index < (offset + limit)) {
                result.add(listStatements.next());
            }

            final boolean isLastPage = ( result.size() < offset + limit );
            return createTriplePatternFragment( result, result.size(), isLastPage );
        }

    } // end of class Worker

}
