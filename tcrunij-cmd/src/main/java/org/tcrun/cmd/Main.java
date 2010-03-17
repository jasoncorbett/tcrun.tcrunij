/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.cmd;

import org.tcrun.api.PluginManager;
import org.tcrun.api.PluginManagerFactory;
import org.tcrun.plugins.apis.cmd.CommandLineOptionPlugin;
import org.tcrun.plugins.apis.cmd.CommandLineConsumerPlugin;
import org.tcrun.api.RuntimeInformation;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;
import org.slf4j.MDC;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author jcorbett
 */
public class Main
{
	public static void main(String[] args)
	{
		// Initialize logging parameters
		String runid = System.getProperty("TESTRUNID");
		if(runid == null)
		{
			runid = System.getenv("TESTRUNID");
		}

		// this is not an else if on purpose, because after the first if runid may still be null.
		if(runid == null)
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			runid = format.format(new Date());
		}
		// this is usable from within logging
		MDC.put("TestRunId", runid);

		// Initialize plugins
		PluginManager plugin_manager = PluginManagerFactory.getPluginManager();
		ClassPathResourceScanner classpath_scanner = new ClassPathResourceScanner();
		RuntimeInformation info = new BasicRuntimeInfo();
		classpath_scanner.scan(plugin_manager, info);

		// Parse the command line
		List<CommandLineOptionPlugin> option_plugins = plugin_manager.getPluginsFor(CommandLineOptionPlugin.class);
		Options options = new Options();
		options.addOption("h", "help", false, "This help message.");
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

		if(cmdline.hasOption("h"))
		{
			HelpFormatter help = new HelpFormatter();
			help.printHelp("tcrunij [options]", options);
			System.exit(0);
		}

		List<CommandLineConsumerPlugin> cmdline_plugins = plugin_manager.getPluginsFor(CommandLineConsumerPlugin.class);
		for(CommandLineConsumerPlugin plugin : cmdline_plugins)
		{
			plugin.consumeCommandLineOptions(cmdline);
		}



		System.out.println("You've reached the end of tcrunij.  Bye.");
		System.exit(0);
	}
}
