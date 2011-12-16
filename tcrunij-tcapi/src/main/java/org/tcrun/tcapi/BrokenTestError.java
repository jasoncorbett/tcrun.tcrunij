/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi;

/**
 *
 * @author jcorbett
 */
public class BrokenTestError extends ResultBasedTestError
{
	public BrokenTestError(String message)
	{
		super(message);
	}

	@Override
	public TestResult getResult()
	{
		return TestResult.BROKEN_TEST;
	}
}
