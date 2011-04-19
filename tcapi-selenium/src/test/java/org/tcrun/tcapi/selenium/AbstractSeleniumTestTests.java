package org.tcrun.tcapi.selenium;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
			public UUID uuid = UUID.fromString("e73af88d-566c-40ee-8e22-0be0b350ac2d");

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

			@Override
			public UUID getTestUUID()
			{
				return uuid;
			}
		};
		Map<String, String> config = new HashMap<String, String>();
		config.put("browser", "headless");
		test.tcSetup(config);
	}
}
