package org.tcrun.integration.tests.tcapiselenium;

import org.tcrun.api.annotations.TestName;
import org.tcrun.integration.tests.tcapiselenium.pages.DynamicTextTestPage;
import org.tcrun.integration.tests.tcapiselenium.pages.DynamicTextTestPage;
import org.tcrun.integration.tests.tcapiselenium.pages.UglyTestPage;
import org.tcrun.tcapi.TestResult;
import org.tcrun.tcapi.selenium.AbstractSeleniumTest;

import java.util.UUID;

import static org.tcrun.tcapi.assertlib.MatchBuilder.Is;

/**
 *
 * @author jcorbett
 */
@TestName("Wait for element's text not empty.")
public class TestWaitForNotEmpty extends AbstractSeleniumTest
{
	@Override
	public void setup() throws Exception
	{
		browser.goTo(UglyTestPage.Url);
		browser.waitFor(UglyTestPage.class);
		browser.click(UglyTestPage.DynamicTextTestLink);
		browser.waitFor(DynamicTextTestPage.class);
	}

	@Override
	public TestResult test() throws Exception
	{
		tclog.debug("Checking for existance of Dynamic Text element, it should exist.");
		check.that(browser.exists(DynamicTextTestPage.DynamicTextElement), Is.True());
        check.that(browser.getText(DynamicTextTestPage.DynamicTextElement), Is.EmptyString());
		browser.on(DynamicTextTestPage.class).makeTextAppear();
        browser.waitForTextNotEmpty(DynamicTextTestPage.DynamicTextElement);
		check.that(browser.getText(DynamicTextTestPage.DynamicTextElement), Is.Not.EmptyString());
		return TestResult.PASS;
	}

	@Override
	public UUID getTestUUID()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
