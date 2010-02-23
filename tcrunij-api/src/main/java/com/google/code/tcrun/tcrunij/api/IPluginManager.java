/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.tcrun.tcrunij.api;

import java.util.List;

/**
 * A plugin manager keeps track of what plugins are available.  You can get an instance
 * of a plugin manager by using the PluginManagerFactory.
 *
 * @author jcorbett
 */
public interface IPluginManager
{
	public void initialize(IRuntimeInformation runtime_information);

	public <T extends IPlugin> List<T> getPluginsFor(Class<T> clazz);

	public <T extends IPlugin> void addPlugin(Class<T> plugin_interface, T implementation);
}
