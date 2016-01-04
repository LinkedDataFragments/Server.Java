package org.linkeddatafragments.exceptions;

/**
 *
 * @author mielvandersande
 */
public class DataSourceCreationException extends DataSourceException {

    public DataSourceCreationException(Throwable cause) {
        super(cause);
    }

    public DataSourceCreationException(String datasourceName, String message) {
        super(datasourceName, "Could not create DataSource - " + message);
    }  
}
