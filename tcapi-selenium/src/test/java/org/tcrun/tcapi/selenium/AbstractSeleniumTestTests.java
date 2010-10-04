package org.tcrun.tcapi.selenium;

import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;
import org.tcrun.tcapi.TestResult;

/**
 *
 * @author jcorbett
 */
public class AbstractSeleniumTestTests
{
	@Test
	public void browserAvailableInSetup() throws Exception
	{
		AbstractSeleniumTest test = new AbstractSeleniumTest() {

			@Override
			public void setup()
			{
				assertNotNull("The browser object shouldn't be null in setup()", browser);
			}

			@Override
			public TestResult test() throws Exception
			{
				return TestResult.PASS;
			}
		};
		test.tcSetup(new HashMap<String, String>());
	}
}
