/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.tcrun.tcrunij.cmd;

import com.google.code.tcrun.tcrunij.api.IPluginManager;
import com.google.code.tcrun.tcrunij.api.IPluginScanner;
import com.google.code.tcrun.tcrunij.api.IRuntimeInformation;

/**
 * Scan for plugins by looking for a plain text file in the classpath.
 * Every time a file matching the resource name is found, it is read and
 * each line is assumed to be a class name to look at for plugin
 * implementations.
 *
 * Each class is checked for any interfaces it implements that are
 * subclasses of the IPlugin interface.  An instance is then created,
 * and it is added for each plugin interface it implements.
 *
 * @author jcorbett
 */
public class ClassPathResourceScanner implements IPluginScanner
{
	/**
	 * This is the default name of the resource name that is searched for
	 * in the classpath.  Every file with this name is loaded.
	 */
	public static final String DEFAULT_RESOURCE_NAME = "tcrunij.plugin";

	public void scan(IPluginManager manager, IRuntimeInformation runtime_info)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
