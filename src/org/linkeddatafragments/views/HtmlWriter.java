/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.linkeddatafragments.views;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.fragments.LinkedDataFragment;

/**
 *
 * @author mielvandersande
 */
public class HtmlWriter {
    private final Configuration cfg;

    public HtmlWriter() throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/views"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }
   
    public void write(OutputStream outputStream, HashMap<String, IDataSource> dataSources, IDataSource datasource, LinkedDataFragment fragment) throws IOException, TemplateException{
        /* Get the template (uses cache internally) */
        Template temp = cfg.getTemplate("index.ftl.html");
        
        Map data = new HashMap();
        data.put("assetsPath", "assets/");
        data.put("header", datasource.getTitle());
        data.put("datasources", dataSources);
        data.put("content", "");
        data.put("date", new Date());

        
        data.put("controls", fragment.getControls());
        data.put("metadata", fragment.getMetadata());
        data.put("triples", fragment.getTriples());
       

        /* Merge data-model with template */
        Writer out = new OutputStreamWriter(outputStream);
        temp.process(data, out);
    }
}
