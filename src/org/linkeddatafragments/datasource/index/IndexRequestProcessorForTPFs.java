package org.linkeddatafragments.datasource.index;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import java.util.HashMap;
import java.util.Map;

import org.linkeddatafragments.datasource.AbstractJenaBasedRequestProcessorForTriplePatterns;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.IFragmentRequestProcessor;
import org.linkeddatafragments.fragments.LinkedDataFragment;
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
    extends AbstractJenaBasedRequestProcessorForTriplePatterns
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
    protected Worker getWorker( final TriplePatternFragmentRequest request )
                                                throws IllegalArgumentException
    {
        return new Worker( request );
    }


    protected class Worker
        extends AbstractJenaBasedRequestProcessorForTriplePatterns.Worker
    {
        public Worker( final TriplePatternFragmentRequest request ) {
            super( request );
        }

        @Override
        protected LinkedDataFragment createFragment( final Resource subject,
                                                     final Property predicate,
                                                     final RDFNode object,
                                                     final long offset,
                                                     final long limit )
        {
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
