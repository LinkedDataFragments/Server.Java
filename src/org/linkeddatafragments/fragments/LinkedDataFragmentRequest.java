package org.linkeddatafragments.fragments;

/**
 * Basis for representing a request of some type of Linked Data Fragment (LDF).
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public interface LinkedDataFragmentRequest
{
    public final static long TRIPLESPERPAGE = 100L;
    public final static String PARAMETERNAME_PAGE = "page";

    /**
     * Returns the URL of the requested LDF.
     */
    String getFragmentURL();

    /**
     * Returns the URL of the dataset to which the requested LDF belongs.
     */
    String getDatasetURL();

    /**
     * Returns true if the request is for a specific page of the requested
     * fragment. In this case, {@link #getPageNumber()} can be used to obtain
     * the requested page number.
     */
    boolean isPageRequest();

    /**
     * Returns the number of the page requested for the LDF, if any (thatis,
     * if {@link #isPageOnly()} returns true).
     *
     * @throws UnsupportedOperationException
     *         If the request is not for a specific page. 
     */
    long getPageNumber() throws UnsupportedOperationException;
}
