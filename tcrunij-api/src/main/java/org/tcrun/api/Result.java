package org.tcrun.api;

import java.util.Map;

/**
 * A result holds information about a test case result.  One test case could possibly file multiple results, or be
 * called more than once, so there isn't necessarily a one to one mapping.  A result object will contain several pieces
 * of information needed for debugging a failed result, or re-running a test with the same conditions.
 *
 * @author jcorbett
 */
public interface Result
{
	/**
	 * The status of the result, for example PASS, FAIL, SKIPPED, ERROR, etc.
	 * 
	 * @return status of this test result.
	 */
	public ResultStatus getStatus();

	/**
	 * A reason for the status.  This could be null, watch out.
	 * @return a reason or message for the status, or null if there is none.
	 */
	public String getReason();

	/**
	 * This returns the configuration the test was ran with.  Even though there is a global configuration inside
	 * the tcrun context, the test runner can decide what parts are available to the test, or change them.
	 * 
	 * @return A map of data passed to the test for configuration and / or data driven pieces.
	 */
	public Map<String, String> getConfiguration();

	/**
	 * Shortcut for getResultAttributes().put(key, value).
	 *
	 * @param key The name of the result attribute.
	 * @param value The value of the result attribute to set.
	 */
	public void setResultAttribute(String key, String value);

	/**
	 * Shortcut for getResultAttributes().containsKey(key).
	 *
	 * @param key The name of the result attribute to check for.
	 * @return true if the result attribute exists in the map, false otherwise.
	 */
	public boolean resultAttributeExists(String key);

	/**
	 * Shortcut for getResultAttributes().get(key).
	 *
	 * @param key The name of the result attribute to retrieve.
	 * @return The value of the result attribute, or null if it does not exist.
	 */
	public String getResultAttribute(String key);

	/**
	 * Returns  the default value if the key does not exist, or it's value is null.  Same as:
	 *
	 * <code>
	 * String value = getResultAttributes().get(key);
	 * if(value == null)
	 * {
	 *     value = defaultValue;
	 * }
	 * </code>
	 *
	 * @param key The name of the result attribute.
	 * @param defaultValue The default value of the result attribute to use if the key doesn't exist, or it's value
	 *                     is null.
	 * @return The value of the result attribute, unless one does not exist, or it's null, then return the
	 *         defaultValue.
	 */
	public String getResultAttribute(String key, String defaultValue);

	/**
	 * Get a map of result attributes.  This is meta-data about the result.  It may contain plugin specific
	 * information.  It's there mostly for plugins to provide a communication mechanism between tests.  There are
	 * no standard attributes, though good examples would be:
	 * <ul>
	 *   <li>Test Name</li>
	 *   <li>Test Plan</li>
	 *   <li>Diagnostic information</li>
	 * </ul>
	 * 
	 * @return a map containing the attributes.
	 */
	public Map<String, String> getAttributes();

	/**
	 * Get the RunnableTest instance, which contains references to the test runner, the object itself, the id
	 * of the test, etc.
	 * 
	 * @return an instance of the test that produced this result.
	 */
	public RunnableTest getTest();
}
