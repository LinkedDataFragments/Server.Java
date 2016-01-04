package org.linkeddatafragments.exceptions;

/**
 *
 * @author mielvandersande
 */
public class UnknownDataSourceTypeException extends DataSourceCreationException {
    
    public UnknownDataSourceTypeException(String type) {
        super("", "Type " + type + " does not exist.");
    } 
}
