package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A data source of Basic Linked Data Fragments.
 * @author Ruben Verborgh
 */
public interface IDataSource {
    /**
     * Gets a page of the Basic Linked Data Fragment matching the specified triple pattern.
     * @param subject the subject (null to match any subject)
     * @param predicate the predicate (null to match any predicate)
     * @param object the object (null to match any object)
     * @param offset the triple index at which to start the page
     * @param limit the number of triples on the page
     * @return the first page of the fragment
     */
    public TriplePatternFragment getFragment(Resource subject, Property predicate, 
                                        RDFNode object, long offset, long limit);
    public String getTitle();
        
    public String getDescription();
}
