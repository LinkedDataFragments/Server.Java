package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * A Basic Linked Data Fragment.
 * @author Ruben Verborgh
 */
public interface BasicLinkedDataFragment {
	/**
	 * Gets the data of this fragment (possibly only partial).
	 * @return the data as triples
	 */
	public Model getTriples();
	
	/**
	 * Gets the total number of triples in the fragment (can be an estimate).
	 * @return the total number of triples
	 */
	public long getTotalSize();
}
