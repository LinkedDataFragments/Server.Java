package org.linkeddatafragments.datasource;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragment;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentImpl;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequest;


/**
 * Base class for implementations of {@link IFragmentRequestProcessor} that
 * process triple pattern based requests.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public abstract class AbstractRequestProcessorForTriplePatterns
    implements IFragmentRequestProcessor
{
    public final TriplePatternFragmentRequest request;
    public final long pageNumber;

    public AbstractRequestProcessorForTriplePatterns(
            final TriplePatternFragmentRequest request )
    {
        this.request = request;
        if ( request.isPageRequest() )
            this.pageNumber = request.getPageNumber();
        else
            this.pageNumber = 1L; 
    }

    @Override
    public void close() {}

    @Override
    public LinkedDataFragment createRequestedFragment()
    {
        final long limit = LinkedDataFragmentRequest.TRIPLESPERPAGE;
        final long offset = limit * ( pageNumber - 1L );

        return createFragment( request.getSubject(),
                               request.getPredicate(),
                               request.getObject(),
                               offset, limit );
        
    }

    protected LinkedDataFragment createFragment( final String subj,
                                                 final String pred,
                                                 final String obj,
                                                 final long offset,
                                                 final long limit )
    {
        final Resource s = FragmentRequestProcessorUtils.parseAsResource(subj);
        final Property p = FragmentRequestProcessorUtils.parseAsProperty(pred);
        final RDFNode  o = FragmentRequestProcessorUtils.parseAsNode(obj);

        return createFragment( s, p, o, offset, limit );
    }

    abstract protected LinkedDataFragment createFragment( final Resource subject,
                                                          final Property predicate,
                                                          final RDFNode object,
                                                          final long offset,
                                                          final long limit );

    protected TriplePatternFragment createEmptyTriplePatternFragment()
    {
        return new TriplePatternFragmentImpl( request.getFragmentURL(),
                                              request.getDatasetURL() );
    }

    protected TriplePatternFragment createTriplePatternFragment(
                      Model triples, long totalSize, final boolean isLastPage )
    {
        return new TriplePatternFragmentImpl( triples,
                                              totalSize,
                                              request.getFragmentURL(),
                                              request.getDatasetURL(),
                                              pageNumber,
                                              isLastPage );
    }

}
