package org.linkeddatafragments.datasource;

import java.io.Closeable;

/**
 * Processes a single request sent to a Linked Data Fragments interface.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public interface IFragmentRequestProcessor extends Closeable
{
    LinkedDataFragment createRequestedFragment();
}
