package org.tcrun.plugins.basic.attributeproviders;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseAttribute;
import org.tcrun.api.plugins.AttributeProviderPlugin;

/**
 * Add a feature attribute to the test case.  This will by default be the name of the jar file (minus any version info
 * and the .jar suffix).
 *
 * @author jcorbett
 */
@ImplementsPlugin(AttributeProviderPlugin.class)
public class FeatureNameAttributeProvider implements AttributeProviderPlugin
{
	private static XLogger logger = XLoggerFactory.getXLogger(FeatureNameAttributeProvider.class);

	@Override
	public List<TestCaseAttribute> getAttributesFor(TCRunContext p_context, Class<?> p_testcase, String p_testid)
	{
		List<TestCaseAttribute> attributes = new ArrayList<TestCaseAttribute>();
		try
		{
			String jarname = (new File(p_testcase.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getName();
			String featurename = jarname;
			Pattern endOfJarFilename = Pattern.compile("((-\\d+\\..*)?\\.jar)$");
			Matcher match = endOfJarFilename.matcher(jarname);
			if(match.find())
			{
				featurename = jarname.substring(0, match.start());
			}

			attributes.add(new TestCaseAttribute("feature", featurename));
		} catch (URISyntaxException ex)
		{
			logger.warn("Problem encountered while trying to get the jar test came from: ", ex);
		}
		return attributes;
	}

	@Override
	public String getPluginName()
	{
		return "Feature Name Attribute Provider";
	}
}
