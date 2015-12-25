package org.linkeddatafragments.fragments.tpf;

import org.linkeddatafragments.fragments.LinkedDataFragmentRequestBase;

/**
 * An implementation of {@link TriplePatternFragmentRequest}.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TriplePatternFragmentRequestImpl<TermType,VarType>
    extends LinkedDataFragmentRequestBase
    implements TriplePatternFragmentRequest<TermType,VarType>
{
    public final TriplePatternElement<TermType,VarType> subject;
    public final TriplePatternElement<TermType,VarType> predicate;
    public final TriplePatternElement<TermType,VarType> object;

    public TriplePatternFragmentRequestImpl( final String fragmentURL,
                                             final String datasetURL,
                                             final boolean pageNumberWasRequested,
                                             final long pageNumber,
                                             final TriplePatternElement<TermType,VarType> subject,
                                             final TriplePatternElement<TermType,VarType> predicate,
                                             final TriplePatternElement<TermType,VarType> object )
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
    public TriplePatternElement<TermType,VarType> getSubject() {
        return subject;
    }

    @Override
    public TriplePatternElement<TermType,VarType> getPredicate() {
        return predicate;
    }

    @Override
    public TriplePatternElement<TermType,VarType> getObject() {
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
