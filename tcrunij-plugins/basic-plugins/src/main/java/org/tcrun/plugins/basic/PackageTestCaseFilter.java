package org.tcrun.plugins.basic;

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
	public FilterResult filterTestCase(RunnableTest testcase)
	{
		logger.debug("Checking test case with package name '{}' against '{}'", testcase.getTestRunner().getTestClass().getPackage().getName(), packageName);
		if(testcase.getTestRunner().getTestClass().getPackage().getName().startsWith(packageName))
			return FilterResult.ACCEPT;
		else
			return FilterResult.UNKNOWN;
	}
}
