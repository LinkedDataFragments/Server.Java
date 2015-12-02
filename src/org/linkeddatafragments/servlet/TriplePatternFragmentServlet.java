package org.linkeddatafragments.servlet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.CharEncoding;

import org.apache.http.HttpHeaders;
import org.apache.http.client.utils.URIBuilder;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import com.google.gson.JsonObject;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.InvalidPropertyURIException;

import org.linkeddatafragments.config.ConfigReader;
import org.linkeddatafragments.datasource.DataSourceFactory;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.TriplePatternFragment;
import org.linkeddatafragments.exceptions.DataSourceException;
import org.linkeddatafragments.util.CommonResources;
import org.linkeddatafragments.util.MIMEParse;

/**
 * Servlet that responds with a Basic Linked Data Fragment.
 *
 * @author Ruben Verborgh
 * @author Bart Hanssens
 */
public class TriplePatternFragmentServlet extends HttpServlet {
    private final static long serialVersionUID = 1L;
    
    // Parameters
    public final static String CFGFILE = "configFile";
    public final static String SUBJ = "subject";
    public final static String PRED = "predicate";
    public final static String OBJ = "object";
    public final static String PAGE = "page";
    
    
    private final static Pattern STRINGPATTERN = 
                    Pattern.compile("^\"(.*)\"(?:@(.*)|\\^\\^<?([^<>]*)>?)?$");
    private final static TypeMapper TYPES = TypeMapper.getInstance();
    private final static long TRIPLESPERPAGE = 100;

