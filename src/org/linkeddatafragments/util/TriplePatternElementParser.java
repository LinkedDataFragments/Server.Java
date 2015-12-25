package org.linkeddatafragments.util;

import org.linkeddatafragments.fragments.tpf.TriplePatternElement;
import org.linkeddatafragments.fragments.tpf.TriplePatternElementFactory;

/**
 * Parses strings (as obtained from HTTP request parameters) into
 * {@link TriplePatternElement}s. 
 *
 * @param <TermType> type for representing RDF terms
 * @param <VarType> type for representing specific variables
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 * @author Ruben Verborgh
 */
abstract public class TriplePatternElementParser<TermType,VarType>
    extends RDFTermParser<TermType>
{
    public final TriplePatternElementFactory<TermType,VarType> factory =
            new TriplePatternElementFactory<TermType,VarType>();

    public TriplePatternElement<TermType,VarType>
                            parseIntoTriplePatternElement( final String param )
    {
        // nothing or empty indicates an unspecified variable
        if ( param == null || param.isEmpty() )
            return factory.createUnspecifiedVariable();

        // identify the kind of RDF term based on the first character
        char firstChar = param.charAt(0);
        switch ( firstChar )
        {
            // specific variable
            case '?':
            {
                final String varName = param.substring(1);
                final VarType var = createSpecificVariable( varName );
                return factory.createSpecificVariable( var );
            }

            // blank node indicates an unspecified variable
            case '_':
            {
                return factory.createUnspecifiedVariable();
            }

            // assume it is an RDF term
            default:
                return factory.createRDFTerm( parseIntoRDFNode(param) );
        }
    }

    abstract public VarType createSpecificVariable( final String varName );
}
