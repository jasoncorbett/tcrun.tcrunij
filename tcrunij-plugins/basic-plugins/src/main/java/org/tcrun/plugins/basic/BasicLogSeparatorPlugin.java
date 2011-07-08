package org.tcrun.plugins.basic;

import org.slf4j.MDC;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestRunner;
import org.tcrun.api.plugins.BeforeTestCasePlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin(BeforeTestCasePlugin.class)
public class BasicLogSeparatorPlugin implements BeforeTestCasePlugin
{
	private int test_count = 0;

	public void beforeTestExecutes(TCRunContext p_context, TestRunner p_testrunner)
	{
		MDC.put("TestCaseDir", MDC.get("TestRunId") + "/" + getNextCount() + "-" + p_testrunner.getTestClass().getName());
	}

	public String getPluginName()
	{
		return "Basic Log Separator";
	}

	public synchronized int getNextCount()
	{
		return ++test_count;
	}
}
