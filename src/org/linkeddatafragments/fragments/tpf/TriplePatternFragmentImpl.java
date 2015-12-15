package org.linkeddatafragments.fragments.tpf;

import org.linkeddatafragments.fragments.LinkedDataFragmentBase;
import org.linkeddatafragments.util.CommonResources;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Implementation of {@link TriplePatternFragment}.
 *
 * @author Ruben Verborgh
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
public class TriplePatternFragmentImpl extends LinkedDataFragmentBase
                                       implements TriplePatternFragment
{
    private final Model triples;
    private final long totalSize;

    /**
     * Creates an empty Triple Pattern Fragment.
     */
    public TriplePatternFragmentImpl( final String fragmentURL,
                                      final String datasetURL ) {
        this( null, 0L, fragmentURL, datasetURL, 1, true );
    }

    /**
     * Creates an empty Triple Pattern Fragment page.
     */
    public TriplePatternFragmentImpl( final String fragmentURL,
                                      final String datasetURL,
                                      final long pageNumber,
                                      final boolean isLastPage ) {
        this( null, 0L, fragmentURL, datasetURL, pageNumber, isLastPage );
    }

    /**
     * Creates a new Triple Pattern Fragment.
     * @param triples the triples (possibly partial)
     * @param totalSize the total size
     */
    public TriplePatternFragmentImpl( Model triples,
                                      long totalSize,
                                      final String fragmentURL,
                                      final String datasetURL,
                                      final long pageNumber,
                                      final boolean isLastPage ) {
        super( fragmentURL, datasetURL, pageNumber, isLastPage );
        this.triples = triples == null ? ModelFactory.createDefaultModel() : triples;
        this.totalSize = totalSize < 0 ? 0 : totalSize;
    }

    @Override
    public StmtIterator getTriples() {
        return triples.listStatements();
    }

    @Override
    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public void addMetadata( final Model model )
    {
        super.addMetadata( model );

        final Resource fragmentId = model.createResource( fragmentURL );

        final Literal totalTyped = model.createTypedLiteral( totalSize,
                                                      XSDDatatype.XSDinteger );
        final Literal limitTyped = model.createTypedLiteral( getMaxPageSize(),
                                                      XSDDatatype.XSDinteger );

        fragmentId.addLiteral( CommonResources.VOID_TRIPLES, totalTyped );
        fragmentId.addLiteral( CommonResources.HYDRA_TOTALITEMS, totalTyped );
        fragmentId.addLiteral( CommonResources.HYDRA_ITEMSPERPAGE, limitTyped );
    }

    @Override
    public void addControls( final Model model )
    {
        super.addControls( model );

        final Resource datasetId = model.createResource( getDatasetURI() );

        final Resource triplePattern = model.createResource();
        final Resource subjectMapping = model.createResource();
        final Resource predicateMapping = model.createResource();
        final Resource objectMapping = model.createResource();

        datasetId.addProperty( CommonResources.HYDRA_SEARCH, triplePattern );

        triplePattern.addProperty( CommonResources.HYDRA_TEMPLATE, getTemplate() );
        triplePattern.addProperty( CommonResources.HYDRA_MAPPING, subjectMapping );
        triplePattern.addProperty( CommonResources.HYDRA_MAPPING, predicateMapping );
        triplePattern.addProperty( CommonResources.HYDRA_MAPPING, objectMapping );

        subjectMapping.addProperty( CommonResources.HYDRA_VARIABLE, TriplePatternFragmentRequest.PARAMETERNAME_SUBJ );
        subjectMapping.addProperty( CommonResources.HYDRA_PROPERTY, CommonResources.RDF_SUBJECT );

        predicateMapping.addProperty( CommonResources.HYDRA_VARIABLE, TriplePatternFragmentRequest.PARAMETERNAME_PRED );
        predicateMapping.addProperty( CommonResources.HYDRA_PROPERTY, CommonResources.RDF_PREDICATE );
        
        objectMapping.addProperty( CommonResources.HYDRA_VARIABLE, TriplePatternFragmentRequest.PARAMETERNAME_OBJ );
        objectMapping.addProperty( CommonResources.HYDRA_PROPERTY, CommonResources.RDF_OBJECT );
    }

    public String getTemplate() {
        return datasetURL + "{?" +
               TriplePatternFragmentRequest.PARAMETERNAME_SUBJ + "," +
               TriplePatternFragmentRequest.PARAMETERNAME_PRED + "," +
               TriplePatternFragmentRequest.PARAMETERNAME_OBJ + "}";
    }

}
