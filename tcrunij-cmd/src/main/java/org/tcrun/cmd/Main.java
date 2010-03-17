/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.cmd;

import org.tcrun.api.PluginManager;
import org.tcrun.api.PluginManagerFactory;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;
import java.util.List;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.plugins.StartupError;
import org.tcrun.api.plugins.StartupTaskPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.ConfigurationOverridePlugin;
import org.tcrun.api.plugins.ConfigurationSourcePlugin;

/**
 *
 * @author jcorbett
 */
public class Main
{
	public static void main(String[] args)
	{
		// Get Context
		TCRunContext context = TCRunContextFactory.getNewContext();
		XLogger logger = XLoggerFactory.getXLogger(Main.class.getCanonicalName() + "#main");

		logger.debug("Initializing Plugin Manager.");
		PluginManager plugin_manager = PluginManagerFactory.getPluginManager();

		logger.debug("Scanning for plugins...");
		ClassPathResourceScanner classpath_scanner = new ClassPathResourceScanner();
		classpath_scanner.scan(plugin_manager, context);
		logger.debug("Done scanning for plugins.");


		logger.debug("Initializing command line options by going through CommandLineOptionPlugin's.");
		List<CommandLineOptionPlugin> option_plugins = plugin_manager.getPluginsFor(CommandLineOptionPlugin.class);
		Options options = new Options();
		logger.debug("There are '{}' command line option plugins, looping through them all.", option_plugins.size());
		for(CommandLineOptionPlugin plugin: option_plugins)
		{
			logger.debug("Calling addToCommandLineParser() on plugin class '{}'.", plugin.getClass().getCanonicalName());
			plugin.addToCommandLineParser(options);
			logger.debug("There are now '{}' command line options.", options.getOptions().size());
		}
		logger.debug("Done looping through command line option plugins.");


		logger.debug("Parsing command line arguments.");
		PosixParser pparser = new PosixParser();
		CommandLine cmdline = null;
		try
		{
			cmdline = pparser.parse(options, args);
		} catch(ParseException ex)
		{
			logger.error("Error with command line arguments to tcrunij: ", ex);
			System.err.println("Error with commandline arguments: " + ex.getMessage());
			HelpFormatter help = new HelpFormatter();
			help.printHelp("tcrunij [options]", options);
			System.exit(1);
		}
		logger.debug("Successfully parsed command line arguments.");


		List<CommandLineConsumerPlugin> cmdline_plugins = plugin_manager.getPluginsFor(CommandLineConsumerPlugin.class);
		logger.debug("There are '{}' command line consumer plugins, looping through them all.", cmdline_plugins.size());
		for(CommandLineConsumerPlugin plugin : cmdline_plugins)
		{
			logger.debug("Calling consumeCommandLineOptions() on plugin class '{}'.", plugin.getClass().getCanonicalName());
			plugin.consumeCommandLineOptions(cmdline);
			logger.debug("Done calling consumeCommandLineOptions() on plugin class '{}'.", plugin.getClass().getCanonicalName());
		}
		logger.debug("Done looping through command line consumer plugins.");


		List<ConfigurationSourcePlugin> configuration_sources = plugin_manager.getPluginsFor(ConfigurationSourcePlugin.class);
		logger.debug("There are '{}' configuration source plugins, looping through them all.", configuration_sources.size());
		for(ConfigurationSourcePlugin plugin : configuration_sources)
		{
			logger.debug("Calling loadConfiguration() on plugin class '{}'.", plugin.getClass().getCanonicalName());
			plugin.loadConfiguration(context);
			logger.debug("Done calling loadConfiguration() on plugin class '{}'.", plugin.getClass().getCanonicalName());
			logger.debug("There are now '{}' tcrun configuration entries and '{}' test case configuration entries.", context.getTCRunConfiguration().size(), context.getTestCaseConfiguration().size());
		}
		logger.debug("Done looping through configuration source plugins.");


		List<ConfigurationOverridePlugin> configuration_overrides = plugin_manager.getPluginsFor(ConfigurationOverridePlugin.class);
		logger.debug("There are '{}' configuration override plugins, looping through them all.", configuration_overrides.size());
		for(ConfigurationOverridePlugin plugin : configuration_overrides)
		{
			logger.debug("Calling handleConfiguration() on plugin class '{}'.", plugin.getClass().getCanonicalName());
			plugin.handleConfiguration(context);
			logger.debug("Done calling handleConfiguration() on plugin class '{}'.", plugin.getClass().getCanonicalName());
			logger.debug("There are now '{}' tcrun configuration entries and '{}' test case configuration entries.", context.getTCRunConfiguration().size(), context.getTestCaseConfiguration().size());
		}
		logger.debug("Done looping through configuration override plugins.");

		List<StartupTaskPlugin> startup_tasks = plugin_manager.getPluginsFor(StartupTaskPlugin.class);
		logger.debug("There are '{}' startup task plugins, looping through them all.", startup_tasks.size());
		for(StartupTaskPlugin plugin : startup_tasks)
		{
			logger.debug("Calling onStartup() on plugin class '{}'.", plugin.getClass().getCanonicalName());
			try
			{
				plugin.onStartup(context);
			} catch(StartupError error)
			{
				// warn and above go to the console, we don't need that.
				logger.info("Startup task '" + plugin.getPluginName() + "' with a class name of '" + plugin.getClass().getCanonicalName() + "' threw a startup error, exiting tcrunij.", error);
				System.err.println(error.getMessage());
				System.exit(1);
			}
			logger.debug("Done calling onStartup() on plugin class '{}'.", plugin.getClass().getCanonicalName());
		}
		logger.debug("Done looping through startup task plugins.");



		System.out.println("You've reached the end of tcrunij.  Bye.");
		System.exit(0);
	}
}
