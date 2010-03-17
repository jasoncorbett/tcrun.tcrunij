/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.cmd;

import org.tcrun.api.PluginManager;
import org.tcrun.api.PluginManagerFactory;
import org.tcrun.plugins.apis.cmd.CommandLineOptionPlugin;
import org.tcrun.plugins.apis.cmd.CommandLineConsumerPlugin;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;
import java.util.List;
import org.tcrun.api.StartupError;
import org.tcrun.api.StartupTaskPlugin;
import org.tcrun.api.TCRunContext;

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

		// Initialize plugins
		PluginManager plugin_manager = PluginManagerFactory.getPluginManager();
		ClassPathResourceScanner classpath_scanner = new ClassPathResourceScanner();
		classpath_scanner.scan(plugin_manager, context);

		// Parse the command line
		List<CommandLineOptionPlugin> option_plugins = plugin_manager.getPluginsFor(CommandLineOptionPlugin.class);
		Options options = new Options();
		for(CommandLineOptionPlugin plugin: option_plugins)
		{
			plugin.addToCommandLineParser(options);
		}

		PosixParser pparser = new PosixParser();
		CommandLine cmdline = null;
		try
		{
			cmdline = pparser.parse(options, args);
		} catch(ParseException ex)
		{
			System.err.println("Error with commandline arguments: " + ex.getMessage());
			HelpFormatter help = new HelpFormatter();
			help.printHelp("tcrunij [options]", options);
			System.exit(1);
		}

		List<CommandLineConsumerPlugin> cmdline_plugins = plugin_manager.getPluginsFor(CommandLineConsumerPlugin.class);
		for(CommandLineConsumerPlugin plugin : cmdline_plugins)
		{
			plugin.consumeCommandLineOptions(cmdline);
		}

		List<StartupTaskPlugin> startup_tasks = plugin_manager.getPluginsFor(StartupTaskPlugin.class);
		for(StartupTaskPlugin plugin : startup_tasks)
		{
			try
			{
				plugin.onStartup(context);
			} catch(StartupError error)
			{
				System.err.println(error.getMessage());
				System.exit(1);
			}
		}



		System.out.println("You've reached the end of tcrunij.  Bye.");
		System.exit(0);
	}
}
