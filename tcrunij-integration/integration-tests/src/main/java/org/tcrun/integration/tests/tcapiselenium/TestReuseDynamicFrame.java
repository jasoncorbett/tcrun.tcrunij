/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tcrun.integration.tests.tcapiselenium;

import java.util.UUID;
import org.tcrun.api.annotations.TestGroup;
import org.tcrun.api.annotations.TestName;
import org.tcrun.integration.tests.tcapiselenium.pages.DynamicFrameTestPage;
import org.tcrun.integration.tests.tcapiselenium.pages.UglyTestPage;
import org.tcrun.tcapi.TestResult;
import org.tcrun.tcapi.selenium.AbstractSeleniumTest;
import static org.tcrun.tcapi.assertlib.MatchBuilder.*;

/**
 *
 * @author jcorbett
 */
@TestName("tcapi-selenium: Reusing a Dynamic Frame")
@TestGroup({"tcrunij", "tcapi-selenium"})
public class TestReuseDynamicFrame extends AbstractSeleniumTest
{
	@Override
	public void setup() throws Exception
	{
		browser.goTo(UglyTestPage.Url);
		browser.waitFor(UglyTestPage.class);
	}

	@Override
	public TestResult test() throws Exception
	{
		browser.click(UglyTestPage.DynamicFrameTestLink);
		browser.waitFor(DynamicFrameTestPage.class);
		check.that(browser.exists(DynamicFrameTestPage.IFrameWithoutId), Is.True());
		browser.waitFor(DynamicFrameTestPage.IframeContentTitle);
		check.that(browser.getText(DynamicFrameTestPage.IframeContentTitle), Is.StringContaining("iframe content"));

		// Navigate away and back and try again
		browser.goTo(UglyTestPage.Url);
		browser.waitFor(UglyTestPage.class);
		browser.click(UglyTestPage.DynamicFrameTestLink);
		browser.waitFor(DynamicFrameTestPage.class);
		check.that(browser.exists(DynamicFrameTestPage.IFrameWithoutId), Is.True());
		browser.waitFor(DynamicFrameTestPage.IframeContentTitle);
		check.that(browser.getText(DynamicFrameTestPage.IframeContentTitle), Is.StringContaining("iframe content"));

		return TestResult.PASS;
	}

	@Override
	public UUID getTestUUID()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
