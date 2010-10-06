package org.tcrun.plugins.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.PluginManager;
import org.tcrun.api.PluginManagerFactory;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseFilter;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.FilterPlugin;
import org.tcrun.api.plugins.TestListPlugin;
import org.tcrun.api.plugins.TestLoaderPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({TestListPlugin.class, CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class})
public class BasicTestListPlugin implements TestListPlugin, CommandLineOptionPlugin, CommandLineConsumerPlugin
{
	private static XLogger logger = XLoggerFactory.getXLogger(BasicTestListPlugin.class);

	private List<String> m_test_ids;

	private List<TestCaseFilter> m_filters;

	public BasicTestListPlugin()
	{
		m_test_ids = new ArrayList<String>();
		m_filters = new ArrayList<TestCaseFilter>();
	}

	public List<RunnableTest> getTests(TCRunContext p_context, List<TestLoaderPlugin> p_loaders)
	{
		logger.debug("There are '{}' test loader plugins, going through each one.", p_loaders.size());
		List<RunnableTest> retval = new ArrayList<RunnableTest>();

		for(TestLoaderPlugin plugin : p_loaders)
		{
			logger.debug("Going to initialize and iterate through tests on plugin class '{}' with name '{}'.", plugin.getClass().getName(), plugin.getPluginName());
			plugin.initialize(p_context);
			for(RunnableTest test : plugin)
			{
				TestCaseFilter.FilterResult in_or_out = TestCaseFilter.FilterResult.UNKNOWN;

				for(TestCaseFilter filter : m_filters)
				{
					logger.debug("Checking test '{}' against filter '{}'.", test.getTestId(), filter.getClass().getName());
					TestCaseFilter.FilterResult result = filter.filterTestCase(test);
					if(result == TestCaseFilter.FilterResult.ACCEPT)
						in_or_out = result;
					if(result == TestCaseFilter.FilterResult.REJECT)
					{
						in_or_out = result;
						break;
					}
				}

				if(in_or_out == TestCaseFilter.FilterResult.ACCEPT)
				{
					logger.debug("Found test '{}' from plugin '{}'.", test.getTestId(), plugin.getPluginName());
					retval.add(test);
				}
			}
		}
		logger.debug("Returning '{}' tests.", retval.size());
		return retval;
	}

	public String getPluginName()
	{
		return "Basic Test List Plugin";
	}

	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.withLongOpt("id")
			         .isRequired(false)
				 .withDescription("Specify a test by it's id, this is the same as -f id:<ID>.")
				 .withArgName("ID")
				 .hasArgs()
				 .create());
		options.addOption(OptionBuilder.hasArgs()
		                 .withArgName("FILTER")
				 .withDescription("Add test cases that match FILTER")
				 .withLongOpt("filter")
				 .create("f"));
		options.addOption(OptionBuilder.withLongOpt("all")
		                 .hasArg(false)
				 .withDescription("Include all tests")
				 .create("a"));
	}

	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("a"))
		{
			m_filters.add(new AllInclusiveTestCaseFilter());
		}

		String[] ids = options.getOptionValues("id");
		if(ids == null || ids.length < 1)
		{
			logger.debug("There are no test ids to load.");
		}
		else
		{
			for(String id : ids)
			{
				m_filters.add(new TestCaseIdFilter("id:" + id));
			}
			logger.debug("There are '{}' test id filters from the --id option.", m_filters.size());
		}

		String[] filters = options.getOptionValues("filter");
		if(filters == null || filters.length < 1)
		{
			logger.debug("There are no test case filters passed in through the command line.");
		} else
		{
			logger.debug("Getting a list of FilterPlugins from PluginManager");
			PluginManager pluginmgr = PluginManagerFactory.getPluginManager();
			List<FilterPlugin> filterPlugins = pluginmgr.getPluginsFor(FilterPlugin.class);
			logger.debug("There are '{}' FilterPlugins.", filterPlugins.size());
			for(String filter : filters)
			{
				TestCaseFilter filterObj = null;
				logger.debug("Looking for a test case filter for '{}'", filter);
				for(FilterPlugin plugin : filterPlugins)
				{
					logger.debug("Calling parseFilterString(\"{}\") for plugin '{}' with class name '{}'.", new Object[] {filter, plugin.getPluginName(), plugin.getClass().getName()});
					filterObj = plugin.parseFilterString(filter);
					if(filterObj != null)
					{
						logger.debug("Found TestCaseFilter '{}' for filter '{}'.", filterObj.getClass().getName(), filter);
						break;
					}
				}
				if(filterObj == null)
				{
					logger.warn("Unknown filter '{}' will be unused.", filter);
				} else
				{
					m_filters.add(filterObj);
				}
			}
		}
	}
}
