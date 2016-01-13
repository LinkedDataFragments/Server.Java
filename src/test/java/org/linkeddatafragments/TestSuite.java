package test.java.org.linkeddatafragments;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.java.org.linkeddatafragments.datasource.HdtDataSourceTest;
import test.java.org.linkeddatafragments.datasource.JenaTDBDataSourceTest;

/**
 *
 * @author <a href="mailto:bart.hanssens@fedict.be">Bart Hanssens</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   HdtDataSourceTest.class,
   JenaTDBDataSourceTest.class
})
public class TestSuite {
    
}
