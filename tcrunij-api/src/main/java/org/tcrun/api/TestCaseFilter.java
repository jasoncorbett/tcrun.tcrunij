package org.tcrun.api;

import java.util.List;

/**
 * A test case filter is a class that filters out / in the tests that match a set of criteria.  This interface doesn't
 * concern itself with what the criteria is, just the filtering operation.
 *
 * 2 lists are given to the filter, a list of tests to be run, and a list of available tests.  The filter's job is to
 * modify the list of to be run tests based on what is available and any filter specific criteria.  The filter could
 * add tests to the list, or remove them (exclusion filters).
 *
 * @author jcorbett
 */
public interface TestCaseFilter
{
	/**
	 * Filter the tests that are to be run.
	 *
	 * @param toberun The list of tests currently to be run.
	 * @param available The available list of tests.
	 */
	public void filterTests(List<RunnableTest> toberun, List<RunnableTest> available);

	/**
	 * Return a string with a description of this filter, and it's criteria.
	 * @return A string describing this filter.
	 */
	public String describeFilter();
}
