package org.tcrun.tcapi;

import org.tcrun.api.TestCaseStep;

/**
 *
 * @author jcorbett
 */
public class BasicTestCaseStep implements TestCaseStep
{
	private String m_name;
	private String m_expected;

	public BasicTestCaseStep(String p_name, String p_expected)
	{
		m_name = p_name;
		m_expected = p_expected;
	}

	@Override
	public String getStepName()
	{
		return m_name;
	}

	@Override
	public String getStepExpectedResult()
	{
		return m_expected;
	}
}
