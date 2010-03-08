/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.tcrun.tcrunij.cmd;

import com.google.code.tcrun.tcrunij.api.ICommandLineConsumerPlugin;
import com.google.code.tcrun.tcrunij.api.ICommandLineOptionPlugin;
import com.google.code.tcrun.tcrunij.api.ImplementsPlugin;
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
