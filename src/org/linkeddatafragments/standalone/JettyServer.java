package org.linkeddatafragments.standalone;

import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.linkeddatafragments.servlet.BasicLdfServlet;

/**
 * <p> Use this class to run as a standalone service. Since it runs the BasicLdfServlet, it is important to have a
 * configuration file at [working dir]/../../conf/ldf-server.json. (That's the way it is right now)</p>
 * <p>This class runs an embedded Jetty servlet container. This way there is no need for a separate servlet container
 * such as Tomcat.</p>
 *
 * <p>
 * Copyright 2014 MMLab, UGent
 * </p?
 *
 * @author Gerald Haesendonck
 */
public class JettyServer {

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("h", "help", false, "Print this help message and then exit.");
		options.addOption("p", "port", true, "The port the server listents to. The default is 8080.");
		boolean printHelp = false;
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine commandLine = parser.parse(options, args);
			if (commandLine.hasOption('h')) {
				printHelp = true;
				return;
			}
			int port;
			if (commandLine.hasOption('p')) {
				port = Integer.parseInt(commandLine.getOptionValue('p'));
			} else {
				port = 8080;
			}

			// create a new (Jetty) server, and add a servlet handler
			Server server = new Server(port);
			ServletHandler handler = new ServletHandler();
			server.setHandler(handler);

			// add the BasicLdfServlet to the handler
			handler.addServletWithMapping(BasicLdfServlet.class, "/*");

			// start the server
			server.start();
			System.out.println("Started server, listening at port " + port);

			// The use of server.join() the will make the current thread join and wait until the server is done executing.
			// See http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
			server.join();

		} finally {
			if (printHelp) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(JettyServer.class.getName() + " [<options>]",  "Starts a standalone LDF Trpile Pattern server. Options:", options, "");
			}
		}
	}
}
