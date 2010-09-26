package org.tcrun.tcapi.selenium;

import org.openqa.selenium.WebDriver;

/**
 *
 * @author jcorbett
 */
public interface WebDriverWrapper
{
	public void setDefaultTimeout(int timeout);

	public void click(PageElement locator);

	public void click(PageElement locator, int timeout);

	public void type(PageElement locator, String text, int timeout);

	public void type(PageElement locator, String text);

	public String getText(PageElement locator);

	public String getText(PageElement locator, int timeout);

	public String getPageTitle();

	public void goTo(String url);

	public WebDriver getDriver();

	public void waitForPage(Class<? extends SelfAwarePage> page);

	public void waitForPage(Class<? extends SelfAwarePage> page, int timeout);

	public <T> void handlePage(Class<? extends SelfAwarePage<T>> page, T context);
}
