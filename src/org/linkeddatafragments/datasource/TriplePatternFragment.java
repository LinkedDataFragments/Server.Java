package org.linkeddatafragments.datasource;

/**
 * A Triple Pattern Fragment.
 * @author Ruben Verborgh
 */
public interface TriplePatternFragment extends LinkedDataFragment {
    /**
     * Gets the total number of triples in the fragment (can be an estimate).
     * @return the total number of triples
     */
    public long getTotalSize();
}
