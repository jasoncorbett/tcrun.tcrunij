package org.tcrun.api;

import java.util.List;

/**
 * An object representing all information needed to understand the test.  This means a list of attributes, the test id
 * (a unique string that identifies the test), and the test's runner.
 *
 * @author jcorbett
 */
public interface RunnableTest
{
	/**
	 * Test Attributes are key value pairs that define meta-data about a test.  They could be things like group or
	 * package membership information.
	 *
	 * @return A Map containing the attributes for a test.
	 */
	public List<TestCaseAttribute> getTestAttributes();

	/**
	 * A test id is a string that uniquely identifies the test from a loading perspective.  An example could be a
	 * test's class name, however if there are multiple tests in a class (as is with JUnit), this string must
	 * differentiate between them (in the example of JUnit it could append #method-name to the class name).
	 *
	 * @return A unique ID for the test.
	 */
	public String getTestId();

	/**
	 * Retrieve the test runner for the test.  The test runner contains an instance of the test, and is able to run
	 * the test.
	 *
	 * @return the instance of the TestRunner that is supposed to run the test.
	 */
	public TestRunner getTestRunner();
}
