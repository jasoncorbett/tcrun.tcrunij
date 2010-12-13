package org.tcrun.plugins.basic.filters;

import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TestCaseFilter;
import org.tcrun.api.plugins.FilterPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin(FilterPlugin.class)
public class BasicExcludeFilterPlugin implements FilterPlugin
{

	@Override
	public TestCaseFilter parseFilterString(String p_filterString)
	{
		if(p_filterString.startsWith("excludeid:"))
			return new ExcludeIdFilter(p_filterString);
		else if(p_filterString.startsWith("excludepkg:"))
			return new ExcludePackageFilter(p_filterString);
		else if(p_filterString.startsWith("excludegroup:"))
			return new ExcludeGroupFilter(p_filterString);
		else
			return null;
	}

	@Override
	public String getPluginName()
	{
		return "Basic Exclude Filter Plugin";
	}
}
