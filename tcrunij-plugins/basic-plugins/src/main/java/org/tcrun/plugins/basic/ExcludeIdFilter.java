package org.tcrun.plugins.basic;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TestCaseFilter;

/**
 *
 * @author jcorbett
 */
public class ExcludeIdFilter implements TestCaseFilter
{
	private static XLogger logger = XLoggerFactory.getXLogger(ExcludeIdFilter.class);
	private String id;

	public ExcludeIdFilter(String filter)
	{
		logger.debug("Creating an exclude id filter for filter string '{}'.", filter);
		if(!filter.startsWith("excludeid:"))
			throw new IllegalArgumentException("ExcludeIdFilters must start with 'excludeid:', filter passed in was '" + filter + "'");
		id = filter.substring("excludeid:".length());
	}

	@Override
	public void filterTests(List<RunnableTest> toberun, List<RunnableTest> available)
	{
		List<RunnableTest> toberemoved = new ArrayList<RunnableTest>();
		for(RunnableTest possible : toberun)
		{
			if(possible.getTestId().equals(id))
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
		return "Exclusion Filter looking for a test with id '" + id + "'";
	}
}
