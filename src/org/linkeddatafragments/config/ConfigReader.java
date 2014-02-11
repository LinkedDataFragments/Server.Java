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
 * @author Ruben Verborgh
 */
public class ConfigReader {
	private final Map<String, String> dataSources = new HashMap<String, String>();
	private final Map<String, String> prefixes = new HashMap<String, String>();
	
	/**
	 * Creates a new configuration reader.
	 * @param configReader the configuration
	 */
	public ConfigReader(Reader configReader) {
        final JsonObject root = new JsonParser().parse(configReader).getAsJsonObject();
        for (final Entry<String, JsonElement> entry : root.getAsJsonObject("datasources").entrySet()) {
        	final JsonObject dataSource = entry.getValue().getAsJsonObject();
        	this.dataSources.put(entry.getKey(), dataSource.getAsJsonPrimitive("path").getAsString());
        }
        for (final Entry<String, JsonElement> entry : root.getAsJsonObject("prefixes").entrySet())
        	this.prefixes.put(entry.getKey(), entry.getValue().getAsString());
	}
	
	/**
	 * Gets the data sources.
	 * @return the data sources
	 */
	public Map<String, String> getDataSources() {
		return dataSources;
	}
	
	/**
	 * Gets the prefixes.
	 * @return the prefixes
	 */
	public Map<String, String> getPrefixes() {
		return prefixes;
	}
}
