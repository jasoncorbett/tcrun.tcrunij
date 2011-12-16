/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.plugins.tcapiplugin;

import java.util.HashMap;
import java.util.Map;
import org.tcrun.api.Result;
import org.tcrun.api.ResultStatus;
import org.tcrun.api.RunnableTest;
import org.tcrun.tcapi.TestResult;

/**
 *
 * @author jcorbett
 */
public class SimpleTestCaseResult implements Result
{
	private RunnableTest m_test;
	private ResultStatus m_result;
	private String m_reason;
	private Map<String, String> m_result_attrs;

	public SimpleTestCaseResult(RunnableTest p_test, String p_reason, TestResult p_result)
	{
		m_test = p_test;
		m_result = ResultStatus.valueOf(p_result.toString()); // The result enum names match up right now.
		m_reason = p_reason;
		m_result_attrs = new HashMap<String, String>();
	}

	public ResultStatus getStatus()
	{
		return m_result;
	}

	public String getReason()
	{
		return m_reason;
	}

	public Map<String, String> getConfiguration()
	{
		return m_test.getTestRunner().getConfiguration();
	}

	public void setResultAttribute(String key, String value)
	{
		m_result_attrs.put(key, value);
	}

	public boolean resultAttributeExists(String key)
	{
		return m_result_attrs.containsKey(key);
	}

	public String getResultAttribute(String key)
	{
		return m_result_attrs.get(key);
	}

	public String getResultAttribute(String key, String defaultValue)
	{
		if(!resultAttributeExists(key))
			return defaultValue;
		return getResultAttribute(key);
	}

	public Map<String, String> getAttributes()
	{
		return m_result_attrs;
	}

	public RunnableTest getTest()
	{
		return m_test;
	}

}
