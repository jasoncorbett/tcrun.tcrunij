package org.tcrun.plugins.basic.attributeproviders;

import java.util.ArrayList;
import java.util.List;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseAttribute;
import org.tcrun.api.annotations.TestGroup;
import org.tcrun.api.plugins.AttributeProviderPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin(AttributeProviderPlugin.class)
public class TestGroupAttributeProvider implements AttributeProviderPlugin
{

	@Override
	public List<TestCaseAttribute> getAttributesFor(TCRunContext p_context, Class<?> p_testcase, String p_testid)
	{
		List<TestCaseAttribute> retval = new ArrayList<TestCaseAttribute>();

		if(p_testcase.isAnnotationPresent(TestGroup.class))
		{
			TestGroup annot = p_testcase.getAnnotation(TestGroup.class);
			for(String group : annot.value())
				retval.add(new TestCaseAttribute("group", group));
		}

		return retval;
	}

	@Override
	public String getPluginName()
	{
		return "TestGroup attribute provider plugin";
	}
}
