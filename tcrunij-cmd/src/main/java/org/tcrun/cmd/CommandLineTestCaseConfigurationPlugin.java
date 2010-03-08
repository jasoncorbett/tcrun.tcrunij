/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.cmd;

import org.tcrun.plugins.apis.cmd.ICommandLineConsumerPlugin;
import org.tcrun.plugins.apis.cmd.ICommandLineOptionPlugin;
import org.tcrun.api.ImplementsPlugin;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({ICommandLineConsumerPlugin.class, ICommandLineOptionPlugin.class})
public class CommandLineTestCaseConfigurationPlugin implements ICommandLineConsumerPlugin, ICommandLineOptionPlugin
{

	public void consumeCommandLineOptions(CommandLine options)
	{
		options.getOptionProperties("o").list(System.out);
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
}
