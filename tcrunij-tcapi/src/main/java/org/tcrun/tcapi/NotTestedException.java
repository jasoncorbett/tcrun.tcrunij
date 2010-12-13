/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi;

/**
 *
 * @author lhigginson
 */
public class NotTestedException extends ResultBasedTestError
{
	public NotTestedException(String message)
	{
		super(message);
	}

	@Override
	public TestResult getResult()
	{
		return TestResult.NOT_TESTED;
	}
}
