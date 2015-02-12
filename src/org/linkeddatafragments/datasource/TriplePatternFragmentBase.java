package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Base implementation of a Basic Linked Data Fragment.
 * @author Ruben Verborgh
 */
public class TriplePatternFragmentBase implements TriplePatternFragment {
	private final Model triples;
	private final long totalSize;
	
	/**
	 * Creates an empty Basic Linked Data Fragment.
	 */
	public TriplePatternFragmentBase() {
		this(null, 0);
	}
	
	/**
	 * Creates a new Basic Linked Data Fragment.
	 * @param triples the triples (possibly partial)
	 * @param totalSize the total size
	 */
	public TriplePatternFragmentBase(Model triples, long totalSize) {
		this.triples = triples == null ? ModelFactory.createDefaultModel() : triples;
		this.totalSize = totalSize < 0 ? 0 : totalSize;
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
