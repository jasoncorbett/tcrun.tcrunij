package org.tcrun.selenium;

import org.openqa.selenium.NoSuchElementException;
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
	private boolean noFrameId = false;
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
		noFrameId = false;
		WebElement frameWebElement = null;
		WebElement element = null;

		// checking for the case of a PageElement being passed in
		if(frameId == null)
		{
			frameId = framePageElement.getElement(browser, 30).getAttribute("id");
			if(!frameId.equals(""))
			{
				logger.debug("Found dynamic frame with id=\"{}\"", frameId);
			} else
			{
				// If the frame does not have an id we will need to switch to it via the WebElement
				frameWebElement = framePageElement.getElement(browser, 30);
				noFrameId = true;
				frameId = null;
				logger.debug("Found frame without and id");
			}
		}
		try
		{
			if(frameWebElement == null)
			{
				String[] frames = frameId.split("\\.");
				for(String frame : frames)
				{
					browser.switchTo().frame(frame);
				}
			} else
			{
				if(InFrameWebElement.class.isAssignableFrom(frameWebElement.getClass()))
				{
					((InFrameWebElement)frameWebElement).beforeOperation();
					browser.switchTo().frame(((InFrameWebElement)frameWebElement).real);
				} else
				{
					browser.switchTo().frame(frameWebElement);
				}
			}

			element = browser.findElement(item.getFinder());
		} finally
		{
			browser.switchTo().defaultContent();
		}

		WebElement retval = null;
		if(frameWebElement == null)
		{
			retval = new InFrameWebElement(item.getFinder(), browser, frameId);
		} else
		{
			retval = new InFrameWebElement(item.getFinder(), browser, frameWebElement);
		}

		return retval;
	}

	@Override
	public String getFindByDescription()
	{
		if(noFrameId)
		{
			return "In Frame 'frame without an id'";
		} else
		{
			return "In Frame '" + frameId + "'";
		}
	}
}
