package org.linkeddatafragments.config;

import java.io.File;
import java.io.FileReader;
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
	 * @param configFile the name of the configuration file
	 * @throws Exception if the configuration cannot be loaded
	 */
	public ConfigReader(String configFile) throws Exception {
		// check the configuration file
		if (configFile == null || configFile.length() == 0)
			throw new Exception("No configuration file name specified.");
		final File config = new File(configFile);
		if (!config.exists())
			throw new Exception("Configuration file " + configFile + " does not exist.");
		
		// read the configuration file
        final JsonObject root = new JsonParser().parse(new FileReader(config)).getAsJsonObject();
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