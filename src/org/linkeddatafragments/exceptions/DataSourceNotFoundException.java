package org.linkeddatafragments.exceptions;

/**
 *
 * @author Miel Vander Sande
 */
public class DataSourceNotFoundException extends DataSourceException {

    public DataSourceNotFoundException(String dataSourceName) {
        super(dataSourceName, "Datasource not found.");
    }  
}
