/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.api;

import java.util.Map;

/**
 * A test runner runs the test, and that's about it!  It's up to the test runner to determine how many steps that
 * entails, and how to accomplish it.  The other methods are for plugins to run their magic.
 *
 * @author jcorbett
 */
public interface TestRunner
{
	/**
	 * Get the test's class object.  This is for informational purposes, and may be used before the instance is
	 * needed.
	 *
	 * @return The class that encapsulates the test.
	 */
	public Class<?> getTestClass();

	/**
	 * The instance of the test will be important when plugins want to alter the test's properties, or get
	 * information from the test.  It is important to note that the instance will probably be created on demand, and
	 * as such this method should not be called until it is absolutely sure that the test will be run.
	 *
	 * @return an instance of the test's class.
	 */
	public Object getTestInstance();

	/**
	 * Get a map of the configuration for this test.  Normally this will be the same as what is in TCRunContext,
	 * however it could be customized for this test (as in data driven test runner).  This does not have to be
	 * a writable version configuration, it could be a copy.
	 *
	 * @return A Map containing the configuration for the test.
	 */
	public Map<String, String> getConfiguration();

	/**
	 * Set a named value in the configuration.  This allows the inputs of the test to be modified at runtime.
	 *
	 * @param p_key The name of the configuration value to set.
	 * @param p_value The value of the configuration to set.
	 */
	public void setConfigurationValue(String p_key, String p_value);


	/**
	 * Run the test, place the result in the context's result list.
	 *
	 * @param context The context which contains a list of results.
	 */
	public void runTest(TCRunContext context);
}