    private ConfigReader config;
    private final HashMap<String, IDataSource> dataSources = new HashMap<>();
    private final Collection<String> mimeTypes = new ArrayList<>();

    
    private File getConfigFile(ServletConfig config) throws IOException {
        String path = config.getServletContext().getRealPath("/");
        if (path == null) {
            // this can happen when running standalone
            path = System.getProperty("user.dir");
        }
        File cfg = new File(path, "config-example.json");
        if (config.getInitParameter(CFGFILE) != null) {
            cfg = new File(config.getInitParameter(CFGFILE));
        }
        if (!cfg.exists()) {
            throw new IOException("Configuration file " + cfg + " not found.");
        }
        if (!cfg.isFile()) {
            throw new IOException("Configuration file " + cfg + " is not a file.");
        }
        return cfg;
    }
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        try {
            // load the configuration
            File configFile = getConfigFile(servletConfig);
            config = new ConfigReader(new FileReader(configFile));
            
            for (Entry<String, JsonObject> dataSource : config.getDataSources().entrySet()) {
                dataSources.put(dataSource.getKey(), DataSourceFactory.create(dataSource.getValue()));
            }
           // register content types
            mimeTypes.add(Lang.TTL.getHeaderString());
            mimeTypes.add(Lang.JSONLD.getHeaderString());
            mimeTypes.add(Lang.NTRIPLES.getHeaderString()); 
            mimeTypes.add(Lang.RDFXML.getHeaderString() );
        } catch (IOException | DataSourceException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Get the datasource
     * 
     * @param request
     * @return
     * @throws IOException 
     */
    private IDataSource getDataSource(HttpServletRequest request) throws IOException {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();

        String path = contextPath == null 
                                ? requestURI 
                                : requestURI.substring(contextPath.length());
        String dataSourceName = path.substring(1);
        IDataSource dataSource = dataSources.get(dataSourceName);
        if (dataSource == null) {
            throw new IOException("Data source not found.");
        }
        return dataSource;
    }
    
    /**
     * Get dataset url
     * 
     * @param request
     * @return 
     */
    private String getDatasetUrl(HttpServletRequest request) {
        String hostName = request.getHeader(HttpHeaders.SERVER);
        if (hostName == null) {
            hostName = request.getServerName();
        }
        return request.getScheme() + "://" + hostName + request.getRequestURI();
    }
    
    /**
     * Add total and limit
     * 
     * @param output
     * @param fragmentId
     * @param total
     * @param limit 
     */
    private void addMeta(Model output, Resource datasetId, Resource fragmentId, 
                                                        long total, long limit) {
        output.add(datasetId, CommonResources.RDF_TYPE, CommonResources.VOID_DATASET);
        output.add(datasetId, CommonResources.RDF_TYPE, CommonResources.HYDRA_COLLECTION);
        output.add(datasetId, CommonResources.VOID_SUBSET, fragmentId);
            
        output.add(fragmentId, CommonResources.RDF_TYPE, CommonResources.HYDRA_COLLECTION);
        output.add(fragmentId, CommonResources.RDF_TYPE, CommonResources.HYDRA_PAGEDCOLLECTION);
        
        Literal totalTyped = output.createTypedLiteral(total, XSDDatatype.XSDinteger);
        Literal limitTyped = output.createTypedLiteral(limit, XSDDatatype.XSDinteger);

        output.add(fragmentId, CommonResources.VOID_TRIPLES, totalTyped);
        output.add(fragmentId, CommonResources.HYDRA_TOTALITEMS, totalTyped);
        output.add(fragmentId, CommonResources.HYDRA_ITEMSPERPAGE, limitTyped);
    }
    

    /**
     * Add reference to first/previous/next page
     * 
     * @param output
     * @param fragmentId
     * @param fragmentUrl
     * @param total
     * @param limit
     * @param offset
     * @param page
     * @throws URISyntaxException 
     */
    private void addPages(Model output, Resource fragmentId, String fragmentUrl, 
                long total, long limit, long offset, long page) throws URISyntaxException { 
        URIBuilder pagedUrl = new URIBuilder(fragmentUrl);
        
        pagedUrl.setParameter(PAGE, "1");
        output.add(fragmentId, CommonResources.HYDRA_FIRSTPAGE, 
                                    output.createResource(pagedUrl.toString()));
        if (offset > 0) {
            pagedUrl.setParameter(PAGE, Long.toString(page - 1));
            output.add(fragmentId, CommonResources.HYDRA_PREVIOUSPAGE, 
                                    output.createResource(pagedUrl.toString()));
        }
        if (offset + limit < total) {
            pagedUrl.setParameter(PAGE, Long.toString(page + 1));
            output.add(fragmentId, CommonResources.HYDRA_NEXTPAGE, 
                                    output.createResource(pagedUrl.toString()));
        }
    }
     
    /**
     * Add controls to output
     * 
     * @param output
     * @param datasetId
     * @param datasetUrl 
     */
    private void addControls(Model output, Resource datasetId, String datasetUrl) {
        // add controls
        Resource triplePattern = output.createResource();
        Resource subjectMapping = output.createResource();
        Resource predicateMapping = output.createResource();
        Resource objectMapping = output.createResource();

        output.add(datasetId, CommonResources.HYDRA_SEARCH, triplePattern);
        output.add(triplePattern, CommonResources.HYDRA_TEMPLATE, output.createLiteral(datasetUrl + "{?subject,predicate,object}"));
        output.add(triplePattern, CommonResources.HYDRA_MAPPING, subjectMapping);
        output.add(triplePattern, CommonResources.HYDRA_MAPPING, predicateMapping);
        output.add(triplePattern, CommonResources.HYDRA_MAPPING, objectMapping);
        
        output.add(subjectMapping, CommonResources.HYDRA_VARIABLE, output.createLiteral(SUBJ));
        output.add(subjectMapping, CommonResources.HYDRA_PROPERTY, CommonResources.RDF_SUBJECT);
        
        output.add(predicateMapping, CommonResources.HYDRA_VARIABLE, output.createLiteral(PRED));
        output.add(predicateMapping, CommonResources.HYDRA_PROPERTY, CommonResources.RDF_PREDICATE);
        output.add(objectMapping, CommonResources.HYDRA_VARIABLE, output.createLiteral(OBJ));
        
        output.add(objectMapping, CommonResources.HYDRA_PROPERTY, CommonResources.RDF_OBJECT);
    }
    
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            IDataSource dataSource = getDataSource(request);
            
            // query the fragment
            Resource subject = parseAsResource(request.getParameter(SUBJ));
            Property predicate = parseAsProperty(request.getParameter(PRED));
            RDFNode object = parseAsNode(request.getParameter(OBJ));
            
            long page = Math.max(1, parseAsInteger(request.getParameter(PAGE)));
            long limit = TRIPLESPERPAGE;
            long offset = limit * (page - 1);
        
            TriplePatternFragment fragment = 
                    dataSource.getFragment(subject, predicate, object, offset, limit);

            // fill the output model
            Model output = fragment.getTriples();
            output.setNsPrefixes(config.getPrefixes());
            
            // do conneg
            String bestMatch = MIMEParse.bestMatch(mimeTypes, request.getHeader("Accept"));
            Lang contentType = RDFLanguages.contentTypeToLang(bestMatch);

            // serialize the output
            response.setHeader("Server", "Linked Data Fragments Server");
            response.setContentType(bestMatch);
            response.setCharacterEncoding("utf-8");
            RDFDataMgr.write(response.getOutputStream(), output, contentType);

            // add dataset metadata
            String datasetUrl = getDatasetUrl(request);
            Resource datasetId = output.createResource(datasetUrl + "#dataset");
            
            String query = request.getQueryString();
            String fragmentUrl = query == null ? datasetUrl : (datasetUrl + "?" + query);
            Resource fragmentId = output.createResource(fragmentUrl);

            long total = fragment.getTotalSize();
            
            addMeta(output, datasetId, fragmentId, total, limit);
            addPages(output, fragmentId, fragmentUrl, total, limit, offset, page);           
            addControls(output, datasetId, datasetUrl);
            
            // serialize the output as Turtle
            response.setHeader(HttpHeaders.SERVER, "Linked Data Fragments Server");
            response.setContentType("text/turtle");
            response.setCharacterEncoding(CharEncoding.UTF_8);
            
            output.write(response.getWriter(), "Turtle", fragmentUrl);
        } catch (IOException | URISyntaxException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Parses the given value as an integer.
     *
     * @param value the value
     * @return the parsed value
     */
    private int parseAsInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * Parses the given value as an RDF resource.
     *
     * @param value the value
     * @return the parsed value, or null if unspecified
     */
    private Resource parseAsResource(String value) {
        RDFNode subject = parseAsNode(value);
        return subject == null || subject instanceof Resource 
                ? (Resource) subject 
                : CommonResources.INVALID_URI;
    }

    /**
     * Parses the given value as an RDF property.
     *
     * @param value the value
     * @return the parsed value, or null if unspecified
     */
    private Property parseAsProperty(String value) {
        RDFNode predicateNode = parseAsNode(value);
        if (predicateNode instanceof Resource) {
            try {
                return ResourceFactory.createProperty(((Resource) predicateNode).getURI());
            } catch (InvalidPropertyURIException ex) {
                return CommonResources.INVALID_URI;
            }
        }
        return predicateNode == null ? null : CommonResources.INVALID_URI;
    }

    /**
     * Parses the given value as an RDF node.
     *
     * @param value the value
     * @return the parsed value, or null if unspecified
     */
    private RDFNode parseAsNode(String value) {
        // nothing or empty indicates an unknown
        if (value == null || value.isEmpty()) {
            return null;
        }
        // find the kind of entity based on the first character
        char firstChar = value.charAt(0);
        switch (firstChar) {
            // variable or blank node indicates an unknown
            case '?':
            case '_':
                return null;
            // angular brackets indicate a URI
            case '<':
                return ResourceFactory.createResource(value.substring(1, value.length() - 1));
            // quotes indicate a string
            case '"':
                Matcher matcher = STRINGPATTERN.matcher(value);
                if (matcher.matches()) {
                    String body = matcher.group(1);
                    String lang = matcher.group(2);
                    String type = matcher.group(3);
                    if (lang != null) {
                        return ResourceFactory.createLangLiteral(body, lang);
                    }
                    if (type != null) {
                        return ResourceFactory.createTypedLiteral(body, TYPES.getSafeTypeByName(type));
                    }
                    return ResourceFactory.createPlainLiteral(body);
                }
                return CommonResources.INVALID_URI;
            // assume it's a URI without angular brackets
            default:
                return ResourceFactory.createResource(value);
        }
    }
}
