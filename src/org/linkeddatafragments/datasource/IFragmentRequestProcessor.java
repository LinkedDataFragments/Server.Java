package org.linkeddatafragments.datasource;

import java.io.Closeable;

import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;

/**
 * Processes {@link LinkedDataFragmentRequest}s and returns
 * the requested {@link LinkedDataFragment}s.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public interface IFragmentRequestProcessor extends Closeable
{
    LinkedDataFragment createRequestedFragment(
            final LinkedDataFragmentRequest request )
                    throws IllegalArgumentException;
}
