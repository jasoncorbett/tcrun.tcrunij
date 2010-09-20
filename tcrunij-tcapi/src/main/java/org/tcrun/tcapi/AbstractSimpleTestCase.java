/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi;

import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.tcapi.assertlib.AssertionFailure;
import org.tcrun.tcapi.assertlib.Check;

/**
 *
 * @author jcorbett
 */
public abstract class AbstractSimpleTestCase implements SimpleTestCase
{
	protected Map<String, String> tcinfo;

	protected XLogger tclog;

	protected Check check;

	@Override
	public final void tcSetup(Map<String, String> configuration) throws Exception
	{
		tcinfo = configuration;
		tclog = XLoggerFactory.getXLogger("test." + this.getClass().getName());
		check = new Check(tclog);
		setup();
	}

	public void setup() throws Exception
	{
	}

	@Override
	public final TestResult doTest() throws Exception
	{
		TestResult retval = TestResult.BROKEN_TEST;
		try
		{
			retval = test();
		} catch(AssertionFailure failure)
		{
			tclog.info("FAIL: Assertion Failure: " + failure.getMessage(), failure);
			retval = TestResult.FAIL;
		}
		return retval;
	}

	public abstract TestResult test() throws Exception;

	@Override
	public final void tcCleanUp() throws Exception
	{
		cleanup();
	}

	public void cleanup() throws Exception
	{
	}

	@Override
	public boolean handleException(Exception e)
	{
		tclog.error("ERROR: Unexpected exception thrown: " + e.getMessage(), e);
		return false;
	}

	public String configValue(String key) throws BrokenTestError
	{
		if(!tcinfo.containsKey(key))
		{
			tclog.error("Missing configuration key '{}'.", key);
			throw new BrokenTestError("Missing configuration key '" + key + "'.");
		}
		String retval = tcinfo.get(key);
		tclog.debug("Using configuration '{}'='{}'.", key, retval);
		return retval;
	}

	public String configValue(String key, String defaultValue)
	{
		String retval = defaultValue;
		if(!tcinfo.containsKey(key))
		{
			tclog.info("Configuration key '{}' missing, using default '{}'.", key, defaultValue);
		} else
		{
			retval = tcinfo.get(key);
			tclog.debug("Using configuration '{}'='{}'.", key, retval);
		}

		return retval;
	}
}
