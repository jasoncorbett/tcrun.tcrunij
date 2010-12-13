/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.plugins.basic.filters;

import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TestCaseFilter;
import org.tcrun.api.plugins.FilterPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin(FilterPlugin.class)
public class BasicFilterPlugin implements FilterPlugin
{

	@Override
	public TestCaseFilter parseFilterString(String p_filterString)
	{
		if(TestCaseIdFilter.isFilterFor(p_filterString))
			return new TestCaseIdFilter(p_filterString);
		else if(PackageTestCaseFilter.isFilterFor(p_filterString))
			return new PackageTestCaseFilter(p_filterString);
		else if(GroupFilter.isFilterFor(p_filterString))
			return new GroupFilter(p_filterString);
		return null;
	}

	@Override
	public String getPluginName()
	{
		return "Basic Filter Factory";
	}
}
