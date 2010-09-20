package org.tcrun.tcapi.assertlib;

import org.slf4j.ext.XLogger;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

/**
 * Assert library based on hamcrest matchers.
 * @author jcorbett
 */
public class Check
{
	private XLogger m_test_case_logger;
	public Check(XLogger p_test_case_logger)
	{
		m_test_case_logger = p_test_case_logger;
	}

	public <T> void that(T actual, Matcher<T> matcher) throws AssertionFailure
	{
		if(matcher.matches(actual))
		{
			m_test_case_logger.info("Successful Check: actual: '{}' expected: {}", actual, StringDescription.asString(matcher));
		} else
		{
			throw new AssertionFailure("Actual: '" + actual + "', Expected: '" + StringDescription.asString(matcher));
		}
	}
}
