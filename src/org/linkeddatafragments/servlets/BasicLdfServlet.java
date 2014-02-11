package org.linkeddatafragments.servlets;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.linkeddatafragments.config.ConfigReader;
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
 * Servlet that responds with a Basic Linked Data Fragment.
 * @author Ruben Verborgh
 */
public class BasicLdfServlet extends HttpServlet {
	private final static long serialVersionUID = 1L;
	private final static int TRIPLES_PER_PAGE = 100;
	private final static Pattern STRINGPATTERN = Pattern.compile("^\"(.*)\"(?:\\^\\^<(.*)>|@(.*))?$");
	
	private ConfigReader config;
	private HashMap<String, Model> dataSources = new HashMap<String, Model>();

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		try {
			config = new ConfigReader(servletConfig.getInitParameter("configFile"));
			for (Entry<String, String> dataSource : config.getDataSources().entrySet()) {
				final HDT hdt = HDTManager.mapIndexedHDT(dataSource.getValue(), null);
				final Model model = ModelFactory.createModelForGraph(new HDTGraph(hdt));
				dataSources.put(dataSource.getKey(), model);
			}
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
			final String dataSourceName = path.substring(1);
			final Model dataSource = dataSources.get(dataSourceName);
			if (dataSource == null)
				throw new Exception("data source not found");
			
			// create the output model
			final Model output = ModelFactory.createDefaultModel();
			output.setNsPrefixes(config.getPrefixes());
			
			// parse the subject, predicate, and object parameters
			final Resource subject = parseAsResource(request.getParameter("subject"), output);
			final Property predicate = parseAsProperty(request.getParameter("predicate"), output);
			final RDFNode object = parseAsNode(request.getParameter("object"), output);
			
			// add all statements with the given parameters to the output model
			final StmtIterator statements = dataSource.listStatements(subject, predicate, object);
			for (int i = 0; i < TRIPLES_PER_PAGE && statements.hasNext(); i++)
				output.add(statements.next());
			
			// serialize the output as Turtle
			response.setHeader("Server", "Linked Data Fragments Server");
			response.setHeader("Content-Type", "text/turtle");
			output.write(response.getWriter(), "Turtle");
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	/**
	 * Parses the given value as an RDF resource.
	 * @param value the value
	 * @param model the model
	 * @return the parsed value, or null if unspecified
	 */
	private Resource parseAsResource(String value, Model model) {
		final RDFNode subject = parseAsNode(value, model);
		return subject instanceof Resource ? (Resource)subject : null;
	}
	
	/**
	 * Parses the given value as an RDF property.
	 * @param value the value
	 * @param model the model
	 * @return the parsed value, or null if unspecified
	 */
	private Property parseAsProperty(String value, Model model) {
		final RDFNode predicate = parseAsNode(value, model);
		return predicate instanceof Resource ? model.createProperty(((Resource)predicate).getURI()) : null;
	}
	
	/**
	 * Parses the given value as an RDF node.
	 * @param value the value
	 * @param model the model
	 * @return the parsed value, or null if unspecified
	 */
	private RDFNode parseAsNode(String value, Model model) {
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
			return model.createResource(value.substring(1, value.length() - 1));
		// quotes indicate a string
		case '"':
			final Matcher matcher = STRINGPATTERN.matcher(value);
			if (matcher.matches()) {
				final String body = matcher.group(1);
				final String type = matcher.group(2);
				final String lang = matcher.group(3);
				if (type != null)
					return model.createTypedLiteral(body, type);
				if (lang != null)
					return model.createLiteral(body, lang);
				return model.createLiteral(body);
			}
			return null;
		// assume it's a URI without angular brackets
		default:
			return model.createResource(value);
		}
	}
}
