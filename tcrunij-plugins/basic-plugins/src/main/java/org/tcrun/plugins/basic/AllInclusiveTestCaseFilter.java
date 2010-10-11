package org.tcrun.plugins.basic;

import java.util.List;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TestCaseFilter;

/**
 *
 * @author jcorbett
 */
public class AllInclusiveTestCaseFilter implements TestCaseFilter
{

	@Override
	public void filterTests(List<RunnableTest> toberun, List<RunnableTest> available)
	{
		toberun.addAll(available);
	}

	@Override
	public String describeFilter()
	{
		return "Filter which includes all tests.";
	}
}
