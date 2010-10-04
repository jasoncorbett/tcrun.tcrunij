package org.tcrun.plugins.basic;

import org.tcrun.api.RunnableTest;
import org.tcrun.api.TestCaseFilter;

/**
 *
 * @author jcorbett
 */
public class TestCaseIdFilter implements TestCaseFilter
{
	public static final String PREFIX = "id";

	private String testId;

	public static boolean isFilterFor(String filterString)
	{
		if(filterString != null && filterString.startsWith("id:"))
			return true;
		else
			return false;
	}

	public TestCaseIdFilter(String filter)
	{
		testId = filter.substring(3);
	}

	@Override
	public FilterResult filterTestCase(RunnableTest testcase)
	{
		if(testcase.getTestId().equalsIgnoreCase(testId))
			return FilterResult.ACCEPT;
		return FilterResult.UNKNOWN;
	}
}
