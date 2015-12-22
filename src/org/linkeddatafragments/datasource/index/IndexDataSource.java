package org.linkeddatafragments.datasource.index;

import java.util.HashMap;

import org.linkeddatafragments.datasource.DataSource;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.IFragmentRequestProcessor;

/**
 * An Index data source provides an overview of all available datasets.
 *
 * @author Miel Vander Sande
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class IndexDataSource extends DataSource {

    protected final IndexRequestProcessorForTPFs requestProcessor;

    public IndexDataSource(String baseUrl, HashMap<String, IDataSource> datasources) {
        super("Index", "List of all datasources");
        requestProcessor = new IndexRequestProcessorForTPFs( baseUrl, datasources );
    }

    @Override
    public IFragmentRequestProcessor getRequestProcessor()
    {
        return requestProcessor;
    }

}
