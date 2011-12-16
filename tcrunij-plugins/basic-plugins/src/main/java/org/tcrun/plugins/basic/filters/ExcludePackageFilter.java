package org.tcrun.plugins.basic.filters;

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
public class ExcludePackageFilter implements TestCaseFilter
{
	private static XLogger logger = XLoggerFactory.getXLogger(ExcludePackageFilter.class);
	private String package_name;

	public ExcludePackageFilter(String filter)
	{
		logger.debug("Creating an exclude package filter for filter string '{}'.", filter);
		if(!filter.startsWith("excludepkg:"))
			throw new IllegalArgumentException("ExcludePackageFilters must start with 'excludepkg:', filter passed in was '" + filter + "'");
		package_name = filter.substring("excludepkg:".length());
	}

	@Override
	public void filterTests(List<RunnableTest> toberun, List<RunnableTest> available)
	{
		List<RunnableTest> toberemoved = new ArrayList<RunnableTest>();
		for(RunnableTest possible : toberun)
		{
			if(possible.getTestRunner().getTestClass().getPackage().getName().startsWith(package_name))
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
		return "Exclusion Filter looking for a test in package '" + package_name + "'";
	}
}
