package org.tcrun.integration.tests.tcapiselenium;

import java.util.UUID;
import org.tcrun.integration.tests.tcapiselenium.pages.DisappearingElementTestPage;
import org.tcrun.integration.tests.tcapiselenium.pages.UglyTestPage;
import org.tcrun.tcapi.TestResult;
import org.tcrun.tcapi.selenium.AbstractSeleniumTest;
import static org.tcrun.tcapi.assertlib.MatchBuilder.*;

/**
 *
 * @author jcorbett
 */
public class TestWaitForNotExists extends AbstractSeleniumTest
{
	@Override
	public void setup() throws Exception
	{
		browser.goTo(UglyTestPage.Url);
		browser.waitFor(UglyTestPage.class);
		browser.click(UglyTestPage.DisappearingElementTestLink);
		browser.waitFor(DisappearingElementTestPage.class);
	}

	@Override
	public TestResult test() throws Exception
	{
		tclog.debug("Checking for existance of Disappearing element, it should exist.");
		check.that(browser.exists(DisappearingElementTestPage.DisappearingElement), Is.True());
		check.that(browser.getAttribute(DisappearingElementTestPage.DisappearingElementContainer, "innerhtml"), Is.Not.EmptyString());
		browser.on(DisappearingElementTestPage.class).makeElementDisappear();
		browser.waitForDoesNotExist(DisappearingElementTestPage.DisappearingElement);
		tclog.debug("Checking for existance of Disappearing element, it should not exist.");
		check.that(browser.exists(DisappearingElementTestPage.DisappearingElement), Is.False());
		check.that(browser.getAttribute(DisappearingElementTestPage.DisappearingElementContainer, "innerhtml"), Is.Null());
		return TestResult.PASS;
	}

	@Override
	public UUID getTestUUID()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
