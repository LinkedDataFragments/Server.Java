package org.linkeddatafragments.fragments.tpf;

/**
 * A factory for {@link ITriplePatternElement}s. 
 *
 * @param <TermType> type for representing RDF terms in triple patterns 
 * @param <VarType> type for representing specific variables in triple patterns
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TriplePatternElementFactory<TermType,VarType>
{
    public ITriplePatternElement<TermType,VarType> createUnspecifiedVariable() {
        return new UnspecifiedVariable<TermType,VarType>();
    }

    public ITriplePatternElement<TermType,VarType> createSpecificVariable(
            final VarType variable ) {
        return new SpecificVariable<TermType,VarType>( variable );
    }
    public ITriplePatternElement<TermType,VarType> createRDFTerm(
            final TermType term ) {
        return new RDFTerm<TermType,VarType>( term );
    }

    static abstract public class Variable<TermType,VarType>
        implements ITriplePatternElement<TermType,VarType>
    {
        public boolean isVariable() { return true; }
        public TermType asTerm() { throw new UnsupportedOperationException(); }
    }

    static public class UnspecifiedVariable<TermType,VarType>
        extends Variable<TermType,VarType>
    {
        public boolean isSpecificVariable() { return false; }
        public VarType asVariable() { throw new UnsupportedOperationException(); }
        public String toString() { return "UnspecifiedVariable"; }
    }

    static public class SpecificVariable<TermType,VarType>
        extends Variable<TermType,VarType>
    {
        protected final VarType v;
        public SpecificVariable( final VarType variable ) { v = variable; }
        public boolean isSpecificVariable() { return true; }
        public VarType asVariable() { return v; }
        public String toString() { return "SpecificVariable(" + v.toString() + ")"; }
    }

    static public class RDFTerm<TermType,VarType>
        implements ITriplePatternElement<TermType,VarType>
    {
        protected final TermType t;
        public RDFTerm( final TermType term ) { t = term; }
        public boolean isVariable() { return false; }
        public boolean isSpecificVariable() { return false; }
        public VarType asVariable() { throw new UnsupportedOperationException(); }
        public TermType asTerm() { return t; }
        public String toString() { return "RDFTerm(" + t.toString() + ")(type: " + t.getClass().getSimpleName() + ")"; }
    }

}
