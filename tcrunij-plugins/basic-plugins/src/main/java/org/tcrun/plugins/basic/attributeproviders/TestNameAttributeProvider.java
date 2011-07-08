package org.tcrun.plugins.basic.attributeproviders;

import java.util.ArrayList;
import java.util.List;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseAttribute;
import org.tcrun.api.annotations.TestName;
import org.tcrun.api.plugins.AttributeProviderPlugin;

/**
 * If the test has a TestName attribute on the class, this provider puts an attribute in the map with the test name.
 *
 * @author jcorbett
 */
@ImplementsPlugin(AttributeProviderPlugin.class)
public class TestNameAttributeProvider implements AttributeProviderPlugin
{

	@Override
	public List<TestCaseAttribute> getAttributesFor(TCRunContext p_context, Class<?> p_testcase, String p_testid)
	{
		List<TestCaseAttribute> retval = new ArrayList<TestCaseAttribute>();

		if(p_testcase.isAnnotationPresent(TestName.class))
		{
			retval.add(new TestCaseAttribute("name", p_testcase.getAnnotation(TestName.class).value()));
		}

		return retval;
	}

	@Override
	public String getPluginName()
	{
		return "Test Name Attribute Provider Plugin";
	}

}
