package org.tcrun.tcapi;

/**
 * An error indicating that a particular result should be the result of the test.  Most common use is for setting
 * a BROKEN_TEST as a result of an exception, but it's possible to use FAIL, NOT_TESTED, or SKIPPED as well.  Using
 * an exception with a result attached is a way to override the result of the test.
 *
 * These exceptions are caught by the test case runner and override any other result.
 *
 * @author jcorbett
 */
public abstract class ResultBasedTestError extends TestCaseError
{
	public abstract TestResult getResult();

	public ResultBasedTestError(String message)
	{
		super(message);
	}
}
