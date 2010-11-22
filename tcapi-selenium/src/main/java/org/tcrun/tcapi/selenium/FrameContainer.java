package org.tcrun.tcapi.selenium;

import org.openqa.selenium.NoSuchElementException;
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
		return browser.findElement(item.getFinder());
	}

	@Override
	public String getFindByDescription()
	{
		return "In Frame '" + frameId + "'";
	}


}
