/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.plugins.cmdOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.ConfigurationOverridePlugin;
import org.tcrun.api.ImplementsPlugin;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({CommandLineConsumerPlugin.class, CommandLineOptionPlugin.class, ConfigurationOverridePlugin.class})
public class CommandLineTestCaseConfigurationPlugin implements CommandLineConsumerPlugin, CommandLineOptionPlugin, ConfigurationOverridePlugin
{
	private Map<String,String> config;
	private static XLogger logger = XLoggerFactory.getXLogger(CommandLineTestCaseConfigurationPlugin.class);

	public void consumeCommandLineOptions(CommandLine options)
	{
		logger.debug("Gathering data from command line options.");
		config = new HashMap<String, String>();
		Properties props = options.getOptionProperties("o");
		logger.debug("There are '{}' options obtained from the command line for test cases.", props.size());
		for(Object key :props.keySet())
		{
			String value = (String)props.get(key);
			logger.debug("Parsed command line option (for test case): '{}' with value '{}'.", key, value);
			config.put((String)key, value);
		}
	}

	public String getPluginName()
	{
		return "Command Line Test Case Configuration Plugin";
	}

	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.withArgName("name=value")
                                 .hasArgs(2)
                                 .withValueSeparator()
                                 .withDescription("Set an option for the test case.")
				 .withLongOpt("option")
                                 .create("o"));
	}

	public void handleConfiguration(TCRunContext context)
	{
		logger.debug("Adding '{}' test case configuration items to the context.", config.size());
		context.getTestCaseConfiguration().putAll(config);
	}
}
