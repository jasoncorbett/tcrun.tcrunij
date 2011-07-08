package org.tcrun.integration.tests.tcapiselenium.pages;

import org.tcrun.tcapi.selenium.PageElement;
import org.tcrun.tcapi.selenium.SelfAwarePage;
import org.tcrun.tcapi.selenium.WebDriverWrapper;
import org.tcrun.tcapi.selenium.In;
import org.openqa.selenium.By;
import org.tcrun.tcapi.selenium.FindBy;

/**
 *
 * @author jcorbett
 */
public class UglyTestPage implements SelfAwarePage<Object>
{
	public static String Url = "http://tcrunij.tcrun.googlecode.com/hg/tcrunij-integration/integration-tests/src/main/html/UglyTestPage.html";

    public static PageElement ContentTitle = new PageElement("title element of main content frame", In.Frame("contentframe"), FindBy.id("content-title"));
    public static PageElement CreateIframeLink = new PageElement("Link which creates an iframe after 1 second", In.Frame("contentframe"), FindBy.id("create-iframe-link"));
    public static PageElement IframeContentTitle = new PageElement("Content title element of iframe", In.Frame("contentframe.contentiframe"), FindBy.id("content-title"));
	public static PageElement ImageWithAltText = new PageElement("Image with alt text that says 'Hello World'.", In.Frame("contentframe"), FindBy.alt("Hello World"));
	public static PageElement BugImageDiv = new PageElement("Parent div for bug picture", In.Frame("contentframe"), By.id("image-parent"));
	public static PageElement BugImage = new PageElement("Image of a bug.", In.ParentElement(BugImageDiv), FindBy.alt("Hello World"));
	public static PageElement DoubleClickTestLink = new PageElement("Link to Double click test page.", In.Frame("menuframe"), FindBy.linkText("Double Click Test"));
	public static PageElement DynamicFrameTestLink = new PageElement("Link to Dynamic Frame test page.", In.Frame("menuframe"), FindBy.linkText("Dynamic Frame Test"));

    @Override
    public boolean isCurrentPage(WebDriverWrapper browser)
    {
        return browser.exists(ContentTitle, false);
    }

    @Override
    public void handlePage(WebDriverWrapper wdw, Object t)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
