package org.linkeddatafragments.datasource.tdb;

import java.io.File;

import org.linkeddatafragments.datasource.DataSource;
import org.linkeddatafragments.datasource.IFragmentRequestProcessor;

/**
 * Experimental Jena TDB-backed data source of Basic Linked Data Fragments.
 *
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class JenaTDBDataSource extends DataSource {

    protected final JenaTDBBasedRequestProcessorForTPFs requestProcessor;

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
    public JenaTDBDataSource(String title, String description, File tdbdir) {
        super(title, description);
        requestProcessor = new JenaTDBBasedRequestProcessorForTPFs( tdbdir );
    }
}
