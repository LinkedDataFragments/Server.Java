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
        if ( request instanceof TriplePatternFragmentRequest<?,?> ) {
            @SuppressWarnings("unchecked")
            final TriplePatternFragmentRequest<TermType,VarType> tpfRequest =
                      (TriplePatternFragmentRequest<TermType,VarType>) request;
            return getTPFSpecificWorker( tpfRequest );
        }
        else
            throw new IllegalArgumentException( request.getClass().getName() );
    }

    abstract protected Worker<TermType,VarType> getTPFSpecificWorker(
            final TriplePatternFragmentRequest<TermType,VarType> request )
                    throws IllegalArgumentException;


    abstract static protected class Worker<TermType,VarType>
        extends AbstractRequestProcessor.Worker
    {        
        public Worker(
                 final TriplePatternFragmentRequest<TermType,VarType> request )
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
            final TriplePatternFragmentRequest<TermType,VarType> tpfRequest =
                      (TriplePatternFragmentRequest<TermType,VarType>) request;

            return createFragment( tpfRequest.getSubject(),
                                   tpfRequest.getPredicate(),
                                   tpfRequest.getObject(),
                                   offset, limit );
        }

        abstract protected LinkedDataFragment createFragment(
                            final TriplePatternElement<TermType,VarType> subj,
                            final TriplePatternElement<TermType,VarType> pred,
                            final TriplePatternElement<TermType,VarType> obj,
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
