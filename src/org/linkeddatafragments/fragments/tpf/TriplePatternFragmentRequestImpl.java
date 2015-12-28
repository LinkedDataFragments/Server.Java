package org.linkeddatafragments.fragments.tpf;

import org.linkeddatafragments.fragments.LinkedDataFragmentRequestBase;

/**
 * An implementation of {@link ITriplePatternFragmentRequest}.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TriplePatternFragmentRequestImpl<TermType,VarType>
    extends LinkedDataFragmentRequestBase
    implements ITriplePatternFragmentRequest<TermType,VarType>
{
    public final ITriplePatternElement<TermType,VarType> subject;
    public final ITriplePatternElement<TermType,VarType> predicate;
    public final ITriplePatternElement<TermType,VarType> object;

    public TriplePatternFragmentRequestImpl( final String fragmentURL,
                                             final String datasetURL,
                                             final boolean pageNumberWasRequested,
                                             final long pageNumber,
                                             final ITriplePatternElement<TermType,VarType> subject,
                                             final ITriplePatternElement<TermType,VarType> predicate,
                                             final ITriplePatternElement<TermType,VarType> object )
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
    public ITriplePatternElement<TermType,VarType> getSubject() {
        return subject;
    }

    @Override
    public ITriplePatternElement<TermType,VarType> getPredicate() {
        return predicate;
    }

    @Override
    public ITriplePatternElement<TermType,VarType> getObject() {
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
