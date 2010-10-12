package org.tcrun.plugins.tcapiplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.PluginManager;
import org.tcrun.api.PluginManagerFactory;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseAttribute;
import org.tcrun.api.TestRunner;
import org.tcrun.api.plugins.AttributeProviderPlugin;
import org.tcrun.tcapi.SimpleTestCase;
import org.tcrun.tcapi.TestResult;

/**
 *
 * @author jcorbett
 */
public class TCApiRunnableTest implements RunnableTest, TestRunner
{
	static private XLogger s_logger = XLoggerFactory.getXLogger(TCApiRunnableTest.class);

	private List<TestCaseAttribute> m_attributes;
	private Class<?> test_class;
	private SimpleTestCase test_instance;
	private Map<String, String> test_configuration;

	public TCApiRunnableTest(Class<?> p_test, TCRunContext p_context)
	{
				// Error checking, make sure we have a valid test case class to work with.
		if(p_test == null)
		{
			s_logger.error("Constructor called with a null test case class.");
			throw new IllegalArgumentException(TCApiRunnableTest.class.getCanonicalName() + "'s constructor called with a null test case class.");
		}

		if(!SimpleTestCase.class.isAssignableFrom(p_test))
		{
			s_logger.error("Constructor called with test class '{}' which does not implement '{}'.", p_test.getCanonicalName(), SimpleTestCase.class.getCanonicalName());
			throw new IllegalArgumentException("Test Class " + p_test.getCanonicalName() + " does not implement " + SimpleTestCase.class.getCanonicalName() + ".");
		}

		test_class = p_test;

		// make a copy of the test configuration passed in, no surprise changes, only diliberate ones
		test_configuration = new HashMap<String, String>(p_context.getTestCaseConfiguration());

		m_attributes = new ArrayList<TestCaseAttribute>();

		PluginManager plugin_manager = PluginManagerFactory.getPluginManager();
		s_logger.debug("Initializing attributes for test '{}' by going through AttributeProviderPlugins.", p_test.getCanonicalName());
		List<AttributeProviderPlugin> attr_plugins = plugin_manager.getPluginsFor(AttributeProviderPlugin.class);
		s_logger.debug("There are '{}' attribute provider plugins, looping through them all.", attr_plugins.size());
		for(AttributeProviderPlugin plugin: attr_plugins)
		{
			s_logger.debug("Calling getAttributesFor() on plugin class '{}'.", plugin.getClass().getCanonicalName());
			m_attributes.addAll(plugin.getAttributesFor(p_context, p_test, p_test.getCanonicalName()));
			s_logger.debug("There are now '{}' attributes for test '{}'.", m_attributes.size(), p_test.getCanonicalName());
		}
		s_logger.debug("Done looping through attribute provider plugins.");

	}

	public List<TestCaseAttribute> getTestAttributes()
	{
		return m_attributes;
	}

	public String getTestId()
	{
		return test_class.getCanonicalName();
	}

	public TestRunner getTestRunner()
	{
		return this;
	}

	public Class<?> getTestClass()
	{
		return test_class;
	}

	public Object getTestInstance()
	{
		if(test_instance == null)
		{
			// create an instance the first time this is called, initialization on demand.
			// it's important NOT to do this in the constructor, because the test class may not actually be used.
			try
			{
				test_instance = (SimpleTestCase) test_class.newInstance();
			} catch(InstantiationException ex)
			{
				s_logger.error("Was not able to create instance of test case class " + test_class.getCanonicalName(), ex.getMessage());
			} catch(IllegalAccessException ex)
			{
				s_logger.error("Was not able to create instance of test case class " + test_class.getCanonicalName(), ex.getMessage());
			}
		}
		return test_instance;
	}

	public Map<String, String> getConfiguration()
	{
		return test_configuration;
	}

	public void setConfigurationValue(String p_key, String p_value)
	{
		test_configuration.put(p_key, p_value);
	}

	public void runTest(TCRunContext context)
	{
		// make sure the instance has been created.
		getTestInstance();
		if(test_instance == null)
		{
			s_logger.error("Unable to run test '{}' because test instance was null.", getTestClass().getName());
			return;
		}

		boolean should_continue = true;
		try
		{
			s_logger.info("Calling tcSetup on test '{}'.", getTestClass().getName());
			test_instance.tcSetup(test_configuration);
		} catch(RuntimeException e)
		{
			// No logging by default, the test's handleException should take care of logging.
			should_continue = test_instance.handleException(e);
		} catch(Exception e)
		{
			// No logging by default, the test's handleException should take care of logging.
			should_continue = test_instance.handleException(e);
		}

		if(!should_continue)
		{
			s_logger.info("Because of an error in tcSetup of test '{}', doTest will not be called (handleException returned false).", getTestClass().getName());
			context.addResult(new SimpleTestCaseResult(this, "", TestResult.BROKEN_TEST));
			return;
		}

		TestResult result = TestResult.BROKEN_TEST;
		String reason = "";

		try
		{
			s_logger.info("Calling doTest on test '{}'.", getTestClass().getName());
			result = test_instance.doTest();
			if (result == null)
			{
				s_logger.warn("Test '{}' returned null test result, using BROKEN_TEST.", test_class.getName());
				result = TestResult.BROKEN_TEST;
				reason = "Test returned null result from doTest method.";
			} else
			{
				reason = "Test returned " + result.toString() + " from doTest method.";
			}
		} catch (RuntimeException e)
		{
			reason = "Exception " + e.getClass().getName() + " was thrown during doTest: " + e.getMessage();
			test_instance.handleException(e);
		} catch (Exception e)
		{
			reason = "Exception " + e.getClass().getName() + " was thrown during doTest: " + e.getMessage();
			test_instance.handleException(e);
		}

		s_logger.info("Test with id '{}' returned result '{}' for reason '{}'.", new Object[] {getTestId(), result.toString(), reason});

		try
		{
			s_logger.info("Calling tcCleanup on test '{}'.", getTestClass().getName());
			test_instance.tcCleanUp();
		} catch(RuntimeException e)
		{
			// No logging by default, the test's handleException should take care of logging.
			should_continue = test_instance.handleException(e);
		} catch(Exception e)
		{
			// No logging by default, the test's handleException should take care of logging.
			should_continue = test_instance.handleException(e);
		}
		context.addResult(new SimpleTestCaseResult(this, reason, result));
	}
}
