package org.tcrun.tcapi;

/**
 * A Basis for all exceptions in the TCApi.  You shouldn't create instances of this class directly, instead use a
 * subclass if possible.
 *
 * @author jcorbett
 */
public class TestCaseError extends Exception
{
	/**
	 * Don't create instances of this error directly for most circumstances, instead use or create a subclass which
	 * has more meaning.
	 * 
	 * @param message The error message.
	 */
	public TestCaseError(String message)
	{
		super(message);
	}
}
