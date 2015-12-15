package org.linkeddatafragments.fragments.TPF;

import org.linkeddatafragments.fragments.LinkedDataFragment;

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
