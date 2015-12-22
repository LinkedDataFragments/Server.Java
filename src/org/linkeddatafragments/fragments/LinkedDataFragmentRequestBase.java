package org.linkeddatafragments.fragments;

/**
 * Base class for implementations of {@link LinkedDataFragmentRequest}.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public abstract class LinkedDataFragmentRequestBase
    implements LinkedDataFragmentRequest
{
    public final String fragmentURL;
    public final String datasetURL;
    public final boolean pageNumberWasRequested;
    public final long pageNumber;
    
    public LinkedDataFragmentRequestBase( final String fragmentURL,
                                          final String datasetURL,
                                          final boolean pageNumberWasRequested,
                                          final long pageNumber )
    {
        this.fragmentURL = fragmentURL;
        this.datasetURL = datasetURL;
        this.pageNumberWasRequested = pageNumberWasRequested;
        this.pageNumber = pageNumber;
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

}
