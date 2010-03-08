/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.tcrun.tcrunij.api;

import org.apache.commons.cli.Options;


/**
 *
 * @author jcorbett
 */
public interface ICommandLineOptionPlugin extends IPlugin
{
	void addToCommandLineParser(Options options);
}

