/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java.org.linkeddatafragments.datasource;

import com.google.gson.JsonObject;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;

import java.io.File;
import java.io.InputStream;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.linkeddatafragments.datasource.DataSourceFactory;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.TriplePatternFragment;

/**
 *
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 */
public class JenaTDBDataSourceTest {
    private static IDataSource tdb;
    private static File jena;
    private static Dataset dataset;
            
    @BeforeClass
    public static void setUpClass() throws Exception {
        String tmpdir = System.getProperty("java.io.tmpdir");
        jena = new File(tmpdir, "ldf-jena-test");
        jena.mkdir();
        
        dataset = TDBFactory.createDataset(jena.getAbsolutePath());

        Model model = dataset.getDefaultModel();
        InputStream in = ClassLoader.getSystemResourceAsStream("demo.nt");
        RDFDataMgr.read(model, in, Lang.NTRIPLES);
        model.commit();
        
        // Everything is in place, now create the LDF datasource
                
        JsonObject config = new JsonObject();
        config.addProperty("title", "jena test");
        config.addProperty("description", "jena tdb test");
        config.addProperty("type", DataSourceFactory.JENA_TDB);
        
        JsonObject settings = new JsonObject();
        settings.addProperty("directory", jena.getAbsolutePath());
        config.add("settings", settings);

        tdb = DataSourceFactory.create(config);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        TDBFactory.release(dataset);
        File[] files = jena.listFiles();
        for (File f : files) {
            f.delete();
        }
        jena.delete();
 
    }

    @Before
    public void setUp() throws Exception {                
    }

    /**
     * Check if estimate is based on jena query plan, or just a fake one.
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
                tdb.getFragment(subj, pred, obj, offset, limit);
        long totalSize = fragment.getTotalSize();
        
        Assert.assertTrue("Estimate is fake: " + totalSize, totalSize != 51);        
    }
    
    @After
    public void tearDown() throws Exception {
    }
}
