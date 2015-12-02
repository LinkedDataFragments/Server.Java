package org.linkeddatafragments.datasource;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import org.linkeddatafragments.exceptions.DataSourceException;
import org.linkeddatafragments.exceptions.UnknownDataSourceTypeException;

/**
 *
 * @author Miel Vander Sande
 * @author Bart Hanssens
 */
public class DataSourceFactory {
    public final static String HDT = "HdtDatasource";
    public final static String JENA_TDB = "JenaTDBDatasource";

    public static IDataSource create(JsonObject config) throws DataSourceException {
        String title = config.getAsJsonPrimitive("type").getAsString();
        String description = config.getAsJsonPrimitive("description").getAsString();
        String type = config.getAsJsonPrimitive("type").getAsString();
        
        JsonObject settings = config.getAsJsonObject("settings");

        switch (type) {
            case HDT:
                try {
                    File file = new File(settings.getAsJsonPrimitive("file").getAsString());
                    return new HdtDataSource(title, description, file.getAbsolutePath());
                } catch (IOException ex) {
                    throw new DataSourceException(ex);
                }
                
            case JENA_TDB:                
                File file = new File(settings.getAsJsonPrimitive("directory").getAsString());
                return new JenaTDBDataSource(title, description, file);
                
            default:
                throw new UnknownDataSourceTypeException(type);

        }

    }

}
