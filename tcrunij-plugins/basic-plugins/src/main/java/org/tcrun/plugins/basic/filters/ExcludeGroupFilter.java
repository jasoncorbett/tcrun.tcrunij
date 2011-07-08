package org.tcrun.plugins.basic.filters;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TestCaseAttribute;
import org.tcrun.api.TestCaseFilter;

/**
 *
 * @author jcorbett
 */
public class ExcludeGroupFilter implements TestCaseFilter
{
	private static XLogger logger = XLoggerFactory.getXLogger(ExcludeGroupFilter.class);
	private String group;

	public ExcludeGroupFilter(String filter)
	{
		logger.debug("Creating an exclude group filter for filter string '{}'.", filter);
		if(!filter.startsWith("excludegroup:"))
			throw new IllegalArgumentException("ExcludeGroupFilters must start with 'excludegroup:', filter passed in was '" + filter + "'");
		group = filter.substring("excludegroup:".length());
	}

	@Override
	public void filterTests(List<RunnableTest> toberun, List<RunnableTest> available)
	{
		List<RunnableTest> toberemoved = new ArrayList<RunnableTest>();
		for(RunnableTest possible : toberun)
		{
			boolean exclude = false;
			for(TestCaseAttribute attr : possible.getTestAttributes())
			{
				if(attr.getName().equals("group") && attr.getValue().equals(group))
				{
					exclude = true;
					break;
				}
			}

			if(exclude)
			{
				logger.debug("Removing test with id {} from toberun list.", possible.getTestId());
				toberemoved.add(possible);
			}
		}
		toberun.removeAll(toberemoved);
	}

	@Override
	public String describeFilter()
	{
		return "Exclusion Filter looking for a test with group '" + group + "'";
	}
}
