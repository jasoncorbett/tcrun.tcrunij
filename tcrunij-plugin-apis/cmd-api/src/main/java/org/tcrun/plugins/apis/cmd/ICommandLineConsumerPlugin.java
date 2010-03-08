/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tcrun.plugins.apis.cmd;

import org.tcrun.api.IPlugin;
import org.apache.commons.cli.CommandLine;

/**
 * A plugin that will get called when all the command line options are passed.
 * @author jcorbett
 */
public interface ICommandLineConsumerPlugin extends IPlugin
{
	void consumeCommandLineOptions(CommandLine options);
}
