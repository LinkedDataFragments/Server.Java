package test.java.org.linkeddatafragments.datasource;

import com.google.gson.JsonObject;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

import java.io.File;
import java.io.InputStream;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.After;
import org.junit.AfterClass;

import org.junit.Before;
import org.junit.BeforeClass;

import org.linkeddatafragments.datasource.DataSourceFactory;

import org.linkeddatafragments.datasource.JenaTDBDataSourceType;

/**
 *
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 */
public class JenaTDBDataSourceTest extends DataSourceTest {
    private static File jena;
    private static Dataset dataset;
            
    @BeforeClass
    public static void setUpClass() throws Exception {
        JenaTDBDataSourceType.register();

        String tmpdir = System.getProperty("java.io.tmpdir");
        jena = new File(tmpdir, "ldf-jena-test");
        jena.mkdir();
        
        dataset = TDBFactory.createDataset(jena.getAbsolutePath());

        Model model = dataset.getDefaultModel();
        InputStream in = ClassLoader.getSystemResourceAsStream("demo.nt");
        RDFDataMgr.read(model, in, Lang.NTRIPLES);
        model.commit();

        // Everything is in place, now create the LDF datasource                
        JsonObject config = createConfig("jena tdb test", "jena tdb test",
                                                    JenaTDBDataSourceType.TYPE_NAME);
        
        JsonObject settings = new JsonObject();
        settings.addProperty("directory", jena.getAbsolutePath());
        config.add("settings", settings);

        setDatasource(DataSourceFactory.create(config));
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
    
    @After
    public void tearDown() throws Exception {
    }
}
