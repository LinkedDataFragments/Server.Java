package org.linkeddatafragments.exceptions;

/**
 *
 * @author mielvandersande
 */
public class DataSourceException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataSourceException(Throwable cause) {
        super(cause.getMessage());
    }

    public DataSourceException(String message) {
        super("Could not create DataSource: " + message);
    }  
}
