package org.tcrun.tcapi.selenium;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author jcorbett
 */
public class FrameContainer implements WebContainer
{
	private String frameId = null;
	private PageElement framePageElement;
 	private static XLogger logger = XLoggerFactory.getXLogger("test." + FrameContainer.class.getName());
	public FrameContainer(String frameId)
	{
		this.frameId = frameId;
	}
	// leeard 2/15/11 - adding ability to accept a PageElement for dynamic frame ID support
	public FrameContainer(PageElement framePageElement)
	{
		this.framePageElement = framePageElement;
	}
	@Override
	public WebElement findElement(WebDriver browser, PageElement item) throws NoSuchElementException
	{
		//browser.switchTo().frame(frameId);
		//browser.switchTo().defaultContent();
		WebElement element = null;

		// checking for the case of a PageElement being passed in
		if(frameId == null)
		{
			frameId = framePageElement.getElement(browser, 30).getAttribute("id");
			logger.debug("Found dynamic frame with id=\"{}\"", frameId);
		}
		try
		{
			String[] frames = frameId.split("\\.");
			for(String frame : frames)
			{
				browser.switchTo().frame(frame);
			}

			element = browser.findElement(item.getFinder());
		} finally
		{
			browser.switchTo().defaultContent();
		}

		WebElement retval = null;
		if(RenderedWebElement.class.isAssignableFrom(element.getClass()))
		{
			retval = new InFrameRenderedWebElement(element, browser, frameId);
		} else
		{
			retval = new InFrameWebElement(element, browser, frameId);
		}

		return retval;
	}

	@Override
	public String getFindByDescription()
	{
		return "In Frame '" + frameId + "'";
	}


}
