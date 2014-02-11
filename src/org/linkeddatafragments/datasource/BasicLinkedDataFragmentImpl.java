package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Base implementation of a Basic Linked Data Fragment.
 * @author Ruben Verborgh
 */
public class BasicLinkedDataFragmentImpl implements BasicLinkedDataFragment {
	private final Model triples;
	private final long totalSize;
	
	/**
	 * Create a new Basic Linked Data Fragment.
	 * @param triples the triples (possibly partial)
	 * @param totalSize the total size
	 */
	public BasicLinkedDataFragmentImpl(Model triples, long totalSize) {
		this.triples = triples;
		this.totalSize = totalSize;
	}

	@Override
	public Model getTriples() {
		return triples;
	}

	@Override
	public long getTotalSize() {
		return totalSize;
	}
}
