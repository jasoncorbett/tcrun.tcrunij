package org.tcrun.plugins.tcapiplugin;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.TestLoaderPlugin;
import org.tcrun.tcapi.SimpleTestCase;


/**
 *
 * @author jcorbett
 */
@ImplementsPlugin(TestLoaderPlugin.class)
public class TCApiTestLoader implements TestLoaderPlugin
{
	public static final String TESTS_SUBDIR_NAME = "tcapi";

	static private XLogger s_logger = XLoggerFactory.getXLogger(TCApiTestLoader.class);
	private TCRunContext m_context = null;
	private File m_test_root;

	public void initialize(TCRunContext p_context)
	{
		s_logger.debug("Initializing plugin.");
		m_context = p_context;

		//TODO: parameterize tests name
		//TODO: check configuration for different tcapi dir name
		m_test_root = new File(m_context.getTCRunRoot().getAbsolutePath() + File.separator +
			               "tests" + File.separator +
				       TESTS_SUBDIR_NAME);
		s_logger.debug("Figured tcapi test root to be: '{}'.", m_test_root.getAbsolutePath());
	}

	public Iterator<RunnableTest> iterator()
	{
		List<RunnableTest> retval_list = new ArrayList<RunnableTest>();
		// get location of test jars
		if(m_test_root.exists())
		{
			// create class loader for jars
			ArrayList<URL> jar_list = new ArrayList<URL>();
			File[] files = m_test_root.listFiles();
			for(File f : files)
			{
				if (f.getName().endsWith(".jar") || f.getName().endsWith(".JAR"))
				{
					s_logger.debug("Found jar in tests folder '{}'.", f.getName());
					try
					{
						jar_list.add(f.toURI().toURL());
					} catch(MalformedURLException ex)
					{
						s_logger.error("Unable to create URL out of jar file '" + f.getAbsolutePath() + "' ", ex);
					}
				}
			}
			s_logger.debug("Found '{}' jars in test folder '{}'.", jar_list.size(), m_test_root.getAbsolutePath());

			URL[] jars = jar_list.toArray(new URL[0]);

			URLClassLoader classloader = new URLClassLoader(jars, this.getClass().getClassLoader());

			//for each jar look for class files
			ArrayList<String> classnames = new ArrayList<String>();
			for(URL jar_url : jar_list)
			{
				s_logger.debug("Looking in jar '{}' for classes.", jar_url);
				try
				{
					JarInputStream jar_stream = new JarInputStream(jar_url.openStream());
					JarEntry entry = null;
					while((entry = jar_stream.getNextJarEntry()) != null)
					{
						if(entry.getName().endsWith(".class"))
						{
							classnames.add (entry.getName().replaceAll("/", "\\.").substring(0, entry.getName().length() - 6));
						}
					}
					jar_stream.close();
				} catch(IOException ex)
				{
					s_logger.warn("Unable to search jar '" + jar_url + "' for tests.", ex);
				}
			}
			s_logger.debug("Found '{}' class files in '{}' jars.", classnames.size(), jar_list.size());

			// search all classes
			for(String classname : classnames)
			{
				try
				{
					Class<?> potential = classloader.loadClass(classname);
					// if non-abstract class that implement SimpleTestCase
					if(!potential.isInterface() &&
					   !Modifier.isAbstract(potential.getModifiers()) && 
					   SimpleTestCase.class.isAssignableFrom(potential))
					{
						// Add TCApi Test Runner to retval_list
						s_logger.debug("Found test '{}'!", classname);
						retval_list.add(new TCApiRunnableTest(potential, m_context));

					}
				} catch(ClassNotFoundException ex)
				{
					s_logger.warn("Found a class file for class '" + classname + "', but couldn't load it.", ex);
				}
			}

		} else
		{
			s_logger.warn("Test root '{}' for TCApi test plugin not found, returning empty list of tests.", m_test_root.getAbsolutePath());
		}

		s_logger.debug("Returning '{}' tests from test loader session.", retval_list.size());

		return retval_list.iterator();
	}

	public String getPluginName()
	{
		return "TCApi Test Loader";
	}

}
