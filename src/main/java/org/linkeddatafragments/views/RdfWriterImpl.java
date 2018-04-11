package org.linkeddatafragments.views;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.jena.query.ARQ;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.fragments.ILinkedDataFragment;
import org.linkeddatafragments.fragments.ILinkedDataFragmentRequest;

/**
 *  Serializes an {@link ILinkedDataFragment} to an RDF format
 * 
 * @author Miel Vander Sande
 */
class RdfWriterImpl extends LinkedDataFragmentWriterBase implements ILinkedDataFragmentWriter {

    private final Lang contentType;

    public RdfWriterImpl(Map<String, String> prefixes, HashMap<String, IDataSource> datasources, String mimeType) {
        super(prefixes, datasources);
        this.contentType = RDFLanguages.contentTypeToLang(mimeType);
        ARQ.init();
    }

    @Override
    public void writeNotFound(ServletOutputStream outputStream, HttpServletRequest request) throws IOException {
        outputStream.println(request.getRequestURL().toString() + " not found!");
        outputStream.close();
    }

    @Override
    public void writeError(ServletOutputStream outputStream, Exception ex) throws IOException {
        outputStream.println(ex.getMessage());
        outputStream.close();
    }

    @Override
    public void writeFragment(ServletOutputStream outputStream, IDataSource datasource, ILinkedDataFragment fragment, ILinkedDataFragmentRequest ldfRequest) throws Exception {
        final Model output = ModelFactory.createDefaultModel();
        output.setNsPrefixes(getPrefixes());
        output.add(fragment.getMetadata());
        output.add(fragment.getTriples());
        output.add(fragment.getControls());
        RDFDataMgr.write(outputStream, output, contentType);
    }

}
