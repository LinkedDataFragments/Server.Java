package test.java.org.linkeddatafragments.datasource;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.Test;

import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.IFragmentRequestProcessor;
import org.linkeddatafragments.fragments.TPF.TriplePatternFragment;
import org.linkeddatafragments.fragments.TPF.TriplePatternFragmentRequest;


/**
 *
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 */
public abstract class DataSourceTest {
    private static IDataSource ds;
    
    /**
     * Get data source
     * 
     * @return data source interface 
     */
    public static IDataSource getDatasource() {
        return ds;
    }

    /**
     * Set the data source
     * 
     * @param ds data source 
     */
    public static void setDatasource(IDataSource ds) {
        DataSourceTest.ds = ds;
    }
        
    /**
     * Copy the demo triple in the jar to a temp file.
     * 
     * @return temp file
     * @throws IOException 
     */
    public static File getResourceAsFile() throws IOException {
        File temp = File.createTempFile("ldf-test-hdt", ".ttl");
        temp.deleteOnExit();

        InputStream in = ClassLoader.getSystemResourceAsStream("demo.nt");
        Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return temp;
    }
    
    /**
     * Generate a basic Json configuration
     * 
     * @param title
     * @param desc
     * @param type
     * @return JSON object
     */
    public static JsonObject createConfig(String title, String desc, String type) {
        JsonObject config = new JsonObject();
        config.addProperty("title", title);
        config.addProperty("description", desc);
        config.addProperty("type", type);
        
        return config;
    }
    
    
    /**
     * Test total size of empty TPF
     * 
     */
    @Test
    public void testEmpty() {        
        TriplePatternFragmentRequest request = new TriplePatternFragmentRequest() {
            public boolean isPageRequest() { return true; }
            public long getPageNumber() { return 1L; }
            public String getFragmentURL() { return "http://example.org/f"; }
            public String getDatasetURL() { return "http://example.org/"; }
            public String getSubject() { return "http://nothing.ldf.org"; }
            public String getPredicate() { return null; }
            public String getObject() { return null; }
        };
    
        IFragmentRequestProcessor proc = getDatasource().getRequestProcessor(request);
        TriplePatternFragment fragment = (TriplePatternFragment) proc.createRequestedFragment();
        long totalSize = fragment.getTotalSize();
        
        Assert.assertTrue("Estimate is too big : " + totalSize, totalSize == 0);        
        
    }
    
    /**
     * Test if estimate seems reasonable.
     */
    @Test
    public void testEstimate() {
        TriplePatternFragmentRequest request = new TriplePatternFragmentRequest() {
            public boolean isPageRequest() { return true; }
            public long getPageNumber() { return 1L; }
            public String getFragmentURL() { return "http://example.org/f"; }
            public String getDatasetURL() { return "http://example.org/"; }
            public String getSubject() { return "http://data.gov.be/catalog/ckanvl"; }
            public String getPredicate() { return null; }
            public String getObject() { return null; }
        };
    
        IFragmentRequestProcessor proc = getDatasource().getRequestProcessor(request);
        TriplePatternFragment fragment = (TriplePatternFragment) proc.createRequestedFragment();
        long totalSize = fragment.getTotalSize();
        
        Assert.assertTrue("Estimate is too small : " + totalSize, totalSize > 100);        
    }
}
