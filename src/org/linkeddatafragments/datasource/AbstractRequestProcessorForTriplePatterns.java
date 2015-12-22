package org.linkeddatafragments.datasource;

import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragment;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentImpl;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequest;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Base class for implementations of {@link IFragmentRequestProcessor} that
 * process {@link TriplePatternFragmentRequest}s.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public abstract class AbstractRequestProcessorForTriplePatterns
    extends AbstractRequestProcessor
{
    @Override
    protected Worker getWorker( final LinkedDataFragmentRequest request )
                                                throws IllegalArgumentException
    {
        if ( request instanceof TriplePatternFragmentRequest )
            return getWorker( (TriplePatternFragmentRequest) request );
        else
            throw new IllegalArgumentException( request.getClass().getName() );
    }

    abstract protected Worker getWorker(
            final TriplePatternFragmentRequest request )
                    throws IllegalArgumentException;


    abstract static protected class Worker
        extends AbstractRequestProcessor.Worker
    {        
        public Worker( final TriplePatternFragmentRequest request )
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
                offset = 0L; // FIXME: we should not have a limit in this case!

            final TriplePatternFragmentRequest tpfRequest =
                                        (TriplePatternFragmentRequest) request;

            return createFragment( tpfRequest.getSubject(),
                                   tpfRequest.getPredicate(),
                                   tpfRequest.getObject(),
                                   offset, limit );
        }

        abstract protected LinkedDataFragment createFragment( final String subj,
                                                              final String pred,
                                                              final String obj,
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
            // FIXME: deal with the case in which request.isPageRequest()==false

            return new TriplePatternFragmentImpl( triples,
                                                  totalSize,
                                                  request.getFragmentURL(),
                                                  request.getDatasetURL(),
                                                  request.getPageNumber(),
                                                  isLastPage );
        }

    } // end of class Worker

}
