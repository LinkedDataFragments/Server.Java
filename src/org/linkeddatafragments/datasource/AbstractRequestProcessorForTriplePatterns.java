package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.rdf.model.Model;

import org.linkeddatafragments.fragments.ILinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;
import org.linkeddatafragments.fragments.tpf.ITriplePatternElement;
import org.linkeddatafragments.fragments.tpf.ITriplePatternFragment;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentImpl;
import org.linkeddatafragments.fragments.tpf.ITriplePatternFragmentRequest;

/**
 * Base class for implementations of {@link IFragmentRequestProcessor} that
 * process {@link ITriplePatternFragmentRequest}s.
 *
 * @param <TermType> type for representing RDF terms in triple patterns 
 * @param <VarType> type for representing specific variables in triple patterns
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public abstract class
    AbstractRequestProcessorForTriplePatterns<TermType,VarType>
        extends AbstractRequestProcessor
{
    @Override
    protected final Worker<TermType,VarType> getWorker(
            final LinkedDataFragmentRequest request )
                                                throws IllegalArgumentException
    {
        if ( request instanceof ITriplePatternFragmentRequest<?,?> ) {
            @SuppressWarnings("unchecked")
            final ITriplePatternFragmentRequest<TermType,VarType> tpfRequest =
                      (ITriplePatternFragmentRequest<TermType,VarType>) request;
            return getTPFSpecificWorker( tpfRequest );
        }
        else
            throw new IllegalArgumentException( request.getClass().getName() );
    }

    abstract protected Worker<TermType,VarType> getTPFSpecificWorker(
            final ITriplePatternFragmentRequest<TermType,VarType> request )
                    throws IllegalArgumentException;


    abstract static protected class Worker<TermType,VarType>
        extends AbstractRequestProcessor.Worker
    {        
        public Worker(
                 final ITriplePatternFragmentRequest<TermType,VarType> request )
        {
            super( request );
        }

        @Override
        public ILinkedDataFragment createRequestedFragment()
                                                throws IllegalArgumentException
        {
            final long limit = LinkedDataFragmentRequest.TRIPLESPERPAGE;
            final long offset;
            if ( request.isPageRequest() )
                offset = limit * ( request.getPageNumber() - 1L );
            else
                offset = 0L;

            @SuppressWarnings("unchecked")
            final ITriplePatternFragmentRequest<TermType,VarType> tpfRequest =
                      (ITriplePatternFragmentRequest<TermType,VarType>) request;

            return createFragment( tpfRequest.getSubject(),
                                   tpfRequest.getPredicate(),
                                   tpfRequest.getObject(),
                                   offset, limit );
        }

        abstract protected ILinkedDataFragment createFragment(
                            final ITriplePatternElement<TermType,VarType> subj,
                            final ITriplePatternElement<TermType,VarType> pred,
                            final ITriplePatternElement<TermType,VarType> obj,
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
