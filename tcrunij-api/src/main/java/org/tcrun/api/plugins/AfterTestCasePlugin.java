package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestRunner;

/**
 * This plugin defines a method that will be run after a test runner has finished running a test.  This is useful for
 * cleaning up anything that might have been initialized and / or injected into the test previously.
 *
 * @author jcorbett
 */
public interface AfterTestCasePlugin extends Plugin
{
	/**
	 * After a test runner has finished a test, this method will be called.
	 * 
	 * @param p_context The context containing configuration and results data.
	 * @param p_testrunner The test runner, which contains a reference to the test.
	 */
	public void afterTestCase(TCRunContext p_context, TestRunner p_testrunner);
}
