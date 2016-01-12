package org.linkeddatafragments.exceptions;

/**
 *
 * @author Miel Vander Sande
 */
public class NoRegisteredMimeTypesException extends Exception {

    public NoRegisteredMimeTypesException() {
        super("List of supported mimeTypes is empty.");
    }
    
}
