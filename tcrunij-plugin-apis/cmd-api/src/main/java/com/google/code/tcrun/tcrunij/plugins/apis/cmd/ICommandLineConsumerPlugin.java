/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.tcrun.tcrunij.plugins.apis.cmd;

import com.google.code.tcrun.tcrunij.api.IPlugin;
import org.apache.commons.cli.CommandLine;

/**
 * A plugin that will get called when all the command line options are passed.
 * @author jcorbett
 */
public interface ICommandLineConsumerPlugin extends IPlugin
{
	void consumeCommandLineOptions(CommandLine options);
}
