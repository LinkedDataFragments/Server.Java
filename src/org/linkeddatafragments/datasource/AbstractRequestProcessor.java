package org.linkeddatafragments.datasource;

import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;

/**
 * Base class for implementations of {@link IFragmentRequestProcessor}.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
abstract public class AbstractRequestProcessor
    implements IFragmentRequestProcessor
{ 
    @Override
    public void close() {}

    @Override
    final public LinkedDataFragment createRequestedFragment(
            final LinkedDataFragmentRequest request )
                    throws IllegalArgumentException
    {
        return getWorker( request ).createRequestedFragment();
    }

    abstract protected Worker getWorker(
            final LinkedDataFragmentRequest request )
                    throws IllegalArgumentException;


    abstract static protected class Worker
    {
        public final LinkedDataFragmentRequest request;
        
        public Worker( final LinkedDataFragmentRequest request )
        {
            this.request = request;
        }

        abstract public LinkedDataFragment createRequestedFragment()
                                               throws IllegalArgumentException;

    } // end of class Worker

}
