package org.linkeddatafragments.fragments.TPF;

import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;

/**
 * Represents a request of a Triple Pattern Fragment (TPF).
 *
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public interface TriplePatternFragmentRequest extends LinkedDataFragmentRequest
{
    public final static String PARAMETERNAME_SUBJ = "subject";
    public final static String PARAMETERNAME_PRED = "predicate";
    public final static String PARAMETERNAME_OBJ = "object";

    /**
     * Returns the subject position of the requested triple pattern (or null,
     * in which case the requested triple pattern has an unnamed variable as
     * subject).
     */
    String getSubject();

    /**
     * Returns the predicate position of the requested triple pattern (or null,
     * in which case the requested triple pattern has an unnamed variable as
     * predicate).
     */
    String getPredicate();

    /**
     * Returns the object position of the requested triple pattern (or null,
     * in which case the requested triple pattern has an unnamed variable as
     * object).
     */
    String getObject();
}
