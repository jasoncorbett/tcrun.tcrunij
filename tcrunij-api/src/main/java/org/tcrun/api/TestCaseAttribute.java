package org.tcrun.api;

/**
 * A simple data structure to hold a name value pair describing an attribute of a test case.
 *
 * @author jcorbett
 */
public class TestCaseAttribute
{
	private String m_name;
	private String m_value;

	public TestCaseAttribute(String p_name, String p_value)
	{
		m_name = p_name;
		m_value = p_value;
	}
	public String getName()
	{
		return m_name;
	}

	public String getValue()
	{
		return m_value;
	}
}
