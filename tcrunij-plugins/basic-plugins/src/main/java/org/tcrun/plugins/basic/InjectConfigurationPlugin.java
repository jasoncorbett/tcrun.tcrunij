package org.tcrun.plugins.basic;

import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestRunner;
import org.tcrun.api.annotations.ConfigValue;
import org.tcrun.api.plugins.BeforeTestCasePlugin;

/**
 * This plugin runs before tests to inject configuration values into fields annotated with @ConfigValue.
 * 
 * @author jcorbett
 */
@ImplementsPlugin(BeforeTestCasePlugin.class)
public class InjectConfigurationPlugin implements BeforeTestCasePlugin
{
	private static Logger logger = LoggerFactory.getLogger(InjectConfigurationPlugin.class);

	@Override
	public void beforeTestExecutes(TCRunContext p_context, TestRunner p_testrunner)
	{
		Object testinstance = p_testrunner.getTestInstance();
		Field[] fields = testinstance.getClass().getDeclaredFields();
		logger.debug("There are {} fields on test class {}.", fields.length, p_testrunner.getTestClass().getName());
		for(Field field : fields)
		{
			logger.debug("Checking field {} for @ConfigValue annotation.", field.getName());
			ConfigValue configAnnotation = field.getAnnotation(ConfigValue.class);
			if(configAnnotation != null)
			{
				logger.debug("Found @ConfigValue annotation on field {} on test class {}.", field.getName(), p_testrunner.getClass().getName());
				field.setAccessible(true);
				try
				{
					if(p_testrunner.getConfiguration().containsKey(configAnnotation.value()))
					{
						field.set(testinstance, p_testrunner.getConfiguration().get(configAnnotation.value()));
					} else
					{
						logger.error("Missing config value {} for field {} on test class {}.", new Object[] {configAnnotation.value(), field.getName(), p_testrunner.getClass().getName()});
						field.set(testinstance, null);
					}
				} catch(IllegalAccessException e)
				{
					logger.error("Unable to set @ConfigValue annotation on field " + field.getName() + " on test class " + p_testrunner.getClass().getName() + ".", e);
				}
			}
		}
	}

	@Override
	public String getPluginName()
	{
		return "Inject ConfigValue Plugin";
	}
	
}
