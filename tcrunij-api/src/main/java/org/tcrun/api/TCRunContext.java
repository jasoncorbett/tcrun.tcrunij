package org.tcrun.api;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * The tcrun context stores info that allmost all plugins and internal components within tcrun need.  Included in
 * this is:
 * <ul>
 *	<li>Configuration data for tcrun</li>
 *	<li>Configuration data for test cases</li>
 *	<li>Base directory of tcrun</li>
 *	<li>Test Run ID</li>
 *	<li>List of results for this test run.</li>
 * </ul>
 *
 * The configuration instances and the result list are instances of those data structures from java.util.concurrent,
 * to support the need for concurrency in case we need it in the future, however most of tcrun's default actions are
 * single threaded at this time.
 *
 * @author Jason Corbett
 */
public interface TCRunContext
{
	/**
	 * Retrieve the TCRun configuration.  This configuration is a map of Strings to Strings.  It's used
	 * to help configure TCRun and it's plugins.
	 *
	 * @return A thread safe concurrent hashmap of String to String
	 */
	public ConcurrentMap<String, String> getTCRunConfiguration();

	/**
	 * Retrieve the configuration for tests.  This configuration is a map of Strings to Strings.  It is specifically
	 * for test cases.  It allows the tests to be parameterized.
	 *
	 * @return A thread safe concurrent hashmap of String to String
	 */
	public ConcurrentMap<String, String> getTestCaseConfiguration();

	/**
	 * The test run id of the current session of tcrun.  This is used by several parts of tcrun, including the
	 * output of results, and logs.  It is usually the name of the directory under results.
	 *
	 * @return A string containing the name of the test run id.
	 */
	public String getTestRunID();

	/**
	 * Get the root of the tcrun directory hierarchy.  This may be important for creating actions such as screen
	 * shots, looking for tests, or looking for configuration files.
	 *
	 * @return A file object pointing to the directory underwhich all of tcrun runtime exists.
	 */
	public File getTCRunRoot();

	/**
	 * Add a result to the list.  This method will call all appropriate plugins (in particular result watcher
	 * plugins).
	 * 
	 * @param result The result to add to the list of results.
	 */
	public void addResult(Result result);

	/**
	 * Retrieve the list of results for this test run.  The result list will be a synchronized one so you don't need
	 * to do your own synchronization.  Don't use this to add results to the list, use the addResult method as
	 * adding via the list won't call result watchers.
	 *
	 * @return A synchronized list holding the results of the test run.
	 */
	public List<Result> getResultList();
}
