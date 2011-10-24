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
public class DynamicFrameTestPage implements SelfAwarePage<Object>
{
	public static PageElement FrameExplanationParagraph = new PageElement("Paragraph element explaining dynamic frame.", In.Frame("contentframe"), FindBy.id("dynamicframe-explanation"));
	public static PageElement IFrameWithoutId = new PageElement("iframe inside the content frame without an id.", In.Frame("contentframe"), FindBy.tagName("iframe"));
    public static PageElement IframeContentTitle = new PageElement("Content title element of iframe", In.Frame(IFrameWithoutId), FindBy.id("content-title"));

	@Override
	public boolean isCurrentPage(WebDriverWrapper browser)
	{
		return browser.exists(FrameExplanationParagraph);
	}

	@Override
	public void handlePage(WebDriverWrapper browser, Object context) throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
