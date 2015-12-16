package org.linkeddatafragments.exceptions;

/**
 *
 * @author mielvandersande
 */
public class NoRegisteredMimeTypesException extends Exception {

    public NoRegisteredMimeTypesException() {
        super("List of supported mimeTypes is empty.");
    }
    
}
