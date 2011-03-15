package org.tcrun.tcapi.selenium;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class ParentElementContainer implements WebContainer
{
	private PageElement parent;

	public ParentElementContainer(PageElement parent)
	{
		this.parent = parent;
	}

	@Override
	public WebElement findElement(WebDriver browser, PageElement item) throws NoSuchElementException
	{
		// fail fast, the child element will handle the timing
		return parent.getElement(browser, 0).findElement(item.getFinder());
	}

	@Override
	public String getFindByDescription()
	{
		return "Inside parent element found " + parent.getFindByDescription();
	}
}
