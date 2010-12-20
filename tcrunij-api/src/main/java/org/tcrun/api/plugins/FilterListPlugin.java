package org.tcrun.api.plugins;

import java.util.List;
import org.tcrun.api.Plugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseFilter;

/**
 * This plugin is responsible for building a list of test case filters.  This list of filters
 * should be retrieved through a plugin specific method.  For instance a CommandLineFilterListPlugin
 * would get a list of filters from the command line options.  A TestPlanFilterListPlugin would get
 * them from some test plan file.
 *
 * @author jcorbett
 */
public interface FilterListPlugin extends Plugin
{
	/**
	 * Get a list of test case filters.
	 *
	 * @param p_context The tcrun context.
	 *
	 * @return A list of test case filters to add to the global list of filters.
	 */
	public List<TestCaseFilter> getFilters(TCRunContext p_context);
}
