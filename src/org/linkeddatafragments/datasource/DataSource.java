package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A data source of Basic Linked Data Fragments.
 * @author Ruben Verborgh
 */
public interface DataSource {
	/**
	 * Gets the Basic Linked Data Fragment matching the specified triple pattern.
	 * @param subject the subject (null to match any subject)
	 * @param predicate the predicate (null to match any predicate)
	 * @param object the object (null to match any object)
	 * @return the first page of the fragment
	 */
	public BasicLinkedDataFragment getFragment(Resource subject, Property predicate, RDFNode object);
}
