package org.linkeddatafragments.datasource;

import java.io.IOException;

import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleID;
import org.rdfhdt.hdt.triples.TripleID;
import org.rdfhdt.hdtjena.NodeDictionary;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * An HDT data source of Basic Linked Data Fragments.
 * @author Ruben Verborgh
 */
public class HdtDataSource implements DataSource {
	private final static int TRIPLES_LIMIT = 100;
	private final HDT datasource;
	private final NodeDictionary dictionary;
	
	/**
	 * Creates a new HdtDataSource.
	 * @param hdtFile the HDT datafile
	 * @throws IOException if the file cannot be loaded
	 */
	public HdtDataSource(String hdtFile) throws IOException {
		datasource = HDTManager.mapIndexedHDT(hdtFile, null);
		dictionary = new NodeDictionary(datasource.getDictionary());
	}

	@Override
	public BasicLinkedDataFragment getFragment(Resource subject, Property predicate, RDFNode object) {
		// look up the result from the HDT datasource
        final int subjectId = subject == null ? 0 : dictionary.getIntID(subject.asNode(), TripleComponentRole.SUBJECT);
        final int predicateId = predicate == null ? 0 : dictionary.getIntID(predicate.asNode(), TripleComponentRole.PREDICATE);
        final int objectId = object == null ? 0 : dictionary.getIntID(object.asNode(), TripleComponentRole.OBJECT);
        if (subjectId < 0 || predicateId < 0 || objectId < 0)
        	return new BasicLinkedDataFragmentBase();
		final IteratorTripleID result = datasource.getTriples().search(new TripleID(subjectId, predicateId, objectId));
		
		// create the fragment
		return new BasicLinkedDataFragment() {
			@Override
			public Model getTriples() {
				final Model triples = ModelFactory.createDefaultModel();
				result.goToStart();
				for (int i = 0; i < TRIPLES_LIMIT && result.hasNext(); i++)
					triples.add(triples.asStatement(toTriple(result.next())));
				return triples;
			}
			
			@Override
			public long getTotalSize() {
				return result.estimatedNumResults();
			}
		};
	}

	/**
	 * Converts the HDT triple to a Jena Triple.
	 * @param tripleId the HDT triple
	 * @return the Jena triple
	 */
	private Triple toTriple(TripleID tripleId) {
		return new Triple(
			dictionary.getNode(tripleId.getSubject(), TripleComponentRole.SUBJECT),
			dictionary.getNode(tripleId.getPredicate(), TripleComponentRole.PREDICATE),
			dictionary.getNode(tripleId.getObject(), TripleComponentRole.OBJECT)
		);
	}
}
