package org.tcrun.api.plugins;

import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestRunner;

/**
 * Plugin that runs before a test runner executes a test.  This can be used to inject items into the test, or change
 * the configuration that a test has.
 *
 * @author jcorbett
 */
public interface BeforeTestCasePlugin
{
	public void beforeTestExecutes(TCRunContext p_context, TestRunner p_testrunner);
}
