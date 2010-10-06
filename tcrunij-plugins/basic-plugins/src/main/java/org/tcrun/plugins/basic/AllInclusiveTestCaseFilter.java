package org.tcrun.plugins.basic;

import org.tcrun.api.RunnableTest;
import org.tcrun.api.TestCaseFilter;

/**
 *
 * @author jcorbett
 */
public class AllInclusiveTestCaseFilter implements TestCaseFilter
{
	@Override
	public FilterResult filterTestCase(RunnableTest testcase)
	{
		return FilterResult.ACCEPT;
	}
}
