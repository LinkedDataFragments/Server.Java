package org.linkeddatafragments.exceptions;

/**
 *
 * @author mielvandersande
 */
public class UnknownDataSourceTypeException extends DataSourceException {
    private static final long serialVersionUID = 1L;

    public UnknownDataSourceTypeException(String type) {
        super("Type " + type + " does not exist.");
    } 
}
