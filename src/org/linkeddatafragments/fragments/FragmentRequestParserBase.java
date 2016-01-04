package org.linkeddatafragments.fragments;

import javax.servlet.http.HttpServletRequest;

import org.linkeddatafragments.config.ConfigReader;

/**
 * Base class for implementations of {@link IFragmentRequestParser}.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
abstract public class FragmentRequestParserBase implements IFragmentRequestParser
{
    @Override
    final public ILinkedDataFragmentRequest parseIntoFragmentRequest(
            final HttpServletRequest httpRequest,
            final ConfigReader config )
                    throws IllegalArgumentException
    {
        return getWorker( httpRequest, config ).createFragmentRequest();
    }

    abstract protected Worker getWorker( final HttpServletRequest httpRequest,
                                         final ConfigReader config )
                                               throws IllegalArgumentException;


    abstract static protected class Worker
    {
        public final HttpServletRequest request;
        public final ConfigReader config;

        public final boolean pageNumberWasRequested;
        public final long pageNumber;
        
        public Worker( final HttpServletRequest request,
                       final ConfigReader config )
        {
            this.request = request;
            this.config = config;
            
            final String givenPageNumber = request.getParameter(
                              ILinkedDataFragmentRequest.PARAMETERNAME_PAGE );
            if ( givenPageNumber != null ) {
                long pageNumber;
                try {
                    pageNumber = Long.parseLong( givenPageNumber );
                } catch (NumberFormatException ex) {
                    pageNumber = 1L;
                }
                this.pageNumber = ( pageNumber > 0 ) ? pageNumber : 1L;
                this.pageNumberWasRequested = true;
            }
            else {
                this.pageNumber = 1L;
                this.pageNumberWasRequested = false;
            }
        }

        abstract public ILinkedDataFragmentRequest createFragmentRequest()
                                               throws IllegalArgumentException;

        public String getFragmentURL() {
            final String datasetURL = getDatasetURL();
            final String query = request.getQueryString();
            return query == null ? datasetURL : (datasetURL + "?" + query);
        }

        public String getDatasetURL() {
            return extractBaseURL( request, config ) + request.getRequestURI();
        }

    } // end of class Worker


    // ----- HELPERS ---------

    public static String extractBaseURL( final HttpServletRequest request,
                                         final ConfigReader config ) {
        if (config.getBaseURL() != null) {
            return config.getBaseURL();
        } else if ((request.getServerPort() == 80)
                || (request.getServerPort() == 443)) {
            return request.getScheme() + "://"
                    + request.getServerName();
        } else {
            return request.getScheme() + "://"
                    + request.getServerName() + ":" + request.getServerPort();
        }
    }

}
