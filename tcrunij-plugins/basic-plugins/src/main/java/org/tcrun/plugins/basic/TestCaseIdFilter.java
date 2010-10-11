package org.tcrun.plugins.basic;

import java.util.List;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TestCaseFilter;

/**
 *
 * @author jcorbett
 */
public class TestCaseIdFilter implements TestCaseFilter
{
	public static final String PREFIX = "id";
	private static XLogger logger = XLoggerFactory.getXLogger(TestCaseIdFilter.class);

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
	public void filterTests(List<RunnableTest> toberun, List<RunnableTest> available)
	{
		for(RunnableTest possible : available)
		{
			if(possible.getTestId().equals(testId))
			{
				logger.debug("Found test to match id {}.", testId);
				toberun.add(possible);
			}
		}
	}

	@Override
	public String describeFilter()
	{
		return "Filter looking for a test with id " + testId + ".";
	}
}
