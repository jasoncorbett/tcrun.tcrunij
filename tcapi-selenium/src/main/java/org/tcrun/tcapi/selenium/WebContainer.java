package org.tcrun.tcapi.selenium;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public interface WebContainer
{
	public WebElement findElement(WebDriver browser, PageElement item) throws NoSuchElementException;

	public String getFindByDescription();
}
