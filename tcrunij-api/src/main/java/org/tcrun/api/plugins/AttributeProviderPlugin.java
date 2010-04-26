package org.tcrun.api.plugins;

import java.util.List;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseAttribute;

/**
 * An attribute provider plugin returns attributes for a test case based on it's id and test case class.
 *
 * @author jcorbett
 */
public interface AttributeProviderPlugin
{
	/**
	 * Get attributes of the test based on it's class.  The test id (provided by the test loader) is given here so
	 * that it can differentiate between the methods if there is multiple test cases per class (as is the case with
	 * junit).
	 * 
	 * @param p_context The tcrun context, for configuration information.
	 * @param p_testcase The class of the test case (in case there are annotations or other meta information).
	 * @param p_testid The id of the test case, a string uniquely identifying the test.
	 * 
	 * @return a list of test case attributes obtained by this plugin (if any).  nulls are valid for no attributes found.
	 */
	public List<TestCaseAttribute> getAttributesFor(TCRunContext p_context, Class<?> p_testcase, String p_testid);
}
