package org.linkeddatafragments.config;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Reads the configuration of a Linked Data Fragments server.
 *
 * @author Ruben Verborgh
 */
public class ConfigReader {
    private final Map<String, JsonObject> dataSources = new HashMap<>();
    private final Map<String, String> prefixes = new HashMap<>();
    private final String baseURL;

    /**
     * Creates a new configuration reader.
     *
     * @param configReader the configuration
     */
    public ConfigReader(Reader configReader) {
        JsonObject root = new JsonParser().parse(configReader).getAsJsonObject();
        this.baseURL = root.has("baseURL") ? root.getAsJsonPrimitive("baseURL").getAsString() : null;
        
        for (Entry<String, JsonElement> entry : root.getAsJsonObject("datasources").entrySet()) {
            JsonObject dataSource = entry.getValue().getAsJsonObject();
            this.dataSources.put(entry.getKey(), dataSource);
        }
        for (Entry<String, JsonElement> entry : root.getAsJsonObject("prefixes").entrySet()) {
            this.prefixes.put(entry.getKey(), entry.getValue().getAsString());
        }
    }

    /**
     * Gets the data sources.
     *
     * @return the data sources
     */
    public Map<String, JsonObject> getDataSources() {
        return dataSources;
    }

    /**
     * Gets the prefixes.
     *
     * @return the prefixes
     */
    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    public String getBaseURL() {
        return baseURL;
    }
}
