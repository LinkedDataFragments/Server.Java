package org.linkeddatafragments.fragments.tpf;

import org.linkeddatafragments.fragments.LinkedDataFragmentRequestBase;

/**
 * An implementation of {@link TriplePatternFragmentRequest}.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TriplePatternFragmentRequestImpl
    extends LinkedDataFragmentRequestBase
    implements TriplePatternFragmentRequest
{
    public final String subject;
    public final String predicate;
    public final String object;

    public TriplePatternFragmentRequestImpl( final String fragmentURL,
                                             final String datasetURL,
                                             final boolean pageNumberWasRequested,
                                             final long pageNumber,
                                             final String subject,
                                             final String predicate,
                                             final String object )
    {
        super( fragmentURL, datasetURL, pageNumberWasRequested, pageNumber );

        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getPredicate() {
        return predicate;
    }

    @Override
    public String getObject() {
        return object;
    }

}
