/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.plugins.basic;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.PluginManager;
import org.tcrun.api.PluginManagerFactory;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseFilter;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.FilterListPlugin;
import org.tcrun.api.plugins.FilterPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({FilterListPlugin.class, CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class})
public class CommandLineOptionFilterListPlugin implements FilterListPlugin, CommandLineOptionPlugin, CommandLineConsumerPlugin
{
	private static XLogger logger = XLoggerFactory.getXLogger(CommandLineOptionFilterListPlugin.class);
	List<TestCaseFilter> filters;

	public CommandLineOptionFilterListPlugin()
	{
		filters = new ArrayList<TestCaseFilter>();
	}

	@Override
	public List<TestCaseFilter> getFilters(TCRunContext p_context)
	{
		return filters;
	}

	@Override
	public String getPluginName()
	{
		return "Command Line Option Filter List Plugin";
	}

	@Override
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

	@Override
	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("a"))
		{
			filters.add(new AllInclusiveTestCaseFilter());
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
				filters.add(new TestCaseIdFilter("id:" + id));
			}
			logger.debug("There are '{}' test id filters from the --id option.", ids.length);
		}

		String[] filter_strings = options.getOptionValues("filter");
		if(filter_strings == null || filter_strings.length < 1)
		{
			logger.debug("There are no test case filters passed in through the command line.");
		} else
		{
			logger.debug("Getting a list of FilterPlugins from PluginManager");
			PluginManager pluginmgr = PluginManagerFactory.getPluginManager();
			List<FilterPlugin> filterPlugins = pluginmgr.getPluginsFor(FilterPlugin.class);
			logger.debug("There are '{}' FilterPlugins.", filterPlugins.size());
			for(String filter : filter_strings)
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
					logger.warn("Unknown filter '{}' will be not be used.", filter);
				} else
				{
					filters.add(filterObj);
				}
			}
		}
	}
}
