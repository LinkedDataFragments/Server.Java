package org.linkeddatafragments.datasource.sparql;

import java.io.File;

import org.linkeddatafragments.datasource.DataSourceBase;
import org.linkeddatafragments.datasource.IFragmentRequestProcessor;
import org.linkeddatafragments.fragments.IFragmentRequestParser;
import org.linkeddatafragments.fragments.tpf.TPFRequestParserForJenaBackends;

/**
 * Experimental Jena TDB-backed data source of Basic Linked Data Fragments.
 *
 * @author <a href="mailto:bart.hanssens@fedict.be">Bart Hanssens</a>
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class SparqlDataSource extends DataSourceBase {

    /**
     * The request processor
     * 
     */
    protected final SparqlBasedRequestProcessorForTPFs requestProcessor;

    @Override
    public IFragmentRequestParser getRequestParser()
    {
        return TPFRequestParserForJenaBackends.getInstance();
    }

    @Override
    public IFragmentRequestProcessor getRequestProcessor()
    {
        return requestProcessor;
    }

    /**
     * Constructor
     *
     * @param title
     * @param description
     * @param tdbdir directory used for TDB backing
     */
    public SparqlDataSource(String title, String description, File tdbdir, String graph) {
        super(title, description);
        requestProcessor = new SparqlBasedRequestProcessorForTPFs( tdbdir , graph);
    }
}
