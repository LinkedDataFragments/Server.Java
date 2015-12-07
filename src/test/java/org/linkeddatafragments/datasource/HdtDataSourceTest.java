/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java.org.linkeddatafragments.datasource;

import com.google.gson.JsonObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.linkeddatafragments.datasource.DataSourceFactory;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.TriplePatternFragment;

import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.options.HDTSpecification;

/**
 *
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 */
public class HdtDataSourceTest {
    private static IDataSource hdt;
    private static File hdtfile;
            
    @BeforeClass
    public static void setUpClass() throws Exception {
        // HDT does not seem to support an InputReader, so write to temp file
        InputStream in = ClassLoader.getSystemResourceAsStream("demo.nt");
        String tmpdir = System.getProperty("java.io.tmpdir");
        File temp = new File(tmpdir, "ldf-jena-test-hdt.ttl");
        Files.copy(in, temp.toPath());
        
        HDT mgr = HDTManager.generateHDT(temp.getAbsolutePath(), null, 
                        RDFNotation.NTRIPLES, new HDTSpecification(), null);
        hdtfile = new File(tmpdir, "ldf-hdt-test");
        mgr.saveToHDT(hdtfile.getAbsolutePath(), null);
        
        temp.getAbsoluteFile().delete();
        
        // Everything is in place, now create the LDF datasource
        
        JsonObject config = new JsonObject();
        config.addProperty("title", "hdt test");
        config.addProperty("description", "hdt test");
        config.addProperty("type", DataSourceFactory.HDT);
        
        JsonObject settings = new JsonObject();
        settings.addProperty("file", hdtfile.getAbsolutePath());
        config.add("settings", settings);
        
        hdt = DataSourceFactory.create(config);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (hdtfile != null) {
            hdtfile.delete();
        }
    }

    @Before
    public void setUp() throws Exception {

    }

    /**
     * Check if estimate makes sense
     * 
     */
    @Test
    public void testEstimate() {
        Model model = ModelFactory.createDefaultModel();
        
        Resource subj = model.createResource("http://data.gov.be/catalog/ckanvl");
        Property pred = null;
        Resource obj = null;

        long offset = 0;
        long limit = 50;
        
        TriplePatternFragment fragment = 
                hdt.getFragment(subj, pred, obj, offset, limit);
        long totalSize = fragment.getTotalSize();
        
        Assert.assertTrue("Estimate is fake: " + totalSize, totalSize != 51);        
    }
    
    @After
    public void tearDown() throws Exception {
    }    
}
