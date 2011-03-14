/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.api;

import java.util.UUID;

/**
 * A UUID can provide a test with a refactor-safe way to identify a test.  This can be particularly useful if
 * syncing automation tests with a test case management solution is needed.  By implementing this interface any
 * plugin can get the UUID / GUID of the test and use it to help identify the test.
 *
 * @author jcorbett
 */
public interface TestWithUUID
{
	/**
	 * A UUID for a test should be a constant value, which will help identify the test in a refactoring safe way.
	 *
	 * @return The UUID for the test (should be a constant).
	 */
	public UUID getTestUUID();
}
