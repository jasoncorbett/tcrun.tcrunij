package org.tcrun.tcapi.selenium;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class FrameContainer implements WebContainer
{
	private String frameId;

	public FrameContainer(String frameId)
	{
		this.frameId = frameId;
	}

	@Override
	public WebElement findElement(WebDriver browser, PageElement item) throws NoSuchElementException
	{
		browser.switchTo().frame(frameId);
		WebElement element = browser.findElement(item.getFinder());
		browser.switchTo().defaultContent();

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
