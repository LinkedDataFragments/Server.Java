package org.linkeddatafragments.datasource;

import java.io.IOException;

import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdtjena.HDTGraph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * An HDT data source of Basic Linked Data Fragments.
 * @author Ruben Verborgh
 */
public class HdtDataSource implements DataSource {
	private final static int TRIPLES_LIMIT = 100;
	private Model data;
	
	/**
	 * Creates a new HdtDataSource.
	 * @param hdtFile the HDT datafile
	 * @throws IOException if the file cannot be loaded
	 */
	public HdtDataSource(String hdtFile) throws IOException {
		final HDT hdt = HDTManager.mapIndexedHDT(hdtFile, null);
		data = ModelFactory.createModelForGraph(new HDTGraph(hdt));
	}

	@Override
	public BasicLinkedDataFragment getFragment(final Resource subject, final Property predicate, final RDFNode object) {
		return new BasicLinkedDataFragment() {
			@Override
			public Model getTriples() {
				final Model triples = ModelFactory.createDefaultModel();
				final StmtIterator statements = data.listStatements(subject, predicate, object);
				for (int i = 0; i < TRIPLES_LIMIT && statements.hasNext(); i++)
					triples.add(statements.next());
				return triples;
			}
			
			@Override
			public int getTotalSize() {
				return 0;
			}
		};
	}
}
