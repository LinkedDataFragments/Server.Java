package org.linkeddatafragments.fragments.tpf;

import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;

/**
 * Represents a request of a Triple Pattern Fragment (TPF).
 *
 * @param <TermType> type for representing RDF terms in triple patterns 
 * @param <VarType> type for representing specific variables in triple patterns
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public interface ITriplePatternFragmentRequest<TermType,VarType>
    extends LinkedDataFragmentRequest
{
    public final static String PARAMETERNAME_SUBJ = "subject";
    public final static String PARAMETERNAME_PRED = "predicate";
    public final static String PARAMETERNAME_OBJ = "object";

    /**
     * Returns the subject position of the requested triple pattern.
     */
    ITriplePatternElement<TermType,VarType> getSubject();

    /**
     * Returns the predicate position of the requested triple pattern.
     */
    ITriplePatternElement<TermType,VarType> getPredicate();

    /**
     * Returns the object position of the requested triple pattern.
     */
    ITriplePatternElement<TermType,VarType> getObject();
}
