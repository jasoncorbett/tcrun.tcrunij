package org.tcrun.cmd;

import java.util.List;
import java.util.Vector;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
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
	static private XLogger s_logger = XLoggerFactory.getXLogger(BasicSerialTestListRunner.class);
	private TCRunContext m_context;
	private List<RunnableTest> m_test_list;

	public void initialize(TCRunContext p_context)
	{
		s_logger.debug("Initializing BasicSerialTestListRunner.");
		m_context = p_context;
		m_test_list = new Vector<RunnableTest>();
	}

	public int getArbitraryImportanceRating()
	{
		return 0;
	}

	public void addTests(List<RunnableTest> p_tests)
	{
		s_logger.debug("Adding '{}' tests to test list for execution.", p_tests.size());
		m_test_list.addAll(p_tests);
	}

	public List<RunnableTest> getTests()
	{
		return m_test_list;
	}

	public void runTests(List<BeforeTestCasePlugin> p_beforetcplugins, List<AfterTestCasePlugin> p_aftertcplugins)
	{
		s_logger.info("Running '{}' tests.", m_test_list.size());
		for(RunnableTest test : m_test_list)
		{
			s_logger.debug("Calling '{}' before test case plugins for test with id '{}'.", p_beforetcplugins.size(), test.getTestId());
			for(BeforeTestCasePlugin plugin : p_beforetcplugins)
			{
				s_logger.debug("Calling beforeTestExecutes from plugin with class '{}' and name '{}' on test with id '{}'.", new Object[] {plugin.getClass().getName(), plugin.getPluginName(), test.getTestId()});
				plugin.beforeTestExecutes(m_context, test.getTestRunner());
			}
			s_logger.debug("Done calling BeforeTestCasePlugin plugins, running test '{}'.", test.getTestId());
			test.getTestRunner().runTest(m_context);
			s_logger.info("Test with id '{}' is now finished running.", test.getTestId());
			s_logger.debug("Calling '{}' AfterTestCasePlugin plugins for test with id '{}'.", p_aftertcplugins.size(), test.getTestId());
			for(AfterTestCasePlugin plugin: p_aftertcplugins)
			{
				s_logger.debug("Calling afterTestCase from plugin with class '{}' and name '{}' on test with id '{}'.", new Object[] {plugin.getClass().getName(), plugin.getPluginName(), test.getTestId()});
				plugin.afterTestCase(m_context, test.getTestRunner());
			}
			s_logger.debug("Done calling AfterTestCasePlugin plugins.");
		}
	}

	public String getPluginName()
	{
		return "Basic Serial Test List Runner";
	}

}
