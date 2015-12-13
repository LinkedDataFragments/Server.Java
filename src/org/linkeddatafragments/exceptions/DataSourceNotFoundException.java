package org.linkeddatafragments.exceptions;

import org.linkeddatafragments.datasource.IDataSource;

/**
 *
 * @author mielvandersande
 */
public class DataSourceNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataSourceNotFoundException(Throwable cause) {
        super(cause.getMessage());
    }

    public DataSourceNotFoundException(String dataSourceName) {
        super("Data source " + dataSourceName + " not found.");
    }  
}
