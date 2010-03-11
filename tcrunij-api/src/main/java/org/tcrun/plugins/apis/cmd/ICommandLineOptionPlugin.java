/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.plugins.apis.cmd;

import org.tcrun.api.IPlugin;
import org.apache.commons.cli.Options;


/**
 *
 * @author jcorbett
 */
public interface ICommandLineOptionPlugin extends IPlugin
{
	void addToCommandLineParser(Options options);
}

