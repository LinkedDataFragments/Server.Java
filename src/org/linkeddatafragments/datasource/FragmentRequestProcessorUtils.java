package org.linkeddatafragments.datasource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shared.InvalidPropertyURIException;

import org.linkeddatafragments.util.CommonResources;



/**
 * Utility functions for dealing with (fragment) requests.
 *
 * @author Ruben Verborgh
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class FragmentRequestProcessorUtils
{
    /**
     * Parses the given value as an RDF resource.
     *
     * @param value the value
     * @return the parsed value, or null if unspecified
     */
    public static Resource parseAsResource(String value) {
        RDFNode subject = parseAsNode(value);
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
    public static Property parseAsProperty(String value) {
        RDFNode predicateNode = parseAsNode(value);
        if (predicateNode instanceof Resource) {
            try {
                return ResourceFactory.createProperty(((Resource) predicateNode).getURI());
            } catch (InvalidPropertyURIException ex) {
                return CommonResources.INVALID_URI;
            }
        }
        return predicateNode == null ? null : CommonResources.INVALID_URI;
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
    public static RDFNode parseAsNode(String value) {
        // nothing or empty indicates an unknown
        if (value == null || value.isEmpty()) {
            return null;
        }
        // find the kind of entity based on the first character
        char firstChar = value.charAt(0);
        switch (firstChar) {
            // variable or blank node indicates an unknown
            case '?':
            case '_':
                return null;
            // angular brackets indicate a URI
            case '<':
                return ResourceFactory.createResource(value.substring(1, value.length() - 1));
            // quotes indicate a string
            case '"':
                Matcher matcher = STRINGPATTERN.matcher(value);
                if (matcher.matches()) {
                    String body = matcher.group(1);
                    String lang = matcher.group(2);
                    String type = matcher.group(3);
                    if (lang != null) {
                        return ResourceFactory.createLangLiteral(body, lang);
                    }
                    if (type != null) {
                        return ResourceFactory.createTypedLiteral(body, TYPES.getSafeTypeByName(type));
                    }
                    return ResourceFactory.createPlainLiteral(body);
                }
                return CommonResources.INVALID_URI;
            // assume it's a URI without angular brackets
            default:
                return ResourceFactory.createResource(value);
        }
    }

}
