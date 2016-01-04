package org.linkeddatafragments.datasource;

import org.linkeddatafragments.fragments.ILinkedDataFragment;
import org.linkeddatafragments.fragments.ILinkedDataFragmentRequest;

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
    final public ILinkedDataFragment createRequestedFragment(
            final ILinkedDataFragmentRequest request )
                    throws IllegalArgumentException
    {
        return getWorker( request ).createRequestedFragment();
    }

    abstract protected Worker getWorker(
            final ILinkedDataFragmentRequest request )
                    throws IllegalArgumentException;


    abstract static protected class Worker
    {
        public final ILinkedDataFragmentRequest request;
        
        public Worker( final ILinkedDataFragmentRequest request )
        {
            this.request = request;
        }

        abstract public ILinkedDataFragment createRequestedFragment()
                                               throws IllegalArgumentException;

    } // end of class Worker

}
