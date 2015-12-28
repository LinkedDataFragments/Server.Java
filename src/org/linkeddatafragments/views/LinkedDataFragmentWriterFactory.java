package org.linkeddatafragments.views;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.linkeddatafragments.datasource.IDataSource;

/**
 *
 * @author mielvandersande
 */
public class LinkedDataFragmentWriterFactory {
    
    private final static String HTML = "text/html";
    
    
    public static ILinkedDataFragmentWriter create(Map <String, String> prefixes, HashMap<String, IDataSource> datasources, String mimeType) throws IOException {
        switch (mimeType) {
            case HTML:
                return new HtmlWriterImpl(prefixes, datasources);
            default:
                return new RdfWriterImpl(prefixes, datasources, mimeType);
        }
    }
}
