/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.cmd;

import org.tcrun.api.IPlugin;
import org.tcrun.api.IPluginManager;
import org.tcrun.api.IPluginScanner;
import org.tcrun.api.IRuntimeInformation;
import org.tcrun.api.ImplementsPlugin;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

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
	private static XLogger logger = XLoggerFactory.getXLogger(ClassPathResourceScanner.class);

	/**
	 * This is the default name of the resource name that is searched for
	 * in the classpath.  Every file with this name is loaded.
	 */
	public static final String DEFAULT_RESOURCE_NAME = "plugin";
	private String m_resource_name;
	private ClassLoader m_class_loader;

	public ClassPathResourceScanner()
	{
		this(ClassPathResourceScanner.DEFAULT_RESOURCE_NAME, ClassPathResourceScanner.class.getClassLoader());
	}

	public ClassPathResourceScanner(String resource_name)
	{
		this(resource_name, ClassPathResourceScanner.class.getClassLoader());
	}

	public ClassPathResourceScanner(String resource_name, ClassLoader class_loader)
	{
		logger.entry(resource_name, class_loader);
		m_resource_name = resource_name;
		m_class_loader = class_loader;
	}

	public void scan(IPluginManager manager, IRuntimeInformation runtime_info)
	{
		// with the manager, we're more interested in the classname of the plugin manager than 
		// it's toString method
		logger.entry(manager.getClass(), runtime_info);
		try
		{
			logger.debug("Looking for plugin resource files with the name '{}'.", m_resource_name);
			Enumeration<URL> identifier_urls = m_class_loader.getResources(m_resource_name);
			while(identifier_urls.hasMoreElements())
			{
				URL identifier_url = identifier_urls.nextElement();
				logger.debug("Reading from plugin resource file '{}'.", identifier_url);
				LineNumberReader identifier_file = new LineNumberReader(new InputStreamReader(identifier_url.openStream()));
				String line = null;
				while((line = identifier_file.readLine()) != null)
				{
					logger.debug("Found plugin resource class '{}' on line '{}' of file '{}'.", new Object[] {line, identifier_file.getLineNumber(), identifier_url});
					try
					{
						Class<?> clazz = m_class_loader.loadClass(line);
						if(isPlugin(clazz))
						{
							List<Class<?>> plugin_interfaces = getPluginsImplemented(clazz);
							logger.debug("Plugin class '{}' implements '{}' plugins.", clazz.getName(), plugin_interfaces.size());
							if(plugin_interfaces.size() > 0)
							{
								try
								{
									// only create an instance if there is at least 1 plugin interface
									Object plugin = clazz.newInstance();
									for(Class<?> plugin_interface : plugin_interfaces)
									{
										logger.info("Found plugin class '{}' for interface '{}'.", clazz.getName(), plugin_interface.getName());
										manager.addPlugin(plugin_interface, plugin_interface.cast(plugin));
									}
								} catch(Exception ex)
								{
									logger.error("Error creating an registering instance of '{}'.", clazz.getName());
								}
							}
						} else
						{
							logger.warn("Plugin class '{}' is not really a plugin (from line '{}' of '{}').", new Object[] {line, identifier_file.getLineNumber(), identifier_url});
						}
					} catch(ClassNotFoundException ex)
					{
						logger.warn("Plugin class '{}' not found (from line '{}' of '{}').", new Object[] {line, identifier_file.getLineNumber(), identifier_url });
					}
				}
			}
		} catch(IOException ex)
		{
			logger.error("Problem occured while loading plugins: ", ex);
		}
	}

	protected List<Class<?>> getPluginsImplemented(Class<?> plugin)
	{
		List<Class<?>> retval = new Vector<Class<?>>();
		if(isPlugin(plugin))
		{
			ImplementsPlugin plugin_list = plugin.getAnnotation(ImplementsPlugin.class);
			for(Class<? extends IPlugin> plugin_type : plugin_list.value())
			{
				if(plugin_type.isAssignableFrom(plugin))
				{
					// only add it as a plugin if they actually implement the plugin
					// T is a class (interface) that extends IPlugin, so is plugin_type
					retval.add((Class<?>)plugin_type);
				}
			}
		}

		return retval;
	}

	protected boolean isPlugin(Class<?> clazz)
	{
		return clazz.isAnnotationPresent(ImplementsPlugin.class);
	}
}
