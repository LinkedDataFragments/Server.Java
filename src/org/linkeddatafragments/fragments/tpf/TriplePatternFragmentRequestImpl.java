package org.linkeddatafragments.fragments.tpf;

import org.linkeddatafragments.fragments.LinkedDataFragmentRequestBase;

/**
 * An implementation of {@link TriplePatternFragmentRequest}.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TriplePatternFragmentRequestImpl<CTT,NVT,AVT>
    extends LinkedDataFragmentRequestBase
    implements TriplePatternFragmentRequest<CTT,NVT,AVT>
{
    public final TriplePatternElement<CTT,NVT,AVT> subject;
    public final TriplePatternElement<CTT,NVT,AVT> predicate;
    public final TriplePatternElement<CTT,NVT,AVT> object;

    public TriplePatternFragmentRequestImpl( final String fragmentURL,
                                             final String datasetURL,
                                             final boolean pageNumberWasRequested,
                                             final long pageNumber,
                                             final TriplePatternElement<CTT,NVT,AVT> subject,
                                             final TriplePatternElement<CTT,NVT,AVT> predicate,
                                             final TriplePatternElement<CTT,NVT,AVT> object )
    {
        super( fragmentURL, datasetURL, pageNumberWasRequested, pageNumber );

        if ( subject == null )
            throw new IllegalArgumentException();

        if ( predicate == null )
            throw new IllegalArgumentException();

        if ( object == null )
            throw new IllegalArgumentException();

        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public TriplePatternElement<CTT,NVT,AVT> getSubject() {
        return subject;
    }

    @Override
    public TriplePatternElement<CTT,NVT,AVT> getPredicate() {
        return predicate;
    }

    @Override
    public TriplePatternElement<CTT,NVT,AVT> getObject() {
        return object;
    }

    @Override
    public String toString()
    {
        return "TriplePatternFragmentRequest(" +
               "class: " + getClass().getName() +
               ", subject: " + subject.toString() +
               ", predicate: " + predicate.toString() +
               ", object: " + object.toString() +
               ", fragmentURL: " + fragmentURL +
               ", isPageRequest: " + pageNumberWasRequested +
               ", pageNumber: " + pageNumber +
               ")";
    }

}
