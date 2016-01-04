package org.linkeddatafragments.exceptions;

import org.linkeddatafragments.datasource.IDataSource;

/**
 *
 * @author mielvandersande
 */
abstract public class DataSourceException extends Exception {

    public DataSourceException(Throwable cause) {
        super(cause);
    }

    public DataSourceException(String datasourceName, String message) {
        super("Error for datasource '" + datasourceName + "': " + message);
    }
    
    public DataSourceException(IDataSource datasource, String message) {
        this(datasource.getTitle(), message);
    }
    
}
