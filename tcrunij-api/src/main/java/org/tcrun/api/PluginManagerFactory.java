/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tcrun.api;

/**
 *
 * @author jcorbett
 */
public class PluginManagerFactory
{
	static private IPluginManager s_plugin_manager = null;

	static public IPluginManager getPluginManager()
	{
		synchronized(PluginManagerFactory.class)
		{
			if(s_plugin_manager == null)
			{
				s_plugin_manager = new PluginManager();
			}
		}

		return s_plugin_manager;
	}

	static protected void setPluginManager(IPluginManager plugin_manager)
	{
		synchronized(PluginManagerFactory.class)
		{
			s_plugin_manager = plugin_manager;
		}
	}

}
