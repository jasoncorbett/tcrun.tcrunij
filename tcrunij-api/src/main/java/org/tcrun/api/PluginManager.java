/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.api;

import java.util.List;

/**
 * A plugin manager keeps track of what plugins are available.  You can get an instance
 * of a plugin manager by using the PluginManagerFactory.
 *
 * @author jcorbett
 */
public interface PluginManager
{
	public void initialize(RuntimeInformation runtime_information);

	public <T extends Plugin> List<T> getPluginsFor(Class<T> clazz);

	public void addPlugin(Class<?> plugin_interface, Object implementation);
}
