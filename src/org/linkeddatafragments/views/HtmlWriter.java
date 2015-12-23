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
import javax.servlet.ServletOutputStream;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.index.IndexDataSource;
import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;

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
   
    public void write(OutputStream outputStream, IDataSource datasource, LinkedDataFragment fragment,  LinkedDataFragmentRequest ldfRequest) throws IOException, TemplateException{
        /* Get the template (uses cache internally) */
        Template temp = cfg.getTemplate("index.ftl.html");
        
        Map data = new HashMap();
        data.put("assetsPath", "assets/");
        data.put("header", datasource.getTitle());
        if (datasource instanceof IndexDataSource)
            data.put("datasources", ((IndexDataSource) datasource).getDatasources());
        data.put("content", "");
        data.put("date", new Date());
        

        data.put("datasourceUrl", ldfRequest.getDatasetURL());
        data.put("datasource", datasource);
        data.put("controls", fragment.getControls());
        data.put("metadata", fragment.getMetadata());
        data.put("triples", fragment.getTriples());
       

        /* Merge data-model with template */
        Writer out = new OutputStreamWriter(outputStream);
        temp.process(data, out);
    }
}
