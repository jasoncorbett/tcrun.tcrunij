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
	static private PluginManager s_plugin_manager = null;

	static public PluginManager getPluginManager()
	{
		synchronized(PluginManagerFactory.class)
		{
			if(s_plugin_manager == null)
			{
				s_plugin_manager = new DefaultPluginManager();
			}
		}

		return s_plugin_manager;
	}

	static protected void setPluginManager(PluginManager plugin_manager)
	{
		synchronized(PluginManagerFactory.class)
		{
			s_plugin_manager = plugin_manager;
		}
	}

}
