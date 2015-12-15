package org.linkeddatafragments.datasource;

import java.io.Closeable;

import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;

/**
 * A data source of Linked Data Fragments.
 *
 * @author Ruben Verborgh
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public interface IDataSource extends Closeable {

    public String getTitle();
        
    public String getDescription();

    /**
     * Returns a data source specific processor for the given request of a
     * Linked Data Fragment.
     */
    IFragmentRequestProcessor getRequestProcessor(
            final LinkedDataFragmentRequest request );
}
