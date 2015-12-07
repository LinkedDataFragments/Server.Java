package org.linkeddatafragments.datasource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import java.util.HashMap;
import java.util.Map;

/**
 * An Index data source provides an overview of all available datasets.
 *
 * @author Miel Vander Sande
 */
public class IndexDataSource extends DataSource {

    final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    final String DC = "http://purl.org/dc/terms/";
    final String VOID = "http://rdfs.org/ns/void#";

    private final Model model;

    public IndexDataSource(String baseUrl, HashMap<String, IDataSource> datasources) {
        super("Index", "List of all datasources");

        this.model = ModelFactory.createDefaultModel();

        for (Map.Entry<String, IDataSource> entry : datasources.entrySet()) {
            String datasourceName = entry.getKey();
            IDataSource datasource = entry.getValue();

            Resource datasourceUrl = new ResourceImpl(baseUrl + "/" + datasourceName);

            model.add(datasourceUrl, new PropertyImpl(RDF + "type"), VOID + "Dataset");
            model.add(datasourceUrl, new PropertyImpl(RDFS + "label"), datasource.getTitle());
            model.add(datasourceUrl, new PropertyImpl(DC + "title"), datasource.getTitle());
            model.add(datasourceUrl, new PropertyImpl(DC + "description"), datasource.getDescription());
        }
    }

    @Override
    public TriplePatternFragment getFragment(Resource subject, Property predicate, RDFNode object, long offset, long limit) {
        StmtIterator listStatements = this.model.listStatements(subject, predicate, object);
        Model result = ModelFactory.createDefaultModel();
        
        long index = 0;
        while (listStatements.hasNext() && index < offset) {
            listStatements.next();
            index++;
        }

        while (listStatements.hasNext() && index < (offset + limit)) {
            result.add(listStatements.next());
        }

        return new TriplePatternFragmentBase(result, result.size());
    }

}
