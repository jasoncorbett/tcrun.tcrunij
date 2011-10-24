package org.tcrun.integration.tests.tcapiselenium.pages;

import org.tcrun.tcapi.selenium.PageElement;
import org.tcrun.tcapi.selenium.SelfAwarePage;
import org.tcrun.selenium.WebDriverWrapper;
import org.tcrun.tcapi.selenium.In;
import org.tcrun.tcapi.selenium.FindBy;

/**
 *
 * @author jcorbett
 */
public class DoubleClickTestPage implements SelfAwarePage<Object>
{
	public static PageElement DoubleClickLink = new PageElement("Link which will cause our test to pass if we double click it.", In.Frame("contentframe"), FindBy.id("dblclick-link"));
	public static PageElement TestResult = new PageElement("Result of the click test", In.Frame("contentframe"), FindBy.id("result"));

	@Override
	public boolean isCurrentPage(WebDriverWrapper browser)
	{
		return browser.exists(DoubleClickLink);
	}

	@Override
	public void handlePage(WebDriverWrapper browser, Object context) throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
