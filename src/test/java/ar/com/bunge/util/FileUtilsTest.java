package ar.com.bunge.util;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {

	public FileUtilsTest() {
	}

	public FileUtilsTest(String name) {
		super(name);
	}

	public void testFixIndexedVariableName() throws Exception {
		assertEquals("var(1)(1)(1)", FileUtils.fixIndexedVariableName("var(1)(1)(1)"));
		assertEquals("var(", FileUtils.fixIndexedVariableName("var("));
		assertEquals("var(", FileUtils.fixIndexedVariableName("var("));
		assertEquals("var(1)(1)(1)", FileUtils.fixIndexedVariableName("vAr(01)(01)(01)"));
		assertEquals("var(1)(1)(1)", FileUtils.fixIndexedVariableName("vAr(01)(1)(01)"));
		assertEquals("var(2)(1)(2)", FileUtils.fixIndexedVariableName("vAr(002)(001)(002)"));
		assertEquals("var(a)", FileUtils.fixIndexedVariableName("vAr(A)"));
	}
}
