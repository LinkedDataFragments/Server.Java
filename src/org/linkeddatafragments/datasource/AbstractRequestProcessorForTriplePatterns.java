package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.rdf.model.Model;

import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;
import org.linkeddatafragments.fragments.tpf.TriplePatternElement;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragment;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentImpl;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequest;

/**
 * Base class for implementations of {@link IFragmentRequestProcessor} that
 * process {@link TriplePatternFragmentRequest}s.
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
            final LinkedDataFragmentRequest request )
                                                throws IllegalArgumentException
    {
        if ( request instanceof TriplePatternFragmentRequest<?,?,?> ) {
            @SuppressWarnings("unchecked")
            final TriplePatternFragmentRequest<CTT,NVT,AVT> tpfRequest =
                      (TriplePatternFragmentRequest<CTT,NVT,AVT>) request;
            return getTPFSpecificWorker( tpfRequest );
        }
        else
            throw new IllegalArgumentException( request.getClass().getName() );
    }

    abstract protected Worker<CTT,NVT,AVT> getTPFSpecificWorker(
            final TriplePatternFragmentRequest<CTT,NVT,AVT> request )
                    throws IllegalArgumentException;


    abstract static protected class Worker<CTT,NVT,AVT>
        extends AbstractRequestProcessor.Worker
    {        
        public Worker(
                 final TriplePatternFragmentRequest<CTT,NVT,AVT> request )
        {
            super( request );
        }

        @Override
        public LinkedDataFragment createRequestedFragment()
                                                throws IllegalArgumentException
        {
            final long limit = LinkedDataFragmentRequest.TRIPLESPERPAGE;
            final long offset;
            if ( request.isPageRequest() )
                offset = limit * ( request.getPageNumber() - 1L );
            else
                offset = 0L;

            @SuppressWarnings("unchecked")
            final TriplePatternFragmentRequest<CTT,NVT,AVT> tpfRequest =
                      (TriplePatternFragmentRequest<CTT,NVT,AVT>) request;

            return createFragment( tpfRequest.getSubject(),
                                   tpfRequest.getPredicate(),
                                   tpfRequest.getObject(),
                                   offset, limit );
        }

        abstract protected LinkedDataFragment createFragment(
                            final TriplePatternElement<CTT,NVT,AVT> subj,
                            final TriplePatternElement<CTT,NVT,AVT> pred,
                            final TriplePatternElement<CTT,NVT,AVT> obj,
                            final long offset,
                            final long limit )
                                               throws IllegalArgumentException;

        protected TriplePatternFragment createEmptyTriplePatternFragment()
        {
            return new TriplePatternFragmentImpl( request.getFragmentURL(),
                                                  request.getDatasetURL() );
        }

        protected TriplePatternFragment createTriplePatternFragment(
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
