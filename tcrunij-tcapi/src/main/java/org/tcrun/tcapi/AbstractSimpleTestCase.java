/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.Main;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.TestCaseStep;
import org.tcrun.api.TestWithName;
import org.tcrun.api.TestWithSteps;
import org.tcrun.api.TestWithUUID;
import org.tcrun.api.annotations.TestName;
import org.tcrun.tcapi.assertlib.AssertionFailure;
import org.tcrun.tcapi.assertlib.Check;

/**
 *
 * @author jcorbett
 */
public abstract class AbstractSimpleTestCase implements SimpleTestCase, TestWithSteps, TestWithName, TestWithUUID
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

	/**
	 * This method can be used by frameworks to setup for a test case.  it is called before setup, but if you choose
	 * to override, call super.frameworkSetup() please.
	 *
	 * @throws Exception tcrunij catches any exceptions thrown, so you don't have to surround your code with try/catch.
	 */
	public void frameworkSetup() throws Exception
	{
	}

	/**
	 * setup for a test case.  This should only be implemented by the test case, frameworks should override the
	 * frameworkSetup method.
	 *
	 * @throws Exception if an error occurs during setup
	 */
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
			handleException(failure);
		}
		return retval;
	}

	/**
	 * Perform the actual test.
	 *
	 * @return a result for the test
	 * @throws Exception if an error occurs during the test run
	 */
	public abstract TestResult test() throws Exception;

	@Override
	public abstract UUID getTestUUID();

	@Override
	public final void tcCleanUp() throws Exception
	{
		try
		{
			cleanup();
		} finally
		{
			frameworkCleanup();
		}
	}

	public void frameworkCleanup() throws Exception
	{
	}

	public void cleanup() throws Exception
	{
	}

	public void setupDebugShell(Global global)
	{
		global.defineProperty("test", this, ScriptableObject.READONLY);
	}

	public void enterDebugShell()
	{
		setupDebugShell(Main.global);
		Main.shellContextFactory.initApplicationClassLoader(this.getClass().getClassLoader());
		Main.exec(new String[] {});
	}

	@Override
	public boolean handleException(Exception e)
	{
		tclog.error("ERROR: Unexpected exception thrown: " + e.getMessage(), e);
		return false;
	}

	/**
	 * Retrieve configuration from the tcinfo variable.  This method will provide information on what key was trying
	 * to be accessed if the key does not exist.
	 *
	 * @param key The name of the configuration you're looking for.
	 * @return The value assigned to the key provided in the configuration.
	 * @throws TestCaseError if the configuration key does not exist, debugging information is provided.
	 */
	public String configValue(String key) throws TestCaseError
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

	/**
	 * Retrieve configuration from the tcinfo variable, using a default value if the key does not exist.
	 *
	 * @param key The name of the configuration you're looking for.
	 * @param defaultValue The default value to be returned if the key does not exist.
	 * @return the value of the configuration assigned to the key provided, if it doesn't exist the default value is returned.
	 * @throws TestCaseError if configValue detects anything wrong.  The default implementation doesn't throw this error.
	 */
	public String configValue(String key, String defaultValue) throws TestCaseError
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

	/**
	 * Log a logical step name / description, and record it for use later.  Steps can be printed out to the tester,
	 * or possibly placed in a database by a plugin.  Using steps can help a manual tester know what you're automation
	 * is trying to do.
	 *
	 * @param name The name or description of the step being performed.
	 */
	public void step(String name)
	{
		step(name, "no expected result given.");
	}

	/**
	 * Log a logical step name / description, and its expected result.  Record it for use later.  Steps can be
	 * printed out to the tester, or possibly placed in a database by a plugin.  Using steps can help a manual tester
	 * know what you're automation is trying to do.
	 *
	 * @param name The name or description of the logical test step.
	 * @param expectedResult The expected result of performing the step.
	 */
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

	@Override
	public String getTestName()
	{
		String retval = this.getClass().getName();

		if(this.getClass().isAnnotationPresent(TestName.class))
		{
			retval = this.getClass().getAnnotation(TestName.class).value();
		} else
		{
				String name = this.getClass().getSimpleName();
				// underscores become spaces
				name = name.replace("_", " ");
				// camelCase get's spaces inbetween, ex CamelCase becomes Camel Case
				name = name.replaceAll("([a-z])([A-Z])", "$1 $2");
				if(!name.contains(" "))
				{
					// use the package name as the first part of the name
					name = this.getClass().getPackage().getName().replaceAll(".*\\.", "");
					// same changes as above
					name = name.replace("_", " ");
					name = name.replaceAll("([a-z])([A-Z])", "$1 $2");
					// add the class name onto the end of the name, no need for substitutions, they didn't do anything
					// before
					name = name + " - " + this.getClass().getSimpleName();
				}
				retval = name;
		}

		return retval;
	}
}
