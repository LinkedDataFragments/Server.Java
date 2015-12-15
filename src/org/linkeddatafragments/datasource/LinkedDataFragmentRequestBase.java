package org.linkeddatafragments.datasource;

import javax.servlet.http.HttpServletRequest;

import org.linkeddatafragments.config.ConfigReader;

/**
 * Base class for implementations of {@link LinkedDataFragmentRequest} that
 * are based on an {@link HttpServletRequest}.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public abstract class LinkedDataFragmentRequestBase
    implements LinkedDataFragmentRequest
{
    public final HttpServletRequest request;
    public final ConfigReader config;

    public final String fragmentURL;
    public final String datasetURL;
    public final boolean pageNumberWasRequested;
    public final long pageNumber;
    
    public LinkedDataFragmentRequestBase( final HttpServletRequest request,
                                          final ConfigReader config )
    {
        this.request = request;
        this.config = config;

        this.fragmentURL = extractFragmentURL( request, config );
        this.datasetURL = extractDatasetURL( request, config );
        
        final String givenPageNumber = request.getParameter( PARAMETERNAME_PAGE );
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

    @Override
    public String getFragmentURL() {
        return fragmentURL;
    }

    @Override
    public String getDatasetURL() {
        return datasetURL;
    }

    @Override
    public boolean isPageRequest() {
        return pageNumberWasRequested;
    }

    @Override
    public long getPageNumber() throws UnsupportedOperationException {
        if ( pageNumberWasRequested )
            return pageNumber;
        else
            throw new UnsupportedOperationException();
    }


    // ----- HELPERS ---------

    public static String extractFragmentURL( final HttpServletRequest request,
                                             final ConfigReader config ) {
        final String datasetURL = extractDatasetURL( request, config );
        final String query = request.getQueryString();
        return query == null ? datasetURL : (datasetURL + "?" + query);
    }

    public static String extractDatasetURL( final HttpServletRequest request,
                                            final ConfigReader config ) {
        return extractBaseURL( request, config ) + request.getRequestURI();
    }

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
