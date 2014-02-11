package org.linkeddatafragments.servlets;

import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that responds with a Linked Data Fragment.
 * @author Ruben Verborgh
 */
public class FragmentServlet extends HttpServlet {

	@Override
	public void init(ServletConfig config) {
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			String path = request.getRequestURI().substring(request.getContextPath().length());
			PrintWriter writer = response.getWriter();
			writer.println(path);
			writer.close();
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
