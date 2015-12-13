/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.java.org.linkeddatafragments.datasource;

import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.linkeddatafragments.datasource.DataSourceFactory;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.datasource.JenaTDBDataSourceType;
import org.linkeddatafragments.datasource.TriplePatternFragment;

/**
 *
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 */
public class JenaTDBDataSourceTest {
    private static IDataSource tdb;
    private static Dataset dataset;
    private static File jena;

    private final static String PREFIX = "http://test.ldf.org/";
            
    @BeforeClass
    public static void setUpClass() throws Exception {
        JenaTDBDataSourceType.register();

        String tmpdir = System.getProperty("java.io.tmpdir");
        jena = new File(tmpdir, "ldf-jena-test");
        jena.mkdir();
        
        dataset = TDBFactory.createDataset(jena.getAbsolutePath());
        
        JsonObject config = new JsonObject();
        config.addProperty("title", "jena test");
        config.addProperty("description", "jena tdb test");
        config.addProperty("type", JenaTDBDataSourceType.TYPE_NAME);
        
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
        Model model = dataset.getDefaultModel();
        
        // Generate a set of statements
        int subjs = 153;
        int preds = 29;
        int objs = 17;
        
        for (int s = 0; s < subjs; s++) {
            Resource subj = model.createResource(PREFIX + "s/" + s);
            for (int p = 0; p < preds ; p++) {
                Property pred = model.createProperty(PREFIX + "p/" + p);
                for (int o = 0; o < objs ; o++) {
                    Resource obj = model.createResource(PREFIX + "o/" + o);
                    model.add(subj, pred, obj);
                }
            }
        }
        model.commit();
    }

    /**
     * Check if estimate is based on jena query plan, or just a fake one.
     * 
     */
    @Test
    public void testEstimate() {
        Model model = dataset.getDefaultModel();
        
        Resource subj = model.createResource(PREFIX + "s/1");
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
