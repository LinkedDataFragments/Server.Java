package org.linkeddatafragments.fragments;

import javax.servlet.http.HttpServletRequest;

import org.linkeddatafragments.config.ConfigReader;

/**
 * Parses HTTP requests into specific {@link LinkedDataFragmentRequest}s.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public interface IFragmentRequestParser
{
    /**
     * Parses the given HTTP request into a specific
     * {@link LinkedDataFragmentRequest}.
     *
     * @throws IllegalArgumentException
     *         If the given HTTP request cannot be interpreted (perhaps due to
     *         missing request parameters).  
     */
    LinkedDataFragmentRequest parseIntoFragmentRequest(
            final HttpServletRequest httpRequest,
            final ConfigReader config )
                    throws IllegalArgumentException;
}
