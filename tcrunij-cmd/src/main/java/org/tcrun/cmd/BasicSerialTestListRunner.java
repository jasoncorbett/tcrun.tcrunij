package org.tcrun.cmd;

import java.util.List;
import java.util.Vector;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.AfterTestCasePlugin;
import org.tcrun.api.plugins.BeforeTestCasePlugin;
import org.tcrun.api.plugins.TestListRunnerPlugin;

/**
 * This is implimented in the Main, because there must be at least 1 test list runner.
 *
 * @author jcorbett
 */
@ImplementsPlugin({TestListRunnerPlugin.class})
public class BasicSerialTestListRunner implements TestListRunnerPlugin
{
	private TCRunContext m_context;
	private List<RunnableTest> m_test_list;

	public void initialize(TCRunContext p_context)
	{
		m_context = p_context;
		m_test_list = new Vector<RunnableTest>();
	}

	public int getArbitraryImportanceRating()
	{
		return 0;
	}

	public void addTests(List<RunnableTest> p_tests)
	{
		m_test_list.addAll(p_tests);
	}

	public List<RunnableTest> getTests()
	{
		return m_test_list;
	}

	public void runTests(List<BeforeTestCasePlugin> p_beforetcplugins, List<AfterTestCasePlugin> p_aftertcplugins)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getPluginName()
	{
		return "Basic Serial Test List Runner";
	}

}
