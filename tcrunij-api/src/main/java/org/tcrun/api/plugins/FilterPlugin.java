/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.TestCaseFilter;

/**
 * A filter plugin creates a test case filter based on a string.  If the string is not valid for this filter it is <b>
 * very important</b> to return null.  This plugin is an exclusive plugin meaning that only one FilterPlugin should
 * respond to a string.
 *
 * If multiple filters respond a test case will be added if any of the filters pass it, however this should be
 * considered an error case for the plugins, and logs will note it as such.
 *
 * @author jcorbett
 */
public interface FilterPlugin extends Plugin
{
	/**
	 * Return a TestCaseFilter if the string passed is for this particular filter.
	 * 
	 * @param p_filterString The input filter string
	 * @return A test case filter, or null if the input string is not valid for this test case.
	 */
	public TestCaseFilter parseFilterString(String p_filterString);
}
