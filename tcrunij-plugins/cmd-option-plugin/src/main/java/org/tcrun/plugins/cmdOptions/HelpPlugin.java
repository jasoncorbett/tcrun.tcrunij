/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.plugins.cmdOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.plugins.apis.cmd.CommandLineOptionPlugin;
import org.tcrun.plugins.apis.cmd.CommandLineConsumerPlugin;
import org.tcrun.api.StartupTaskPlugin;
import org.tcrun.api.StartupError;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class, StartupTaskPlugin.class})
public class HelpPlugin implements CommandLineOptionPlugin, CommandLineConsumerPlugin, StartupTaskPlugin
{
	private Options m_options;
	private boolean m_help_selected = false;

	public void addToCommandLineParser(Options options)
	{
		m_options = options;
		options.addOption(OptionBuilder.withDescription("Get Command Line Help.")
				 .withLongOpt("help")
				 .hasArg(false)
                                 .create("h"));

	}

	public String getPluginName()
	{
		return "Command Line Help Plugin";
	}

	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("h"))
			m_help_selected = true;
	}

	public void onStartup(TCRunContext context) throws StartupError
	{
		if(m_help_selected)
		{
			HelpFormatter help = new HelpFormatter();
			help.printHelp("tcrunij [options]", m_options);
			throw new StartupError("Exiting after help printout.");
		}
	}

}
