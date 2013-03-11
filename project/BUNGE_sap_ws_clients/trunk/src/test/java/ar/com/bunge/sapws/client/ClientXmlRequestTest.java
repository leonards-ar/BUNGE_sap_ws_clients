package ar.com.bunge.sapws.client;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class ClientXmlRequestTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ClientXmlRequestTest( String testName )
    {
        super( testName );
    }

    public void testNestedLoops() throws Exception
    {
    	String requestXml = "init\n" +
    			"${xloopn(v)}\n" +
				"InitValue: ${#initVal#}\n" +
				"\t${loopn(j)}\n"
				+ "\t\tNestedVal: ${#nestedVal#}\n" +
				"\t{end loopn}\n"
				+ "EndValue: ${#endVal#}\n" +
				"{end xloopn}\n" +
				"end";

    	String expected = "init\n\nInitValue: initVal(1)\n\t\n\t\tNestedVal: nestedVal(1)(1)\n\t\nEndValue: endVal(1)\n\nInitValue: initVal(2)\n\t\n\t\tNestedVal: nestedVal(2)(1)\n\t\n\t\tNestedVal: nestedVal(2)(2)\n\t\nEndValue: endVal(2)\n\nend";

    	Map<String, Object> context = new HashMap<String, Object>();
    	context.put("v", "2");
    	context.put("j(1)", "1");
    	context.put("j(2)", "2");
    	context.put("initval(1)", "initVal(1)");
    	context.put("initval(2)", "initVal(2)");
    	context.put("endval(1)", "endVal(1)");
    	context.put("endval(2)", "endVal(2)");
    	context.put("nestedval(1)(1)", "nestedVal(1)(1)");
    	context.put("nestedval(2)(1)", "nestedVal(2)(1)");
    	context.put("nestedval(2)(2)", "nestedVal(2)(2)");
    	
    	ClientXmlRequest req = new ClientXmlRequest(requestXml);
    	req.compile(context);
    	
    	String result = req.getRequest();
    	
    	assertEquals(expected, result);

    }
}
