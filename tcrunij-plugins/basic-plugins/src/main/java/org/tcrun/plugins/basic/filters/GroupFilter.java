package org.tcrun.plugins.basic.filters;

import java.util.List;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TestCaseAttribute;
import org.tcrun.api.TestCaseFilter;

/**
 * A test case filter that makes sure that the test is in the specified group.
 *
 * @author jcorbett
 */
public class GroupFilter implements TestCaseFilter
{
	private String group;

	public static boolean isFilterFor(String filter)
	{
		return filter.startsWith("group:") || filter.startsWith("g:");
	}

	public GroupFilter(String groupfilter)
	{
		if(groupfilter.startsWith("group:"))
			group = groupfilter.substring("group:".length());
		else if(groupfilter.startsWith("g:"))
			group = groupfilter.substring("g:".length());
		else
			group = ""; // BAD
	}

	@Override
	public void filterTests(List<RunnableTest> toberun, List<RunnableTest> available)
	{
		for(RunnableTest possible : available)
		{
			boolean include = false;
			for(TestCaseAttribute attr : possible.getTestAttributes())
			{
				if(attr.getName().equals("group") && attr.getValue().equals(group))
				{
					include = true;
					break;
				}
			}
			if(include)
				toberun.add(possible);
		}
	}

	@Override
	public String describeFilter()
	{
		return "Filter looking for a test case with an attribute group=" + group;
	}
}
