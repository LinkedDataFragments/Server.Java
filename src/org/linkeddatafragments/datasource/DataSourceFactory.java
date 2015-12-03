package org.linkeddatafragments.datasource;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import org.linkeddatafragments.exceptions.DataSourceException;
import org.linkeddatafragments.exceptions.UnknownDataSourceTypeException;

/**
 *
 * @author Miel Vander Sande
 */
public class DataSourceFactory {

    public static IDataSource create(JsonObject config) throws DataSourceException {
        String title = config.getAsJsonPrimitive("title").getAsString();
        String description = config.getAsJsonPrimitive("description").getAsString();
        String type = config.getAsJsonPrimitive("type").getAsString();
        
        JsonObject settings = config.getAsJsonObject("settings");

        switch (type) {
            case "HdtDatasource":
                File file = new File(settings.getAsJsonPrimitive("file").getAsString());
                
                try {
                    return new HdtDataSource(title, description, file.getAbsolutePath());
                } catch (IOException ex) {
                    throw new DataSourceException(ex);
                }
            default:
                throw new UnknownDataSourceTypeException(type);

        }

    }

}
