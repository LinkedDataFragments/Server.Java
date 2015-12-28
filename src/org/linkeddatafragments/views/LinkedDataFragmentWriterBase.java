package org.linkeddatafragments.views;

import java.util.HashMap;
import java.util.Map;
import org.linkeddatafragments.datasource.IDataSource;

/**
 *
 * @author mielvandersande
 */
public abstract class LinkedDataFragmentWriterBase implements ILinkedDataFragmentWriter {
    private final Map<String, String> prefixes;
    private final HashMap<String, IDataSource> datasources;

    public LinkedDataFragmentWriterBase(Map<String, String> prefixes, HashMap<String, IDataSource> datasources) {
        this.prefixes = prefixes;
        this.datasources = datasources;
    }

    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    public HashMap<String, IDataSource> getDatasources() {
        return datasources;
    }
}
