package org.tcrun.tcapi.selenium;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.util.Calendar;
import java.util.Date;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author jcorbett
 */
public class DefaultWebDriverWrapper implements WebDriverWrapper
{
	private WebDriver driver;
	private int timeout;
	private static XLogger logger = XLoggerFactory.getXLogger("test." + DefaultWebDriverWrapper.class.getName());

	public static WebDriver getDriverFromBrowserName(String name)
	{
		if (name.equalsIgnoreCase("ff") ||
		    name.equalsIgnoreCase("firefox"))
		{
			return new FirefoxDriver();
		}

		if (name.equalsIgnoreCase("headless"))
		{
			HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3);
			driver.setJavascriptEnabled(true);
			return driver;
		}

		if (name.equalsIgnoreCase("ie") ||
		    name.equalsIgnoreCase("InternetExplorer"))
		{
			return new InternetExplorerDriver();
		}

		throw new IllegalArgumentException("Browser driver with name '" + name + "' not supported yet.");
	}

	public DefaultWebDriverWrapper(WebDriver driver)
	{
		this.driver = driver;
	}

	public DefaultWebDriverWrapper(String name)
	{
		this(DefaultWebDriverWrapper.getDriverFromBrowserName(name));
	}

	public void setDefaultTimeout(int timeout)
	{
		this.timeout = timeout;
	}

	public void checkForElementExists(PageElement locator, int timeout)
	{
		if(!locator.exists(driver, timeout))
		{
			logger.error("Element with name {} and locator {} was not found after {} seconds.", new Object[] {locator.getName(), locator.getFinder(), timeout});
			logger.error("Current page URL: {}", driver.getCurrentUrl());
			logger.error("Current page title: {}", driver.getTitle());
			logger.error("Current page source: {}", driver.getPageSource());
			throw new NoSuchElementException("Couldn't find element with locator " + locator.getFinder());
		}
	}

	public void click(PageElement locator)
	{
		click(locator, timeout);
	}

	public void click(PageElement locator, int timeout)
	{
		checkForElementExists(locator, timeout);
		driver.findElement(locator.getFinder()).click();
	}

	public void type(PageElement locator, String text, int timeout)
	{
		checkForElementExists(locator, timeout);
		driver.findElement(locator.getFinder()).sendKeys(text);
	}

	public void type(PageElement locator, String text)
	{
		type(locator, text, timeout);
	}

	public String getText(PageElement locator)
	{
		return getText(locator, timeout);
	}

	public String getText(PageElement locator, int timeout)
	{
		checkForElementExists(locator, timeout);
		return driver.findElement(locator.getFinder()).getText();
	}

	@Override
	public String getPageTitle()
	{
		return driver.getTitle();
	}

	@Override
	public void goTo(String url)
	{
		logger.debug("Going to page '{}'.", url);
		driver.get(url);
	}

	public WebDriver getDriver()
	{
		return driver;
	}

	@Override
	public void waitForPage(Class<? extends SelfAwarePage> page)
	{
		waitForPage(page, timeout);
	}

	@Override
	public void waitForPage(Class<? extends SelfAwarePage> page, int timeout)
	{
		logger.debug("Waiting for page '{}'.", page.getName());
		try
		{
			SelfAwarePage page_instance = page.newInstance();
			Date start_time = new Date();
			Calendar end_time = Calendar.getInstance();
			end_time.add(Calendar.SECOND, timeout);
			while(Calendar.getInstance().before(end_time) && !page_instance.isCurrentPage(this))
			{
				try
				{
					Thread.sleep(200);
				} catch(InterruptedException ex)
				{
				}
			}
			if(!page_instance.isCurrentPage(this))
			{
				logger.error("Waited for page '{}' for {} seconds, but still is not here.", page.getName(), timeout);
				logger.error("Current page URL: {}", driver.getCurrentUrl());
				logger.error("Current page title: {}", driver.getTitle());
				logger.error("Current page source: {}", driver.getPageSource());
				throw new NoSuchElementException("Couldn't find page '" + page.getName() + "' after " + timeout + " seconds.");
			}
			logger.info("Found page '{}' after {} seconds.", page.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000 );
		} catch(InstantiationException ex)
		{
			logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
			throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
		} catch(IllegalAccessException ex)
		{
			logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
			throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
		}
	}

	public <T> void handlePage(Class<? extends SelfAwarePage<T>> page, T context)
	{
		try
		{
			SelfAwarePage<T> page_instance = page.newInstance();
			page_instance.handlePage(this, context);
		} catch(InstantiationException ex)
		{
			logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
			throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
		} catch(IllegalAccessException ex)
		{
			logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
			throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
		}
	}
}