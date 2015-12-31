package org.linkeddatafragments.fragments.tpf;

/**
 * A factory for {@link TriplePatternElement}s.
 *
 * @param <CTT>
 *          type for representing constants in triple patterns (i.e., URIs and
 *          literals)
 * @param <NVT>
 *          type for representing named variables in triple patterns
 * @param <AVT>
 *          type for representing anonymous variables in triple patterns (i.e.,
 *          variables denoted by a blank node)
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TriplePatternElementFactory<CTT,NVT,AVT>
{
    public TriplePatternElement<CTT,NVT,AVT> createUnspecifiedVariable()
    {
        return new UnspecifiedVariable<CTT,NVT,AVT>();
    }

    public TriplePatternElement<CTT,NVT,AVT> createNamedVariable( final NVT v )
    {
        return new NamedVariable<CTT,NVT,AVT>( v );
    }

    public TriplePatternElement<CTT,NVT,AVT> createAnonymousVariable(
                                                              final AVT bnode )
    {
        return new AnonymousVariable<CTT,NVT,AVT>( bnode );
    }

    public TriplePatternElement<CTT,NVT,AVT> createConstantRDFTerm(
                                                               final CTT term )
    {
        return new ConstantRDFTerm<CTT,NVT,AVT>( term );
    }


    static abstract public class Variable<CTT,NVT,AVT>
        implements TriplePatternElement<CTT,NVT,AVT>
    {
        @Override
        public boolean isVariable() { return true; }
        @Override
        public CTT asConstantTerm() { throw new UnsupportedOperationException(); }
    }

    static public class UnspecifiedVariable<CTT,NVT,AVT>
        extends Variable<CTT,NVT,AVT>
    {
        @Override
        public boolean isSpecificVariable() { return false; }
        @Override
        public boolean isNamedVariable() { return false; }
        @Override
        public NVT asNamedVariable() { throw new UnsupportedOperationException(); }
        @Override
        public boolean isAnonymousVariable() { return false; }
        @Override
        public AVT asAnonymousVariable() { throw new UnsupportedOperationException(); }
        @Override
        public String toString() { return "UnspecifiedVariable"; }
    }

    static abstract public class SpecificVariable<CTT,NVT,AVT>
        extends Variable<CTT,NVT,AVT>
    {
        @Override
        public boolean isSpecificVariable() { return true; }
    }

    static public class NamedVariable<CTT,NVT,AVT>
        extends SpecificVariable<CTT,NVT,AVT>
    {
        protected final NVT v;
        public NamedVariable( final NVT variable ) { v = variable; }
        @Override
        public boolean isNamedVariable() { return true; }
        @Override
        public NVT asNamedVariable() { return v; }
        @Override
        public boolean isAnonymousVariable() { return false; }
        @Override
        public AVT asAnonymousVariable() { throw new UnsupportedOperationException(); }
        @Override
        public String toString() { return "NamedVariable(" + v.toString() + ")"; }
    }

    static public class AnonymousVariable<CTT,NVT,AVT>
        extends SpecificVariable<CTT,NVT,AVT>
    {
        protected final AVT bn;
        public AnonymousVariable( final AVT bnode ) { bn = bnode; }
        @Override
        public boolean isNamedVariable() { return false; }
        @Override
        public NVT asNamedVariable() { throw new UnsupportedOperationException(); }
        @Override
        public boolean isAnonymousVariable() { return true; }
        @Override
        public AVT asAnonymousVariable() { return bn; }
        @Override
        public String toString() { return "AnonymousVariable(" + bn.toString() + ")"; }
    }

    static public class ConstantRDFTerm<CTT,NVT,AVT>
        implements TriplePatternElement<CTT,NVT,AVT>
    {
        protected final CTT t;
        public ConstantRDFTerm( final CTT term ) { t = term; }
        @Override
        public boolean isVariable() { return false; }
        @Override
        public boolean isSpecificVariable() { return false; }
        @Override
        public boolean isNamedVariable() { return false; }
        @Override
        public NVT asNamedVariable() { throw new UnsupportedOperationException(); }
        @Override
        public boolean isAnonymousVariable() { return false; }
        @Override
        public AVT asAnonymousVariable() { throw new UnsupportedOperationException(); }
        @Override
        public CTT asConstantTerm() { return t; }
        @Override
        public String toString() { return "ConstantRDFTerm(" + t.toString() + ")(type: " + t.getClass().getSimpleName() + ")"; }
    }

}
