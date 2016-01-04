package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.rdf.model.Model;

import org.linkeddatafragments.fragments.ILinkedDataFragment;
import org.linkeddatafragments.fragments.ILinkedDataFragmentRequest;
import org.linkeddatafragments.fragments.tpf.ITriplePatternElement;
import org.linkeddatafragments.fragments.tpf.ITriplePatternFragment;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentImpl;
import org.linkeddatafragments.fragments.tpf.ITriplePatternFragmentRequest;

/**
 * Base class for implementations of {@link IFragmentRequestProcessor} that
 * process {@link ITriplePatternFragmentRequest}s.
 *
 * @param <CTT>
 *          type for representing constants in triple patterns (i.e., URIs and
 *          literals)
 * @param <NVT>
 *          type for representing named variables in triple patterns
 * @param <AVT>
 *          type for representing anonymous variables in triple patterns (i.e.,
 *          variables denoted by a blank node)
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public abstract class
    AbstractRequestProcessorForTriplePatterns<CTT,NVT,AVT>
        extends AbstractRequestProcessor
{
    @Override
    protected final Worker<CTT,NVT,AVT> getWorker(
            final ILinkedDataFragmentRequest request )
                                                throws IllegalArgumentException
    {
        if ( request instanceof ITriplePatternFragmentRequest<?,?,?> ) {
            @SuppressWarnings("unchecked")
            final ITriplePatternFragmentRequest<CTT,NVT,AVT> tpfRequest =
                      (ITriplePatternFragmentRequest<CTT,NVT,AVT>) request;
            return getTPFSpecificWorker( tpfRequest );
        }
        else
            throw new IllegalArgumentException( request.getClass().getName() );
    }

    abstract protected Worker<CTT,NVT,AVT> getTPFSpecificWorker(
            final ITriplePatternFragmentRequest<CTT,NVT,AVT> request )
                    throws IllegalArgumentException;


    abstract static protected class Worker<CTT,NVT,AVT>
        extends AbstractRequestProcessor.Worker
    {        
        public Worker(
                 final ITriplePatternFragmentRequest<CTT,NVT,AVT> request )
        {
            super( request );
        }

        @Override
        public ILinkedDataFragment createRequestedFragment()
                                                throws IllegalArgumentException
        {
            final long limit = ILinkedDataFragmentRequest.TRIPLESPERPAGE;
            final long offset;
            if ( request.isPageRequest() )
                offset = limit * ( request.getPageNumber() - 1L );
            else
                offset = 0L;

            @SuppressWarnings("unchecked")
            final ITriplePatternFragmentRequest<CTT,NVT,AVT> tpfRequest =
                      (ITriplePatternFragmentRequest<CTT,NVT,AVT>) request;

            return createFragment( tpfRequest.getSubject(),
                                   tpfRequest.getPredicate(),
                                   tpfRequest.getObject(),
                                   offset, limit );
        }

        abstract protected ILinkedDataFragment createFragment(
                            final ITriplePatternElement<CTT,NVT,AVT> subj,
                            final ITriplePatternElement<CTT,NVT,AVT> pred,
                            final ITriplePatternElement<CTT,NVT,AVT> obj,
                            final long offset,
                            final long limit )
                                               throws IllegalArgumentException;

        protected ITriplePatternFragment createEmptyTriplePatternFragment()
        {
            return new TriplePatternFragmentImpl( request.getFragmentURL(),
                                                  request.getDatasetURL() );
        }

        protected ITriplePatternFragment createTriplePatternFragment(
                                                     final Model triples,
                                                     final long totalSize,
                                                     final boolean isLastPage )
        {
            return new TriplePatternFragmentImpl( triples,
                                                  totalSize,
                                                  request.getFragmentURL(),
                                                  request.getDatasetURL(),
                                                  request.getPageNumber(),
                                                  isLastPage );
        }

    } // end of class Worker

}
