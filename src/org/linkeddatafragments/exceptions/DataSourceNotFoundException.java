package org.linkeddatafragments.exceptions;

/**
 *
 * @author mielvandersande
 */
public class DataSourceNotFoundException extends DataSourceException {

    public DataSourceNotFoundException(String dataSourceName) {
        super(dataSourceName, "Datasource not found.");
    }  
}
