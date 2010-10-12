package org.tcrun.plugins.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
public class BasicTestPlanPlugin implements FilterListPlugin, CommandLineOptionPlugin, CommandLineConsumerPlugin
{
	private String planName = null;
	private static XLogger logger = XLoggerFactory.getXLogger(BasicTestPlanPlugin.class);

	@Override
	public List<TestCaseFilter> getFilters(TCRunContext p_context)
	{
		if(planName == null)
			return null;

		File planFile = new File(new File(p_context.getTCRunRoot(), "plans"), planName + ".txt");
		if(!planFile.exists())
		{
			logger.error("Cannot find file for plan name {}, '{}' does not exist.", planName, planFile.getAbsolutePath());
			System.err.println(String.format("Unable to find plan {}, '{}' does not exist", planName, planFile.getAbsolutePath()));
			System.exit(1);
		}
		List<TestCaseFilter> filters = new ArrayList<TestCaseFilter>();
		List<FilterPlugin> filterPlugins = PluginManagerFactory.getPluginManager().getPluginsFor(FilterPlugin.class);

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(planFile));
			String line;
			while ((line = in.readLine()) != null)
			{
				// ignore comments
				if(line.startsWith("#"))
					continue;
				if(line.isEmpty())
					continue;
				TestCaseFilter filterObj = null;
				logger.debug("Looking for a test case filter for '{}'", line);
				for(FilterPlugin plugin : filterPlugins)
				{
					logger.debug("Calling parseFilterString(\"{}\") for plugin '{}' with class name '{}'.", new Object[] {line, plugin.getPluginName(), plugin.getClass().getName()});
					filterObj = plugin.parseFilterString(line);
					if(filterObj != null)
					{
						logger.debug("Found TestCaseFilter '{}' for filter '{}'.", filterObj.getClass().getName(), line);
						break;
					}
				}
				if(filterObj == null)
				{
					logger.warn("Unknown filter '{}' will be not be used.", line);
				} else
				{
					filters.add(filterObj);
				}
			}
			in.close();
		} catch (IOException e)
		{
			logger.error("Encountered an IOException while reading file " + planFile.getAbsolutePath(), e);
			System.err.println("Unable to read file " + planFile.getAbsolutePath() + ", see log for details.");
			System.exit(1);
		}

		return filters;
	}

	@Override
	public String getPluginName()
	{
		return "Basic Test Plan Plugin";
	}

	@Override
	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.hasArg(true)
		                  .withLongOpt("plan")
						  .withDescription("Specify a test plan in the plans directory that has the list of filters to run.")
						  .withArgName("PLANNAME")
						  .create("p"));
	}

	@Override
	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("plan"))
		{
			planName = options.getOptionValue("plan");
		}
	}
}
