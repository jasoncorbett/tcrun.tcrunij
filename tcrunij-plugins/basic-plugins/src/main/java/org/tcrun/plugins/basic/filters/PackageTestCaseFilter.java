package org.tcrun.plugins.basic.filters;

import java.util.List;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TestCaseFilter;

/**
 *
 * @author jcorbett
 */
public class PackageTestCaseFilter implements TestCaseFilter
{
	private static XLogger logger = XLoggerFactory.getXLogger(PackageTestCaseFilter.class);
	private String packageName;

	public static boolean isFilterFor(String filterString)
	{
		if(filterString != null && (filterString.startsWith("package:") || filterString.startsWith("pkg:")))
			return true;
		else
			return false;
	}

	public PackageTestCaseFilter(String filterString)
	{
		if(filterString.startsWith("pkg:"))
			packageName = filterString.substring(4);
		else
			packageName = filterString.substring(8); // starts with package:
	}

	@Override
	public void filterTests(List<RunnableTest> toberun, List<RunnableTest> available)
	{
		for(RunnableTest possible : available)
		{
			if(possible.getTestRunner().getTestClass().getPackage().getName().startsWith(packageName))
			{
				logger.debug("Found test {} that is in package {}.", possible.getTestId(), packageName);
				toberun.add(possible);
			}
		}
	}

	@Override
	public String describeFilter()
	{
		return "Filter looking for tests inside a package or sub package of " + packageName + ".";
	}

}
