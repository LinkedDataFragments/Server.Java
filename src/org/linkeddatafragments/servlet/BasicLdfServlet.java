package org.linkeddatafragments.servlet;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;
import org.linkeddatafragments.config.ConfigReader;
import org.linkeddatafragments.datasource.BasicLinkedDataFragment;
import org.linkeddatafragments.datasource.DataSource;
import org.linkeddatafragments.datasource.HdtDataSource;

import static org.linkeddatafragments.util.CommonResources.*;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.InvalidPropertyURIException;

/**
 * Servlet that responds with a Basic Linked Data Fragment.
 * @author Ruben Verborgh
 */
public class BasicLdfServlet extends HttpServlet {
	private final static long serialVersionUID = 1L;
	private final static Pattern STRINGPATTERN = Pattern.compile("^\"(.*)\"(?:@(.*)|\\^\\^<(.*)>)?$");
	private final static TypeMapper types = TypeMapper.getInstance();
	private final static long TRIPLESPERPAGE = 100;
	
	private ConfigReader config;
	private HashMap<String, DataSource> dataSources = new HashMap<String, DataSource>();

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		try {
			// find the configuration file
			final File applicationPath = new File(servletConfig.getServletContext().getRealPath("/"));
			final File serverHome = applicationPath.getParentFile().getParentFile();
		    final File configFile = new File(serverHome, "conf/ldf-server.json");
			if (!configFile.exists())
				throw new Exception("Configuration file " + configFile + " not found.");
			
			// load the configuration
			config = new ConfigReader(new FileReader(configFile));
			for (Entry<String, String> dataSource : config.getDataSources().entrySet())
				dataSources.put(dataSource.getKey(), new HdtDataSource(dataSource.getValue()));
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			// find the data source
			final String path = request.getRequestURI().substring(request.getContextPath().length());
			final String query = request.getQueryString();
			final String dataSourceName = path.substring(1);
			final DataSource dataSource = dataSources.get(dataSourceName);
			if (dataSource == null)
				throw new Exception("Data source not found.");
			
			// query the fragment
			final Resource subject = parseAsResource(request.getParameter("subject"));
			final Property predicate = parseAsProperty(request.getParameter("predicate"));
			final RDFNode object = parseAsNode(request.getParameter("object"));
			final long page = Math.max(1, parseAsInteger(request.getParameter("page")));
			final long limit = TRIPLESPERPAGE, offset = limit * (page - 1);
			final BasicLinkedDataFragment fragment = dataSource.getFragment(subject, predicate, object, offset, limit);
			
			// fill the output model
			final Model output = fragment.getTriples();
			final boolean isEmpty = output.size() == 0;
			output.setNsPrefixes(config.getPrefixes());
			
			// add dataset metadata
			final String hostName = request.getHeader("Host");
			final String datasetUrl = request.getScheme() + "://" +
								      (hostName == null ? request.getServerName() : hostName) + request.getRequestURI();
			final String fragmentUrl = query == null ? datasetUrl : (datasetUrl + "?" + query);
			final Resource datasetId = output.createResource(datasetUrl + "#dataset");
			final Resource fragmentId = output.createResource(fragmentUrl);
			output.add(datasetId, RDF_TYPE, VOID_DATASET);
			output.add(datasetId, RDF_TYPE, HYDRA_COLLECTION);
			output.add(datasetId, VOID_SUBSET, fragmentId);
			
			// add fragment metadata
			output.add(fragmentId, RDF_TYPE, HYDRA_COLLECTION);
			output.add(fragmentId, RDF_TYPE, HYDRA_PAGEDCOLLECTION);
			final Literal total = output.createTypedLiteral(fragment.getTotalSize(), XSDDatatype.XSDinteger);
			output.add(fragmentId, VOID_TRIPLES, total);
			output.add(fragmentId, HYDRA_TOTALITEMS, total);
			output.add(fragmentId, HYDRA_ITEMSPERPAGE, output.createTypedLiteral(limit, XSDDatatype.XSDinteger));
			
			// add pages
			final URIBuilder pagedUrl = new URIBuilder(fragmentUrl);
			pagedUrl.setParameter("page", "1");
			output.add(fragmentId, HYDRA_FIRSTPAGE, output.createResource(pagedUrl.toString()));
			if (offset > 0) {
				pagedUrl.setParameter("page", Long.toString(page - 1));
				output.add(fragmentId, HYDRA_PREVIOUSPAGE, output.createResource(pagedUrl.toString()));
			}
			if (offset + limit < fragment.getTotalSize()) {
				pagedUrl.setParameter("page", Long.toString(page + 1));
				output.add(fragmentId, HYDRA_NEXTPAGE, output.createResource(pagedUrl.toString()));
			}
			
			// add controls
			final Resource triplePattern    = output.createResource();
			final Resource subjectMapping   = output.createResource();
			final Resource predicateMapping = output.createResource();
			final Resource objectMapping    = output.createResource();
			output.add(datasetId,        HYDRA_SEARCH,   triplePattern);
			output.add(triplePattern,    HYDRA_TEMPLATE, output.createLiteral(datasetUrl + "{?subject,predicate,object}"));
			output.add(triplePattern,    HYDRA_MAPPING,  subjectMapping);
			output.add(triplePattern,    HYDRA_MAPPING,  predicateMapping);
			output.add(triplePattern,    HYDRA_MAPPING,  objectMapping);
			output.add(subjectMapping,   HYDRA_VARIABLE, output.createLiteral("subject"));
			output.add(subjectMapping,   HYDRA_PROPERTY, RDF_SUBJECT);
			output.add(predicateMapping, HYDRA_VARIABLE, output.createLiteral("predicate"));
			output.add(predicateMapping, HYDRA_PROPERTY, RDF_PREDICATE);
			output.add(objectMapping,    HYDRA_VARIABLE, output.createLiteral("object"));
			output.add(objectMapping,    HYDRA_PROPERTY, RDF_OBJECT);
			
			// serialize the output as Turtle
			response.setStatus(isEmpty ? 404 : 200);
			response.setHeader("Server", "Linked Data Fragments Server");
			response.setContentType("text/turtle");
			response.setCharacterEncoding("utf-8");
			output.write(response.getWriter(), "Turtle", fragmentUrl);
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	/**
	 * Parses the given value as an integer.
	 * @param value the value
	 * @return the parsed value
	 */
	private int parseAsInteger(String value) {
		try { return Integer.parseInt(value); }
		catch (NumberFormatException ex) { return 0; }
	}
	
	/**
	 * Parses the given value as an RDF resource.
	 * @param value the value
	 * @return the parsed value, or null if unspecified
	 */
	private Resource parseAsResource(String value) {
		final RDFNode subject = parseAsNode(value);
		return subject == null || subject instanceof Resource ? (Resource)subject : INVALID_URI;
	}
	
	/**
	 * Parses the given value as an RDF property.
	 * @param value the value
	 * @return the parsed value, or null if unspecified
	 */
	private Property parseAsProperty(String value) {
		final RDFNode predicateNode = parseAsNode(value);
		if (predicateNode instanceof Resource) {
			try { return ResourceFactory.createProperty(((Resource)predicateNode).getURI()); }
			catch (InvalidPropertyURIException ex) { return INVALID_URI; }
		}
		return predicateNode == null ? null : INVALID_URI;
	}
	
	/**
	 * Parses the given value as an RDF node.
	 * @param value the value
	 * @return the parsed value, or null if unspecified
	 */
	private RDFNode parseAsNode(String value) {
		// nothing or empty indicates an unknown
		if (value == null || value.length() == 0)
			return null;
		// find the kind of entity based on the first character
		final char firstChar = value.charAt(0);
		switch(firstChar) {
		// variable or blank node indicates an unknown
		case '?':
		case '_':
			return null;
		// angular brackets indicate a URI
		case '<':
			return ResourceFactory.createResource(value.substring(1, value.length() - 1));
		// quotes indicate a string
		case '"':
			final Matcher matcher = STRINGPATTERN.matcher(value);
			if (matcher.matches()) {
				final String body = matcher.group(1);
				final String lang = matcher.group(2);
				final String type = matcher.group(3);
				if (lang != null)
					return ResourceFactory.createLangLiteral(body, lang);
				if (type != null)
					return ResourceFactory.createTypedLiteral(body, types.getSafeTypeByName(type));
				return ResourceFactory.createPlainLiteral(body);
			}
			return null;
		// assume it's a URI without angular brackets
		default:
			return ResourceFactory.createResource(value);
		}
	}
}
