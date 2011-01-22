package org.tcrun.integration.tests.tcapiselenium;

import java.util.UUID;
import org.tcrun.api.annotations.TestGroup;
import org.tcrun.api.annotations.TestName;
import org.tcrun.tcapi.TestResult;
import org.tcrun.tcapi.selenium.AbstractSeleniumTest;
import static org.tcrun.tcapi.assertlib.MatchBuilder.*;

/**
 *
 * @author jcorbett
 */
@TestName("tcapi-selenium: Wait for frame exists")
@TestGroup({"tcrunij", "tcapi-selenium"})
public class TestWaitForFrame extends AbstractSeleniumTest
{
	private UUID uuid = UUID.fromString("66ab6056-2f2d-45ee-903f-dd3a748e56a4");

    @Override
    public void setup() throws Exception
    {
        browser.goTo("http://tcrun.org/UglyTestPage.html");
    }

    @Override
    public TestResult test() throws Exception
    {
		step("Go to ugly test page at http://tcrun.org/UglyTestPage.html", "Ugly test page with frames loads");
        browser.waitFor(UglyTestPage.class);
        check.that(browser.getText(UglyTestPage.ContentTitle), Is.EqualTo("The main content!"));
        //check.that(browser.exists(UglyTestPage.IframeContentTitle), Is.False());
		step("Click on create iframe link to create a nested iframe", "nested iframe is created after 2 seconds");
        browser.click(UglyTestPage.CreateIframeLink);
		Thread.sleep(2000);
        browser.waitFor(UglyTestPage.IframeContentTitle);
        check.that(browser.getText(UglyTestPage.IframeContentTitle), Is.EqualTo("The iframe content!"));
		check.that(browser.exists(UglyTestPage.ImageWithAltText), Is.True());
		check.that(browser.getAttribute(UglyTestPage.BugImage,"src"), Is.StringContaining("bug.gif"));
        return TestResult.PASS;
    }

	@Override
	public UUID getTestUUID()
	{
		return uuid;
	}
}
