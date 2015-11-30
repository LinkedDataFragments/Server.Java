package org.linkeddatafragments.standalone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.linkeddatafragments.servlet.TriplePatternFragmentServlet;

/**
 * <p>
 * Use this class to run as a standalone service. Since it runs the
 * BasicLdfServlet, it is important to have a configuration file in the web.xml
 * as "configFile" init parameter. Default [baseDir]/config.xml</p>
 * <p>
 * This class runs an embedded Jetty servlet container. This way there is no
 * need for a separate servlet container such as Tomcat.</p>
 *
 * <p>
 * Copyright 2014 MMLab, UGent </p>
 *
 * @author Gerald Haesendonck
 * @author Miel Vander Sande
 * @author Bart Hanssens
 */
public class JettyServer {
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(JettyServer.class.getName() + " [config-example.json] [<options>]",
                    "Starts a standalone LDF Triple Pattern server. Options:", options, "");
    }
    
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", "help", false, "Print this help message and then exit.");
        options.addOption("p", "port", true, "The port the server listents to. The default is 8080.");
        
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        String config = null;
        if (!commandLine.getArgList().isEmpty()) {
            config = commandLine.getArgs()[0];
        }

        if (config == null || commandLine.hasOption('h')) {
            printHelp(options);
            System.exit(-1);
        }    
        
        int port = 8080;
        if (commandLine.hasOption('p')) {
            port = Integer.parseInt(commandLine.getOptionValue('p'));
        }

        // create a new (Jetty) server, and add a servlet handler
        Server server = new Server(port);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        // add the TriplePatternFragmentsServlet to the handler
        ServletHolder tpfServletHolder = new ServletHolder(new TriplePatternFragmentServlet());
        tpfServletHolder.setInitParameter(TriplePatternFragmentServlet.CFGFILE, config);
        handler.addServletWithMapping(tpfServletHolder, "/*");

        // start the server
        server.start();
        System.out.println("Started server, listening at port " + port);

        // The use of server.join() the will make the current thread join and wait until the server is done executing.
        // See http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
        server.join();
    }
}
