/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.cmd;

import org.tcrun.api.IPluginManager;
import org.tcrun.api.PluginManagerFactory;
import org.tcrun.plugins.apis.cmd.ICommandLineOptionPlugin;
import org.tcrun.plugins.apis.cmd.ICommandLineConsumerPlugin;
import org.tcrun.api.IRuntimeInformation;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import java.util.List;
import org.apache.commons.cli.HelpFormatter;

/**
 *
 * @author jcorbett
 */
public class Main
{
	public static void main(String[] args)
	{
		// Initialize plugins
		IPluginManager plugin_manager = PluginManagerFactory.getPluginManager();
		ClassPathResourceScanner classpath_scanner = new ClassPathResourceScanner();
		IRuntimeInformation info = new BasicRuntimeInfo();
		classpath_scanner.scan(plugin_manager, info);

		// Parse the command line
		List<ICommandLineOptionPlugin> option_plugins = plugin_manager.getPluginsFor(ICommandLineOptionPlugin.class);
		Options options = new Options();
		for(ICommandLineOptionPlugin plugin: option_plugins)
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

		List<ICommandLineConsumerPlugin> cmdline_plugins = plugin_manager.getPluginsFor(ICommandLineConsumerPlugin.class);
		for(ICommandLineConsumerPlugin plugin : cmdline_plugins)
		{
			plugin.consumeCommandLineOptions(cmdline);
		}


		System.out.println("You've reached the end of tcrunij.  Bye.");
		System.exit(0);
	}
}
