package org.tcrun.plugins.basic;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseFilter;
import org.tcrun.api.plugins.FilterListPlugin;
import org.tcrun.api.plugins.TestListPlugin;
import org.tcrun.api.plugins.TestLoaderPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin(TestListPlugin.class)
public class BasicTestListPlugin implements TestListPlugin
{
	private static XLogger logger = XLoggerFactory.getXLogger(BasicTestListPlugin.class);

	private List<String> m_test_ids;

	private List<TestCaseFilter> m_filters;

	public BasicTestListPlugin()
	{
		m_test_ids = new ArrayList<String>();
		m_filters = new ArrayList<TestCaseFilter>();
	}

	@Override
	public List<RunnableTest> getTests(TCRunContext p_context, List<TestLoaderPlugin> p_loaders, List<FilterListPlugin> p_filterlists)
	{
		logger.debug("There are '{}' test loader plugins, going through each one.", p_loaders.size());
		List<RunnableTest> available = new ArrayList<RunnableTest>();
		List<RunnableTest> toberun = new ArrayList<RunnableTest>();

		for(TestLoaderPlugin plugin : p_loaders)
		{
			logger.debug("Going to initialize and iterate through tests on plugin class '{}' with name '{}'.", plugin.getClass().getName(), plugin.getPluginName());
			plugin.initialize(p_context);
			for(RunnableTest test : plugin)
			{
				available.add(test);
			}
			logger.debug("There are now {} available tests.", available.size());
		}

		List<TestCaseFilter> filters = new ArrayList<TestCaseFilter>();
		logger.debug("Looping through {} filter list plugins to get a list of filters.", p_filterlists.size());
		for(FilterListPlugin plugin : p_filterlists)
		{
			logger.debug("Calling getFilters on plugin with name '{}' and class name '{}'.", plugin.getPluginName(), plugin.getClass().getName());
			List<TestCaseFilter> filters_from_plugin = plugin.getFilters(p_context);
			if(filters_from_plugin != null)
			{
				logger.debug("Recieved {} filters from plugin '{}'.", filters_from_plugin.size(), plugin.getPluginName());
				filters.addAll(filters_from_plugin);
			}
		}
		logger.debug("There are a total of {} filters that need to be run.", filters.size());
		for(TestCaseFilter filter : filters)
		{
			logger.debug("Calling filterTests for a filter describing itself as '{}'.", filter.describeFilter());
			filter.filterTests(toberun, available);
			logger.debug("There are now {} tests to be run.", toberun.size());
		}
		logger.debug("Returning '{}' tests.", toberun.size());
		return toberun;
	}

	public String getPluginName()
	{
		return "Basic Test List Plugin";
	}
}
