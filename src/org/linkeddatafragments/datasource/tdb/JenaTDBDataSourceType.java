package org.linkeddatafragments.datasource.tdb;

import java.io.File;

import org.linkeddatafragments.datasource.DataSourceTypesRegistry;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.IDataSourceType;
import org.linkeddatafragments.exceptions.DataSourceException;

import com.google.gson.JsonObject;

/**
 * The type of Triple Pattern Fragment data sources that are backed by
 * a Jena TDB instance.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class JenaTDBDataSourceType implements IDataSourceType
{
    public static final String TYPE_NAME = "JenaTDBDatasource";

    public static void register() {
        if ( ! DataSourceTypesRegistry.isRegistered(TYPE_NAME) ) {
            DataSourceTypesRegistry.register( TYPE_NAME,
                                              new JenaTDBDataSourceType() );
        }
    }

    @Override
    public IDataSource createDataSource( final String title,
                                         final String description,
                                         final JsonObject settings )
                                                     throws DataSourceException
    {
        final String dname = settings.getAsJsonPrimitive("directory").getAsString();
        final File dir = new File( dname );

        try {
            return new JenaTDBDataSource(title, description, dir);
        } catch (Exception ex) {
            throw new DataSourceException(ex);
        }
    }

}
