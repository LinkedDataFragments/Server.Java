package org.linkeddatafragments.fragments.tpf;

import java.util.NoSuchElementException;

import org.linkeddatafragments.fragments.LinkedDataFragmentBase;
import org.linkeddatafragments.util.CommonResources;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.NiceIterator;

/**
 * Base class for implementations of {@link TriplePatternFragment}.
 *
 * @author Ruben Verborgh
 * @author <a href="http://olafhartig.de">Olaf Hartig</a>
 */
abstract public class TriplePatternFragmentBase extends LinkedDataFragmentBase
                                       implements TriplePatternFragment
{
    private final long totalSize;

    /**
     * Creates an empty Triple Pattern Fragment.
     */
    public TriplePatternFragmentBase( final String fragmentURL,
                                      final String datasetURL ) {
        this( 0L, fragmentURL, datasetURL, 1, true );
    }

    /**
     * Creates an empty Triple Pattern Fragment page.
     */
    public TriplePatternFragmentBase( final String fragmentURL,
                                      final String datasetURL,
                                      final long pageNumber,
                                      final boolean isLastPage ) {
        this( 0L, fragmentURL, datasetURL, pageNumber, isLastPage );
    }

    /**
     * Creates a new Triple Pattern Fragment.
     * @param totalSize the total size
     */
    public TriplePatternFragmentBase( long totalSize,
                                      final String fragmentURL,
                                      final String datasetURL,
                                      final long pageNumber,
                                      final boolean isLastPage ) {
        super( fragmentURL, datasetURL, pageNumber, isLastPage );
        this.totalSize = totalSize < 0L ? 0L : totalSize;
    }

    @Override
    public StmtIterator getTriples() {
        if ( totalSize == 0L )
            return emptyStmtIterator;
        else
            return getNonEmptyStmtIterator();
    }

    abstract protected StmtIterator getNonEmptyStmtIterator();

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


    public static final StmtIterator emptyStmtIterator = new EmptyStmtIterator();

    public static class EmptyStmtIterator
        extends NiceIterator<Statement>
        implements StmtIterator
    {
        public Statement nextStatement() { throw new NoSuchElementException(); }
    }

}
