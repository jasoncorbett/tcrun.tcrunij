package org.tcrun.integration.tests.tcapiselenium.pages;

import org.tcrun.selenium.*;

/**
 *
 * @author jcorbett
 */
public class DynamicTextTestPage implements SelfAwarePage<Object>, PageWithActions
{
	public static PageElement PageContentTitle = new PageElement("DynamicTextTestPage.PageContentTitle", In.Frame("contentframe"), FindBy.id("content-title"));
	public static PageElement StartTestLink = new PageElement("DynamicTextTestPage.StartTestLink", In.Frame("contentframe"), FindBy.id("activate-test"));
	public static PageElement DynamicTextElement = new PageElement("DynamicTextTestPage.DynamicTextElement", In.Frame("contentframe"), FindBy.id("result"));

	private WebDriverWrapper browser = null;

	@Override
	public boolean isCurrentPage(WebDriverWrapper browser)
	{
		return browser.exists(PageContentTitle, false) && browser.getText(PageContentTitle).contains("Dynamic Text");
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

	public void makeTextAppear()
	{
		browser.click(StartTestLink);
	}
	
}
