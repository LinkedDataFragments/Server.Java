package org.linkeddatafragments.datasource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequest;
import org.linkeddatafragments.util.CommonResources;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Base class for implementations of {@link IFragmentRequestProcessor} that
 * process {@link TriplePatternFragmentRequest}s based on the Jena API.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 * @author Ruben Verborgh
 */
abstract public class AbstractJenaBasedRequestProcessorForTriplePatterns
    extends AbstractRequestProcessorForTriplePatterns
{
    abstract static protected class Worker
        extends AbstractRequestProcessorForTriplePatterns.Worker
    {        
        public Worker( final TriplePatternFragmentRequest request )
        {
            super( request );
        }

        @Override
        protected LinkedDataFragment createFragment( final String subject,
                                                     final String predicate,
                                                     final String object,
                                                     final long offset,
                                                     final long limit )
                                                throws IllegalArgumentException
        {
            final Resource s = parseAsResource( subject );
            final Property p = parseAsProperty( predicate );
            final RDFNode  o = parseAsNode( object );

            return createFragment( s, p, o, offset, limit );
        }

        abstract protected LinkedDataFragment createFragment(
                                                      final Resource subject,
                                                      final Property predicate,
                                                      final RDFNode object,
                                                      final long offset,
                                                      final long limit )
                                               throws IllegalArgumentException;

        /**
         * Parses the given value as an RDF resource.
         *
         * @param value the value
         * @return the parsed value, or null if unspecified
         */
        public Resource parseAsResource( String value )
        {
            RDFNode subject = parseAsNode( value );
            return subject == null || subject instanceof Resource
                    ? (Resource) subject
                    : CommonResources.INVALID_URI;
        }

        /**
         * Parses the given value as an RDF property.
         *
         * @param value the value
         * @return the parsed value, or null if unspecified
         */
        public Property parseAsProperty( String value )
        {
            RDFNode predicateNode = parseAsNode( value );
            if ( predicateNode instanceof Resource ) {
                final String uri = ( (Resource) predicateNode ).getURI();
                return ResourceFactory.createProperty( uri );
            }
            else if ( predicateNode == null ) {
                return null;
            }
            else {
                return CommonResources.INVALID_URI;
            }
        }

        public final static TypeMapper TYPES = TypeMapper.getInstance();
        public final static Pattern STRINGPATTERN
                = Pattern.compile("^\"(.*)\"(?:@(.*)|\\^\\^<?([^<>]*)>?)?$");

        /**
         * Parses the given value as an RDF node.
         *
         * @param value the value
         * @return the parsed value, or null if unspecified
         */
        public RDFNode parseAsNode( String value )
        {
            // nothing or empty indicates an unknown
            if ( value == null || value.isEmpty() ) {
                return null;
            }

            // find the kind of entity based on the first character
            char firstChar = value.charAt(0);
            switch ( firstChar )
            {
                // variable or blank node indicates an unknown
                case '?':
                case '_':
                    return null;

                // angular brackets indicate a URI
                case '<':
                    return ResourceFactory.createResource(
                                      value.substring(1, value.length() - 1) );

                // quotes indicate a string
                case '"':
                    Matcher matcher = STRINGPATTERN.matcher( value );
                    if ( matcher.matches() ) {
                        String body = matcher.group(1);
                        String lang = matcher.group(2);
                        String type = matcher.group(3);
                        if ( lang != null ) {
                            return ResourceFactory.createLangLiteral(
                                    body, lang );
                        }
                        else if ( type != null ) {
                            return ResourceFactory.createTypedLiteral(
                                    body, TYPES.getSafeTypeByName(type) );
                        }
                        else {
                            return ResourceFactory.createPlainLiteral( body );
                        }
                    }
                    else {
                        return CommonResources.INVALID_URI;
                    }                    

                // assume it's a URI without angular brackets
                default:
                    return ResourceFactory.createResource( value );
            }
        }

    } // end of class Worker

}
