package org.tcrun.itegration.tests.tcapi;

import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.annotations.TestName;
import org.tcrun.tcapi.SimpleTestCase;
import org.tcrun.tcapi.TestResult;

/**
 *
 * @author jcorbett
 */
@TestName("TCRunIJ: Very, very simple test")
public class VerySimpleTest implements SimpleTestCase
{
	private static XLogger tclog = XLoggerFactory.getXLogger("test." + VerySimpleTest.class.getName());
	private Map<String, String> tcinfo;

	public void tcSetup(Map<String, String> configuration) throws Exception
	{
		tcinfo = configuration;
		tclog.debug("Inside tcSetup!");
	}

	public TestResult doTest() throws Exception
	{
		tclog.info("Inside doTest, going to return PASS.");
		Thread.sleep(2000);
		return TestResult.PASS;
	}

	public void tcCleanUp() throws Exception
	{
		tclog.info("Inside tcCleanUp!");
	}

	public boolean handleException(Exception e)
	{
		tclog.error("Caught exception during test execution.", e);
		return false;
	}
}
