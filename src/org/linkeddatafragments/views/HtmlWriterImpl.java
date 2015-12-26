package org.linkeddatafragments.views;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.index.IndexDataSource;
import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;
import org.linkeddatafragments.fragments.tpf.TriplePatternFragmentRequest;

/**
 *
 * @author mielvandersande
 */
public class HtmlWriterImpl extends LinkedDataFragmentWriterBase implements LinkedDataFragmentWriter {
    private final Configuration cfg;
    
    private final Template indexTemplate;
    private final Template datasourceTemplate;
    private final Template notfoundTemplate;
    private final Template errorTemplate;
    

    public HtmlWriterImpl(Map<String, String> prefixes, HashMap<String, IDataSource> datasources) throws IOException {
        super(prefixes, datasources);
        
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/views"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        
        indexTemplate = cfg.getTemplate("index.ftl.html");
        datasourceTemplate = cfg.getTemplate("datasource.ftl.html");
        notfoundTemplate = cfg.getTemplate("notfound.ftl.html");
        errorTemplate = cfg.getTemplate("error.ftl.html");
    }
   
    @Override
    public void writeFragment(ServletOutputStream outputStream, IDataSource datasource, LinkedDataFragment fragment,  LinkedDataFragmentRequest ldfRequest) throws IOException, TemplateException{
        Map data = new HashMap();
        
        // base.ftl.html
        data.put("assetsPath", "assets/");
        data.put("header", datasource.getTitle());
        data.put("date", new Date());
        
        // fragment.ftl.html
        data.put("datasourceUrl", ldfRequest.getDatasetURL());
        data.put("datasource", datasource);
        data.put("controls", fragment.getControls());
        data.put("metadata", fragment.getMetadata());
        data.put("triples", fragment.getTriples());
        data.put("datasources", getDatasources());
        
        Map query = new HashMap();
        TriplePatternFragmentRequest tpfRequest = (TriplePatternFragmentRequest) ldfRequest;
        query.put("subject", tpfRequest.getSubject());
        query.put("predicate", tpfRequest.getPredicate());
        query.put("object", tpfRequest.getObject());
        data.put("query", query);
       
        /* Get the template (uses cache internally) */
        Template temp = datasource instanceof IndexDataSource ? indexTemplate : datasourceTemplate;

        /* Merge data-model with template */
        Writer out = new OutputStreamWriter(outputStream);
        temp.process(data, out);
    }

    @Override
    public void writeNotFound(ServletOutputStream outputStream, HttpServletRequest request) throws Exception {
        Map data = new HashMap();
        data.put("assetsPath", "assets/");
        data.put("datasources", getDatasources());
        data.put("date", new Date());
        data.put("url", request.getRequestURL().toString());
        
        Writer out = new OutputStreamWriter(outputStream);
        notfoundTemplate.process(data, out);
    }

    @Override
    public void writeError(ServletOutputStream outputStream, Exception ex)  throws Exception {
        Map data = new HashMap();
        data.put("assetsPath", "assets/");
        data.put("date", new Date());
        data.put("error", ex);
        
        Writer out = new OutputStreamWriter(outputStream);
        errorTemplate.process(data, out);
    }
}
