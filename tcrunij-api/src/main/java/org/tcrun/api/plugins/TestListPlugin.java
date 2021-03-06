package org.tcrun.api.plugins;

import java.util.List;
import org.tcrun.api.Plugin;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;

/**
 * A test list plugin is responsible for forming a list of tests to be run.  It should use filters
 * or other methods to select which tests to run.  A list of loader plugins are passed in for the use
 * of getting the list.
 *
 * @author jcorbett
 */
public interface TestListPlugin extends Plugin
{
	/**
	 * Get a list of runnable tests from a list of loaders.
	 *
	 * @param p_context The tcrun context containing (among other things) configuration data.
	 * @param p_loaders A list of test case loaders.
	 * @param p_filters A list of FilterList plugins that can provide the filters to run.
	 *
	 * @return a list of runnable tests to be added to the TestListRunner.
	 */
	public List<RunnableTest> getTests(TCRunContext p_context, List<TestLoaderPlugin> p_loaders, List<FilterListPlugin> p_filters);
}
