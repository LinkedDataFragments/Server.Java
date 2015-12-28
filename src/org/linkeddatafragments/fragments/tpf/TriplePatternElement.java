package org.linkeddatafragments.fragments.tpf;

/**
 * Represents an element of a triple pattern (i.e., subject, predicate, object). 
 *
 * @param <TermType> type for representing RDF terms in triple patterns 
 * @param <VarType> type for representing specific variables in triple patterns
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public interface TriplePatternElement<TermType,VarType>
{
    /**
     * Returns true if this element is a variable (named or unnamed).
     */
    boolean isVariable();

    /**
     * Returns true if this element is a specific variable, and false if either
     * it is not a variable but an RDF term or it is some variable that is not
     * specified. The latter (unspecified variables) is possible because when
     * a client requests a triple pattern fragment, it may omit triple pattern
     * related parameters.
     *
     * If this element is a specific variable (that is, this method returns
     * true), this specific variable can be obtained by {@link #asVariable()}.
     */
    boolean isSpecificVariable();

    /**
     * Returns a representation of this element as a specific variable (assuming
     * it is a specific variable).
     *
     * @throws UnsupportedOperationException
     *         If this element is not a specific variable (i.e.,
     *         if {@link #isSpecificVariable()} returns false).
     */
    VarType asVariable() throws UnsupportedOperationException;

    /**
     * Returns a representation of this element as an RDF term (assuming it is
     * an RDF term and not a variable).
     *
     * @throws UnsupportedOperationException
     *         If this element is not an RDF term but a variable
     *         (i.e., if {@link #isVariable()} returns true).
     */
    TermType asTerm() throws UnsupportedOperationException;
}
