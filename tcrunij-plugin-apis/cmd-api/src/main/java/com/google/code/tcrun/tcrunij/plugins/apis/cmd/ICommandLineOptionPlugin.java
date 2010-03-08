/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.tcrun.tcrunij.plugins.apis.cmd;

import com.google.code.tcrun.tcrunij.api.IPlugin;
import org.apache.commons.cli.Options;


/**
 *
 * @author jcorbett
 */
public interface ICommandLineOptionPlugin extends IPlugin
{
	void addToCommandLineParser(Options options);
}

