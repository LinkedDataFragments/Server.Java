package test.java.org.linkeddatafragments.datasource;

import com.google.gson.JsonObject;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.linkeddatafragments.datasource.DataSourceFactory;
import org.linkeddatafragments.datasource.DataSourceTypesRegistry;
import org.linkeddatafragments.datasource.hdt.HdtDataSourceType;
import org.linkeddatafragments.util.TriplePatternElementParser;
import org.linkeddatafragments.util.TriplePatternElementParserForJena;
import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.options.HDTSpecification;

/**
 *
 * @author Bart Hanssens <bart.hanssens@fedict.be>
 */
public class HdtDataSourceTest extends DataSourceTest<RDFNode,String,String> {

    private static File hdtfile;

    @Override
    protected TriplePatternElementParser<RDFNode,String,String>
                                               getTriplePatternElementParser()
    {
        return TriplePatternElementParserForJena.getInstance();
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        final String typeName = "HdtTestSourceType";
        if ( ! DataSourceTypesRegistry.isRegistered(typeName) ) {
            DataSourceTypesRegistry.register( typeName, new HdtDataSourceType() );
        }

        // HDT does not seem to support an InputReader, so write to temp file
        File temp = getResourceAsFile();

        HDT mgr = HDTManager.generateHDT(temp.getAbsolutePath(),
                        "http://linkeddatafragments.org",
                        RDFNotation.NTRIPLES, new HDTSpecification(), null);
        hdtfile = File.createTempFile("ldf-hdt-test", ".hdt");
        mgr.saveToHDT(hdtfile.getAbsolutePath(), null);
        
        temp.getAbsoluteFile().delete();
        
        // Everything is in place, now create the LDF datasource
        JsonObject config = createConfig("hdt test", "hdt test", typeName);
        
        JsonObject settings = new JsonObject();
        settings.addProperty("file", hdtfile.getAbsolutePath());
        config.add("settings", settings);
        
        setDatasource(DataSourceFactory.create(config));
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
    
    @After
    public void tearDown() throws Exception {
    }    
}
