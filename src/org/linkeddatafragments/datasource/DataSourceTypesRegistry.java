package org.linkeddatafragments.datasource;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry of {@link IDataSourceType}s.
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class DataSourceTypesRegistry
{
    private static Map<String, IDataSourceType> registry =
                                        new HashMap<String, IDataSourceType>();

    public static synchronized IDataSourceType getType( final String typeName )
    {
        return registry.get( typeName );
    }

    public static synchronized boolean isRegistered( final String typeName )
    {
        return registry.containsKey( typeName );
    }

    public static synchronized void register( final String typeName,
                                              final IDataSourceType type )
    {
        if ( registry.containsKey(typeName) ) {
            throw new IllegalArgumentException( "The registry already " +
                       "contains a type with the name '" + typeName + "'." );
        }
        registry.put( typeName, type );
    }

}
