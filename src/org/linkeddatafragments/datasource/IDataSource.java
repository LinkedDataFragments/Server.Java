package org.linkeddatafragments.datasource;

import java.io.Closeable;

import org.linkeddatafragments.fragments.IFragmentRequestParser;

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
     * Returns a data source specific {@link IFragmentRequestParser}.
     */
    IFragmentRequestParser getRequestParser();

    /**
     * Returns a data source specific {@link IFragmentRequestProcessor}.
     */
    IFragmentRequestProcessor getRequestProcessor();
}
