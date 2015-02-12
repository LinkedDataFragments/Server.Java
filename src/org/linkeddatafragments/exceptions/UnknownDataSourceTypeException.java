package org.linkeddatafragments.exceptions;

/**
 *
 * @author mielvandersande
 */
public class UnknownDataSourceTypeException extends DataSourceException {

    public UnknownDataSourceTypeException(String type) {
        super("Type " + type + " does not exist.");
    }
    
}
