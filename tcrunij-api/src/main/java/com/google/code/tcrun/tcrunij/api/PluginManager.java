/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.tcrun.tcrunij.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author jcorbett
 */
public class PluginManager implements IPluginManager
{
	private Map<Class, List> m_plugins;
	private IRuntimeInformation m_runtime_info;

	public PluginManager()
	{
		m_plugins = new HashMap<Class, List>();
	}

	public void initialize(IRuntimeInformation info)
	{
		m_runtime_info = info;
	}

	public <T extends IPlugin> List<T> getPluginsFor(Class<T> clazz)
	{
		List<T> retval = new Vector<T>();
		synchronized(PluginManager.class)
		{
			if(m_plugins.containsKey(clazz))
			{
				retval.addAll(m_plugins.get(clazz));
			}
		}
		return retval;
	}

	public void addPlugin(Class<?> plugin_interface, Object implementation)
	{
		synchronized(PluginManager.class)
		{
			if(m_plugins.containsKey(plugin_interface))
			{
				m_plugins.get(plugin_interface).add(implementation);
			} else
			{
				List<Object> plugin_list = new Vector<Object>();
				plugin_list.add(implementation);
				m_plugins.put(plugin_interface, plugin_list);
			}
		}
	}
}
