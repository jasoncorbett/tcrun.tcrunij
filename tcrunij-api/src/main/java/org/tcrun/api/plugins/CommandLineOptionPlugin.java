/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.apache.commons.cli.Options;


/**
 *
 * @author jcorbett
 */
public interface CommandLineOptionPlugin extends Plugin
{
	void addToCommandLineParser(Options options);
}

