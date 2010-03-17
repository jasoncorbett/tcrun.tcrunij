/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.apache.commons.cli.CommandLine;

/**
 * A plugin that will get called when all the command line options are passed.
 * @author jcorbett
 */
public interface CommandLineConsumerPlugin extends Plugin
{
	void consumeCommandLineOptions(CommandLine options);
}
