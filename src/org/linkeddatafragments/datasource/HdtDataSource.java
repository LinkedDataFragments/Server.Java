package org.linkeddatafragments.datasource;

import java.io.IOException;

import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.triples.IteratorTripleID;
import org.rdfhdt.hdt.triples.TripleID;
import org.rdfhdt.hdtjena.NodeDictionary;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * An HDT data source of Basic Linked Data Fragments.
 *
 * @author Ruben Verborgh
 */
public class HdtDataSource extends DataSource {

    private final HDT datasource;
    private final NodeDictionary dictionary;

    /**
     * Creates a new HdtDataSource.
     *
     * @param title title of the datasource
     * @param description datasource description
     * @param hdtFile the HDT datafile
     * @throws IOException if the file cannot be loaded
     */
    public HdtDataSource(String title, String description, String hdtFile) throws IOException {
        super(title, description);
        datasource = HDTManager.mapIndexedHDT(hdtFile, null);
        dictionary = new NodeDictionary(datasource.getDictionary());
    }

    @Override
    public TriplePatternFragment getFragment(Resource subject, Property predicate, RDFNode object, final long offset, final long limit) {
        checkBoundaries(offset, limit);

        // look up the result from the HDT datasource)
        int subjectId = subject == null ? 0 : dictionary.getIntID(subject.asNode(), TripleComponentRole.SUBJECT);
        int predicateId = predicate == null ? 0 : dictionary.getIntID(predicate.asNode(), TripleComponentRole.PREDICATE);
        int objectId = object == null ? 0 : dictionary.getIntID(object.asNode(), TripleComponentRole.OBJECT);
        
        if (subjectId < 0 || predicateId < 0 || objectId < 0) {
            return new TriplePatternFragmentBase();
        }
        
        final Model triples = ModelFactory.createDefaultModel();
        IteratorTripleID matches = datasource.getTriples().search(new TripleID(subjectId, predicateId, objectId));
        boolean hasMatches = matches.hasNext();
		
        if (hasMatches) {
            // try to jump directly to the offset
            boolean atOffset;
            if (matches.canGoTo()) {
                try {
                    matches.goTo(offset);
                    atOffset = true;
                } // if the offset is outside the bounds, this page has no matches
                catch (IndexOutOfBoundsException exception) {
                    atOffset = false;
                }
            } // if not possible, advance to the offset iteratively
            else {
                matches.goToStart();
                for (int i = 0; !(atOffset = i == offset) && matches.hasNext(); i++) {
                    matches.next();
                }
            }
            // try to add `limit` triples to the result model
            if (atOffset) {
                for (int i = 0; i < limit && matches.hasNext(); i++) {
                    triples.add(triples.asStatement(toTriple(matches.next())));
                }
            }
        }

        // estimates can be wrong; ensure 0 is returned if there are no results, 
        // and always more than actual results
        final long estimatedTotal = triples.size() > 0 
                ? Math.max(offset + triples.size() + 1, matches.estimatedNumResults())
                : hasMatches 
                    ? Math.max(matches.estimatedNumResults(), 1) 
                    : 0;

        // create the fragment
        return new TriplePatternFragment() {
            @Override
            public Model getTriples() {
                return triples;
            }

            @Override
            public long getTotalSize() {
                return estimatedTotal;
            }
        };
    }

    /**
     * Converts the HDT triple to a Jena Triple.
     *
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
