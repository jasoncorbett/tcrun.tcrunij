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

	public String getAttribute(PageElement locator, String attribute);

	public String getAttribute(PageElement locator, int timeout, String attribute);

	public void selectByOptionText(PageElement selectList, String option);

	public void selectByOptionText(PageElement selectList, String option, int timeout);

	public void selectByOptionValue(PageElement selectList, String optionValue);

	public void selectByOptionValue(PageElement selectList, String optionValue, int timeout);

	public String getPageTitle();

	public void goTo(String url);

	public WebDriver getDriver();

	public void waitFor(Class<? extends SelfAwarePage> page);

	public void waitFor(Class<? extends SelfAwarePage> page, int timeout);

	public void waitFor(PageElement element);

	public void waitFor(PageElement element, int timeout);

	public boolean exists(PageElement element);

	public boolean exists(Class<? extends SelfAwarePage> page);

	public <T> void handlePage(Class<? extends SelfAwarePage<T>> page, T context);

	public boolean isCurrentPage(Class<? extends SelfAwarePage> page);
}
