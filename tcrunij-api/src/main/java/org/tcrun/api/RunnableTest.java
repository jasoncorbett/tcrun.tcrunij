package org.tcrun.api;

import java.util.Map;

/**
 * An object representing all information needed to understand the test.  This means a list of attributes, the test id
 * (a unique string that identifies the test), and the test's runner.
 *
 * @author jcorbett
 */
public interface RunnableTest
{
	/**
	 * 
	 * @return
	 */
	public Map<String, String> getTestAttributes();

	/**
	 *
	 * @return
	 */
	public String getTestId();

	/**
	 *
	 * @return
	 */
	public TestRunner getTestRunner();
}
