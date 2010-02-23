/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.tcrun.tcrunij.api;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author jcorbett
 */
public class PluginManagerTest
{
	private PluginManager manager = null;

	@Before
	public void setup()
	{
		manager = new PluginManager();
	}

	@Test
	public void addAndRetrieve()
	{
		TPlugin test_plugin = new TPlugin();
		manager.addPlugin(ITestPlugin.class, test_plugin);
		List<ITestPlugin> plugin_list = manager.getPluginsFor(ITestPlugin.class);
		assertNotNull("getPluginsFor should not have returned null.", plugin_list);
		assertEquals("There should be only 1 plugin in the list (only 1 was added).", 1, plugin_list.size());
		assertSame("The plugin that was added via addPlugin should be the exact same one retrieved.", test_plugin, plugin_list.get(0));
	}

	@Test
	public void getPluginsForNotNull()
	{
		List<ITestPlugin> plugin_list = manager.getPluginsFor(ITestPlugin.class);
		assertNotNull("getPluginsFor should not return null.", plugin_list);
		assertEquals("getPluginsFor should return an empty list when nothing has been added.", 0, plugin_list.size());
	}
}


