package org.tcrun.plugins.basic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.ConfigurationSourcePlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class, ConfigurationSourcePlugin.class})
public class BasicTestCaseEnvironment implements CommandLineOptionPlugin, CommandLineConsumerPlugin, ConfigurationSourcePlugin
{
	private static XLogger logger = XLoggerFactory.getXLogger(BasicTestCaseEnvironment.class);
	private String test_config_name;

	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.hasArg(true)
		                  .withDescription("Environment Name (file inside conf directory with .ini extension).")
				  .withLongOpt("environment")
				  .create("e"));
	}

	public String getPluginName()
	{
		return "Basic Test Case Environment Plugin";
	}

	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("e"))
		{
			test_config_name = options.getOptionValue("e");
		} else
		{
			test_config_name = null;
		}
	}

	public void loadConfiguration(TCRunContext context)
	{
		if(test_config_name != null)
		{
			logger.debug("Attempting to load test environment config with name {}", test_config_name);
			File env_file = new File(new File(context.getTCRunRoot(), "conf"), test_config_name + ".ini");
			if(env_file.exists())
			{
				logger.debug("Found test environment file {}.", env_file.getAbsolutePath());
				try
				{
					Ini env = new Ini(new FileReader(env_file));
					for(String sectionName : env.keySet())
					{
						Section section = env.get(sectionName);
						for(String key : section.keySet())
						{
							context.getTestCaseConfiguration().put(sectionName + "." + key, section.fetch(key));
						}
					}
				} catch(IOException ex)
				{
					logger.error("Unable to read file " + env_file.getAbsolutePath() + ".", ex);
					System.out.println("Unable to read environment file " + env_file.getAbsolutePath() + ": " + ex.getMessage());
					System.exit(1);
				}
			} else
			{
				String message = String.format("Environment {} is invalid, file does not exist: {}.", test_config_name, env_file.getAbsolutePath());
				logger.error(message);
				System.out.println(message);
				System.exit(1);
			}
		} else
		{
			logger.debug("No test case environment specified.");
		}
	}
}
