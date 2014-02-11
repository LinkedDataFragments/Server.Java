package org.linkeddatafragments.util;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

@SuppressWarnings("javadoc")
public class CommonResources {
	public final static String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public final static Property RDF_TYPE = createProperty(RDF + "type");
	
	public final static String VOID = "http://rdfs.org/ns/void#";
	public final static Property VOID_TRIPLES = createProperty(VOID + "triples");
	
	private final static Property createProperty(String uri) {
		return ResourceFactory.createProperty(uri);
	}
}
