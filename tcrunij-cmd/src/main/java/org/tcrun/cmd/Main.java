/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.cmd;

import java.util.Collections;
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
import java.util.Vector;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.plugins.StartupError;
import org.tcrun.api.plugins.StartupTaskPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.AfterTestCasePlugin;
import org.tcrun.api.plugins.AfterTestListRunnerPlugin;
import org.tcrun.api.plugins.BeforeTestCasePlugin;
import org.tcrun.api.plugins.BeforeTestListRunnerPlugin;
import org.tcrun.api.plugins.ConfigurationOverridePlugin;
import org.tcrun.api.plugins.ConfigurationSourcePlugin;
import org.tcrun.api.plugins.ShutdownTaskPlugin;
import org.tcrun.api.plugins.TestListPlugin;
import org.tcrun.api.plugins.TestListRunnerPlugin;
import org.tcrun.api.plugins.TestLoaderPlugin;

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

		// Get a list of test loader plugins
		List<TestLoaderPlugin> test_loaders = plugin_manager.getPluginsFor(TestLoaderPlugin.class);
		logger.debug("There are '{}' test loaders.", test_loaders.size());

		// Get a list of test list plugins
		List<TestListPlugin> test_list_plugins = plugin_manager.getPluginsFor(TestListPlugin.class);
		logger.debug("There are '{}' test list plugins, looping through them all.", test_list_plugins.size());
		List<RunnableTest> test_list = new Vector<RunnableTest>();
		for(TestListPlugin plugin : test_list_plugins)
		{
			// call each test list plugin
			logger.debug("Calling getTests() on test list plugin '{}' with class name of '{}'.", plugin.getPluginName(), plugin.getClass().getCanonicalName());
			List<RunnableTest> tests = plugin.getTests(context, test_loaders);
			if(tests == null || tests.isEmpty())
			{
				logger.info("Test List Plugin '{}' returned no tests.", plugin.getPluginName());
			} else
			{
				logger.info("Test List Plugin '{}' returned '{}' tests.", plugin.getPluginName(), tests.size());
				test_list.addAll(tests);
			}
			logger.debug("Done calling getTests() on test list plugin with class name '{}'.", plugin.getClass().getCanonicalName());
		}
		logger.debug("Done looping through all '{}' test list plugin(s).", test_list_plugins.size());
		logger.info("There are '{}' test(s) total to be run.", test_list.size());

		// Get a list of Test List Runners

		List<TestListRunnerPlugin> test_list_runners = plugin_manager.getPluginsFor(TestListRunnerPlugin.class);
		TestListRunnerPlugin list_runner = null;
		logger.debug("There are '{}' test_list_runners, there can be only one.", test_list_runners.size());
		if(test_list_runners.size() > 1)
		{
			// Sort the list of test list runners
			logger.debug("Sorting the list to find the one with the highest importance rating.");
			Collections.sort(test_list_runners, new CompareByImportanceRating());
			list_runner = test_list_runners.get(test_list_runners.size() - 1);

		} else if(test_list_runners.size() == 1)
		{
			logger.debug("There is only 1 test list runner plugin, choosing it as the runner.");
			list_runner = test_list_runners.get(0);
		} else
		{
			logger.error("There are no Test List Runner plugins, there must be something wrong with the installation.");
			System.err.println("There are no Test List Runner plugins, there must be something wrong with the installation.");
			System.exit(1);
		}
		logger.info("The test list runner is '{}' implemented by class '{}'.", list_runner.getPluginName(), list_runner.getClass().getCanonicalName());

		// Initialize and call the top test list runner
		logger.debug("Initializing the list runner.");
		list_runner.initialize(context);
		logger.debug("Adding all '{}' test(s) to be run to the test list runner.", test_list.size());
		list_runner.addTests(test_list);

		List<BeforeTestListRunnerPlugin> before_test_list_runner_plugins = plugin_manager.getPluginsFor(BeforeTestListRunnerPlugin.class);
		logger.debug("There are '{}' BeforeTestListRunner plugins, looping through them all.", before_test_list_runner_plugins.size());
		for(BeforeTestListRunnerPlugin plugin : before_test_list_runner_plugins)
		{
			logger.debug("Calling beforeTestListRunnerExecutes() on plugin class '{}'.", plugin.getClass().getCanonicalName());
			try
			{
				plugin.beforTestListRunnerExecutes(context, list_runner);
			} catch(StartupError error)
			{
				// warn and above go to the console, we don't need that.
				logger.info("BeforeTestListRunner plugin '" + plugin.getPluginName() + "' with a class name of '" + plugin.getClass().getCanonicalName() + "' threw a startup error, exiting tcrunij.", error);
				System.err.println(error.getMessage());
				System.exit(1);
			}
			logger.debug("Done calling beforeTestListRunnerExecutes() on plugin class '{}'.", plugin.getClass().getCanonicalName());
		}
		logger.debug("Done looping through BeforeTestListRunner plugins.");

		logger.debug("Gathering before and after test case plugins.");
		List<BeforeTestCasePlugin> before_tc_plugins = plugin_manager.getPluginsFor(BeforeTestCasePlugin.class);
		List<AfterTestCasePlugin> after_tc_plugins = plugin_manager.getPluginsFor(AfterTestCasePlugin.class);
		logger.debug("There are '{}' before test case plugins and '{}' after test case plugins.", before_tc_plugins.size(), after_tc_plugins.size());
		logger.info("Calling runTests on test list runner '{}' implemented by class '{}'.", list_runner.getPluginName(), list_runner.getClass().getCanonicalName());
		list_runner.runTests(before_tc_plugins, after_tc_plugins);
		logger.info("Done calling runTests on test list runner.");

		List<AfterTestListRunnerPlugin> aftertlr_plugins = plugin_manager.getPluginsFor(AfterTestListRunnerPlugin.class);
		logger.debug("There are '{}' after test list plugins, looping through them all.", aftertlr_plugins.size());
		for(AfterTestListRunnerPlugin plugin : aftertlr_plugins)
		{
			logger.debug("Calling afterTestListRunnerHasExecuted(context, list_runner) on plugin '{}' with class '{}'.", plugin.getPluginName(), plugin.getClass().getName());
			plugin.afterTestListRunnerHasExecuted(context, list_runner);
			logger.debug("Done calling afterTestListRunnerHasExecuted(context, list_runner) on plugin '{}' with class '{}'.", plugin.getPluginName(), plugin.getClass().getName());
		}
		logger.debug("Done looping through after test list runner plugins.");

		List<ShutdownTaskPlugin> shutdown_tasks = plugin_manager.getPluginsFor(ShutdownTaskPlugin.class);
		logger.debug("There are '{}' shutdown task plugins, looping through them all.", shutdown_tasks.size());
		for(ShutdownTaskPlugin plugin : shutdown_tasks)
		{
			logger.debug("Calling onShutdown() on plugin '{}' with class '{}'.", plugin.getPluginName(), plugin.getClass().getCanonicalName());
			plugin.onShutdown(context);
			logger.debug("Done calling onShutdown() on plugin '{}' with class '{}'.", plugin.getPluginName(), plugin.getClass().getCanonicalName());
		}
		logger.debug("Done looping through shutdown task plugins.");


		System.exit(0);
	}
}
