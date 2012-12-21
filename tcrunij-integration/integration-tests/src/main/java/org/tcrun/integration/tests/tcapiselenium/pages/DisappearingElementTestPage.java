package org.tcrun.integration.tests.tcapiselenium.pages;

import org.tcrun.selenium.FindBy;
import org.tcrun.selenium.In;
import org.tcrun.selenium.PageElement;
import org.tcrun.selenium.PageWithActions;
import org.tcrun.selenium.SelfAwarePage;
import org.tcrun.selenium.WebDriverWrapper;

/**
 *
 * @author jcorbett
 */
public class DisappearingElementTestPage implements SelfAwarePage<Object>, PageWithActions
{
	public static PageElement PageContentTitle = new PageElement("DisappearingElementTestPage.PageContentTitle", In.Frame("contentframe"), FindBy.id("content-title"));
	public static PageElement StartTestLink = new PageElement("DisappearingElementTestPage.StartTestLink", In.Frame("contentframe"), FindBy.id("activate-test"));
	public static PageElement DisappearingElement = new PageElement("DisappearingElementTestPage.DisappearingElement", In.Frame("contentframe"), FindBy.id("result"));
	public static PageElement DisappearingElementContainer = new PageElement("DisappearingElementTestPage.DisappearingElementContainer", In.Frame("contentframe"), FindBy.id("result-container"));

	private WebDriverWrapper browser = null;

	@Override
	public boolean isCurrentPage(WebDriverWrapper browser)
	{
		return browser.exists(PageContentTitle, false) && browser.getText(PageContentTitle).contains("Disappearing Element");
	}

	@Override
	public void handlePage(WebDriverWrapper browser, Object context) throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void initializePage(WebDriverWrapper browser)
	{
		this.browser = browser;
	}

	public void makeElementDisappear()
	{
		browser.click(StartTestLink);
	}
	
}
