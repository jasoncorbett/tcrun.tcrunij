/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.api.plugins;

import java.util.List;
import org.tcrun.api.Plugin;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;

/**
 * A test list runner is responsible for running a list of tests.  It is important to note
 * that this plugin will need to be sure to call all the normal pre and post test case plugins.
 *
 * @author jcorbett
 */
public interface TestListRunnerPlugin extends Plugin
{
	/**
	 * Initialize the test list runner plugin.
	 *
	 * @param p_context
	 */
	public void initialize(TCRunContext p_context);

	/**
	 * There can only be one test list runner per session.  Because of this the plugin needs
	 * to provide an "importance rating" for how important this particular runner is used.  Highest
	 * importance wins.
	 *
	 * @return an "importance rating"
	 */
	public int getArbitraryImportanceRating();

	/**
	 * Add tests to the final list of tests to be run.
	 *
	 * @param p_tests
	 */
	public void addTests(List<RunnableTest> p_tests);

	/**
	 * Get a list of tests that are to be run by this runner.  This should include every test
	 * added in the addTests method.
	 *
	 * @return a list of tests.
	 */
	public List<RunnableTest> getTests();

	/**
	 * Run all the tests that are a part of the test plan.
	 */
	public void runTests(List<BeforeTestCasePlugin> p_beforetcplugins, List<AfterTestCasePlugin> p_aftertcplugins);
}
