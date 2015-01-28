package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.TCRunContext;

/**
 * The after test list runner plugin get's called after a test list runner has finished running, but before (non live) reporting starts.
 * This could be used to modify or record results.
 *
 * @author jcorbett
 */
public interface AfterTestListRunnerPlugin extends Plugin
{
	/**
	 * This method is called after the test list runner has finished executing and has given up control.
	 * 
	 * @param p_context The tcrun context (which also provides the results list).
	 * @param p_testlistrunner The test list runner which has finished.
	 */
	public void afterTestListRunnerHasExecuted(TCRunContext p_context, TestListRunnerPlugin p_testlistrunner);
}
