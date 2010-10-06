package org.tcrun.tcapi.selenium;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.util.Calendar;
import java.util.Date;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Select;
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

		if (name.equalsIgnoreCase("Chrome"))
		{
			return new ChromeDriver();
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

	public WebElement getElement(PageElement locator, int timeout)
	{
		WebElement element = null;
		try
		{
			element = locator.getElement(driver, timeout);
		} catch(NoSuchElementException ex)
		{
			logger.error("Element with name {} and found {} was not found after {} seconds.", new Object[] {locator.getName(), locator.getFindByDescription(), timeout});
			logger.error("Current page URL: {}", driver.getCurrentUrl());
			logger.error("Current page title: {}", driver.getTitle());
			logger.error("Current page source: {}", driver.getPageSource());
			throw ex;
		}
		return element;
	}

	public void click(PageElement locator)
	{
		logger.debug("Clicking on element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
		click(locator, timeout);
	}

	public void click(PageElement locator, int timeout)
	{
		getElement(locator, timeout).click();
	}

	public void type(PageElement locator, String text, int timeout)
	{
		logger.debug("Typing text '{}' in element with name '{}' and found '{}'.", new Object[] {text, locator.getName(), locator.getFindByDescription()});
		getElement(locator, timeout).sendKeys(text);
	}

	public void type(PageElement locator, String text)
	{
		type(locator, text, timeout);
	}

	public String getText(PageElement locator)
	{
		logger.debug("Getting text from element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
		return getText(locator, timeout);
	}

	public String getText(PageElement locator, int timeout)
	{

		return getElement(locator, timeout).getText();
	}

	@Override
	public String getPageTitle()
	{
		logger.debug("Getting current page title.");
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
		logger.debug("Waiting for page '{}' a max of {} seconds.", page.getName(), timeout);
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

        @Override
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

        @Override
        public boolean isCurrentPage(Class<? extends SelfAwarePage> page)
	{
		try
		{
			SelfAwarePage page_instance = page.newInstance();
			return page_instance.isCurrentPage(this);
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

	@Override
	public void selectByOptionText(PageElement selectList, String option)
	{
		selectByOptionText(selectList, option, timeout);
	}

	@Override
	public void selectByOptionText(PageElement selectList, String option, int timeout)
	{
		logger.debug("Selecting option with display text '{}' of select list '{}' found by '{}' waiting a max timeout of {} seconds.", new Object[] {option, selectList.getName(), selectList.getFinder(), timeout});
		Select selectInput = new Select(getElement(selectList, timeout));
		selectInput.selectByVisibleText(option);
	}

	@Override
	public void selectByOptionValue(PageElement selectList, String optionValue)
	{
		selectByOptionValue(selectList, optionValue, timeout);
	}

	@Override
	public void selectByOptionValue(PageElement selectList, String optionValue, int timeout)
	{
		logger.debug("Selecting option with value '{}' of select list '{}' found by '{}' waiting a max timeout of {} seconds.", new Object[] {optionValue, selectList.getName(), selectList.getFinder(), timeout});
		Select selectInput = new Select(getElement(selectList, timeout));
		selectInput.selectByValue(optionValue);
	}

}
