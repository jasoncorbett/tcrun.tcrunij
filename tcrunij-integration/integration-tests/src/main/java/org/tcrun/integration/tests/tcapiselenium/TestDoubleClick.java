package org.tcrun.integration.tests.tcapiselenium;

import java.util.UUID;
import org.tcrun.integration.tests.tcapiselenium.pages.DoubleClickTestPage;
import org.tcrun.integration.tests.tcapiselenium.pages.UglyTestPage;
import org.tcrun.tcapi.TestResult;
import org.tcrun.tcapi.selenium.AbstractSeleniumTest;
import static org.tcrun.tcapi.assertlib.MatchBuilder.*;

/**
 *
 * @author jcorbett
 */
public class TestDoubleClick extends AbstractSeleniumTest
{
	private static UUID uuid = UUID.fromString("cf5f4aa5-ce5b-4c41-92d1-779a812f55c1");

	@Override
	public void setup() throws Exception
	{
		browser.goTo(UglyTestPage.Url);
		browser.waitFor(UglyTestPage.class);
		browser.click(UglyTestPage.DoubleClickTestLink);
		browser.waitFor(DoubleClickTestPage.class);
	}


	@Override
	public TestResult test() throws Exception
	{
		step("Get the value of the result element.", "Text of the element is NOT TESTED");
		check.that(browser.getText(DoubleClickTestPage.TestResult), Is.EqualTo("NOT TESTED"));
		step("Click the test link once (not a double click).", "The text of the result element should be FAIL.");
		browser.click(DoubleClickTestPage.DoubleClickLink);
		Thread.sleep(500);
		check.that(browser.getText(DoubleClickTestPage.TestResult), Is.EqualTo("FAIL"));
		step("Double click the test link.", "The text of the result element should change to PASS.");
		browser.doubleClick(DoubleClickTestPage.DoubleClickLink);
		Thread.sleep(500);
		check.that(browser.getText(DoubleClickTestPage.TestResult), Is.EqualTo("PASS"));
		return TestResult.PASS;
	}

	@Override
	public UUID getTestUUID()
	{
		return uuid;
	}
}
