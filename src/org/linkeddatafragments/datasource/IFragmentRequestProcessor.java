package org.linkeddatafragments.datasource;

import java.io.Closeable;

import org.linkeddatafragments.fragments.ILinkedDataFragment;
import org.linkeddatafragments.fragments.ILinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;

/**
 * Processes {@link LinkedDataFragmentRequest}s and returns
 * the requested {@link ILinkedDataFragment}s.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public interface IFragmentRequestProcessor extends Closeable
{
    ILinkedDataFragment createRequestedFragment(
            final LinkedDataFragmentRequest request )
                    throws IllegalArgumentException;
}
