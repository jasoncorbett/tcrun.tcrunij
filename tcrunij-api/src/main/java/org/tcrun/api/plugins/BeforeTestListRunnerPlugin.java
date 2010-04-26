package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.TCRunContext;

/**
 * A plugin to be executed before a test list runner starts to run it's tests.
 *
 * @author jcorbett
 */
public interface BeforeTestListRunnerPlugin extends Plugin
{
	/**
	 * These plugins get executed before a test list runner begins it's execution.
	 * 
	 * @param p_context The tcrun context containing configuration data.
	 * @param p_testplan The test plan that is about to be executed.
	 * @throws StartupError You can throw a StartupError to stop execution, please provide a simple end user reason.
	 */
	public void beforTestListRunnerExecutes(TCRunContext p_context, TestListRunnerPlugin p_testlistrunner) throws StartupError;

}
