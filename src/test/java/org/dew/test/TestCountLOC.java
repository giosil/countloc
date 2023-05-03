package org.dew.test;

import java.io.File;

import org.dew.dev.CountLOC;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestCountLOC extends TestCase {
  
  public TestCountLOC(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestCountLOC.class);
  }
  
  public void testApp() throws Exception {
    CountLOC.start(new File("."), "java", null);
  }
  
}
