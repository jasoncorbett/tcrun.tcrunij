package org.tcrun.api;

/**
 * A test case filter is a class that filters out / in the tests that match a set of criteria.  This interface doesn't
 * concern itself with what the criteria is, just the filtering operation.
 *
 * @author jcorbett
 */
public interface TestCaseFilter
{
	public enum FilterResult { ACCEPT, REJECT };
	public FilterResult filterTestCase(RunnableTest testcase);
}
