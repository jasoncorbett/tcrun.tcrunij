/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.TestCaseStep;
import org.tcrun.api.TestWithSteps;
import org.tcrun.tcapi.assertlib.AssertionFailure;
import org.tcrun.tcapi.assertlib.Check;

/**
 *
 * @author jcorbett
 */
public abstract class AbstractSimpleTestCase implements SimpleTestCase, TestWithSteps
{
	protected Map<String, String> tcinfo;

	protected List<TestCaseStep> steps;

	protected XLogger tclog;

	protected Check check;

	@Override
	public final void tcSetup(Map<String, String> configuration) throws Exception
	{
		tcinfo = configuration;
		tclog = XLoggerFactory.getXLogger("test." + this.getClass().getName());
		check = new Check(tclog);
		steps = new ArrayList<TestCaseStep>();
		frameworkSetup();
		setup();
	}

	public void frameworkSetup() throws Exception
	{
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
		frameworkCleanup();
		cleanup();
	}

	public void frameworkCleanup() throws Exception
	{
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

	public void step(String name)
	{
		step(name, "no expected result given.");
	}

	public void step(String name, String expectedResult)
	{
		tclog.info("Step {} Description: {}", steps.size() + 1, name);
		tclog.info("Step {} Expected Result: {}", steps.size() + 1, expectedResult);
		steps.add(new BasicTestCaseStep(name, expectedResult));
	}

	@Override
	public List<TestCaseStep> getTestSteps()
	{
		return steps;
	}
}
