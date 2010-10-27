package org.tcrun.tcapi.selenium;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.internal.selenesedriver.GetPageSource;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RenderedRemoteWebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import java.util.Set;
import org.openqa.selenium.NoSuchWindowException;

/**
 *
 * @author jcorbett
 */
public class DefaultWebDriverWrapper implements WebDriverWrapper
{

	private WebDriver driver;
	private int timeout;
	private static XLogger logger = XLoggerFactory.getXLogger("test." + DefaultWebDriverWrapper.class.getName());

	public static WebDriver getDriverFromBrowserName(String name, String remote) throws MalformedURLException
	{
		URL remoteUrl = new URL("http://" + remote + ":4444/wd/hub");
		Capabilities caps = null;
		if (name.equalsIgnoreCase("ff")
		|| name.equalsIgnoreCase("firefox"))
		{
			caps = DesiredCapabilities.firefox();
		}

		if (name.equalsIgnoreCase("headless"))
		{
			caps = DesiredCapabilities.htmlUnit();
		}

		if (name.equalsIgnoreCase("ie")
		|| name.equalsIgnoreCase("internetExplorer"))
		{
			caps = DesiredCapabilities.internetExplorer();
		}

		if (name.equalsIgnoreCase("chrome"))
		{
			caps = DesiredCapabilities.chrome();
		}

		return new RemoteWebDriver(remoteUrl, caps);
	}

	public static WebDriver getDriverFromBrowserName(String name)
	{
		if (name.equalsIgnoreCase("ff")
		|| name.equalsIgnoreCase("firefox"))
		{
			return new FirefoxDriver();
		}

		if (name.equalsIgnoreCase("headless"))
		{
			HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3);
			driver.setJavascriptEnabled(true);
			return driver;
		}

		if (name.equalsIgnoreCase("ie")
		|| name.equalsIgnoreCase("InternetExplorer"))
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
		} catch (NoSuchElementException ex)
		{
			logger.error("Element with name {} and found {} was not found after {} seconds.", new Object[]
			{
				locator.getName(), locator.getFindByDescription(), timeout
			});
			logger.error("Current page URL: {}", driver.getCurrentUrl());
			logger.error("Current page title: {}", driver.getTitle());
			logger.error("Current page source: {}", driver.getPageSource());
			throw ex;
		}
		return element;
	}

        @Override
	public void click(PageElement locator)
	{
		click(locator, timeout);
	}

        @Override
	public void click(PageElement locator, int timeout)
	{
		logger.debug("Clicking on element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
		getElement(locator, timeout).click();
	}

        @Override
	public void clear(PageElement locator)
	{
		clear(locator, timeout);
	}

        @Override
	public void clear(PageElement locator, int timeout)
	{
		logger.debug("Clearing the text from element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
		getElement(locator, timeout).clear();
	}

        @Override
        public void submit(PageElement locator)
        {
                submit(locator, timeout);
        }

        @Override
	public void submit(PageElement locator, int timeout)
	{
		logger.debug("Submitting an element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
		getElement(locator, timeout).submit();
	}

        @Override
	public void type(PageElement locator, String text, int timeout)
	{
		logger.debug("Typing text '{}' in element with name '{}' and found '{}'.", new Object[]
		{
			text, locator.getName(), locator.getFindByDescription()
		});
		getElement(locator, timeout).sendKeys(text);
	}

        @Override
	public void type(PageElement locator, String text)
	{
		type(locator, text, timeout);
	}

	@Override
	public String getText(PageElement locator)
	{
		logger.debug("Getting text from element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
		return getText(locator, timeout);
	}

	@Override
	public String getText(PageElement locator, int timeout)
	{

		return getElement(locator, timeout).getText();
	}

	@Override
	public String getAttribute(PageElement locator, String attribute)
	{
		return getAttribute(locator, timeout, attribute);
	}

	@Override
	public String getAttribute(PageElement locator, int timeout, String attribute)
	{
		logger.debug("Getting attribute '" + attribute + "' from element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
		return getElement(locator, timeout).getAttribute(attribute);
	}

	@Override
	public String getPageTitle()
	{
		logger.debug("Getting current page title.");
		return driver.getTitle();
	}

        @Override
        public String getPageSource()
        {
                logger.debug("Getting current page html source.");
                return driver.getPageSource();
        }

        @Override
        public String getPageUrl()
        {
                logger.debug("Getting current page url.");
                return driver.getCurrentUrl();
        }

	@Override
	public void goTo(String url)
	{
		logger.debug("Going to page '{}'.", url);
		driver.get(url);
	}

        @Override
        public void goBack()
        {
                logger.debug("Going back in the browser.");
                driver.navigate().back();
        }

        @Override
        public void goForward()
        {
                logger.debug("Going forward in the browser.");
                driver.navigate().forward();
        }

	public WebDriver getDriver()
	{
		return driver;
	}

	@Override
	public void waitFor(Class<? extends SelfAwarePage> page)
	{
		waitFor(page, timeout);
	}

	@Override
	public void waitFor(Class<? extends SelfAwarePage> page, int timeout)
	{
		logger.debug("Waiting for page '{}' a max of {} seconds.", page.getName(), timeout);
		try
		{
			SelfAwarePage page_instance = page.newInstance();
			Date start_time = new Date();
			Calendar end_time = Calendar.getInstance();
			end_time.add(Calendar.SECOND, timeout);
			while (Calendar.getInstance().before(end_time) && !page_instance.isCurrentPage(this))
			{
				try
				{
					Thread.sleep(200);
				} catch (InterruptedException ex)
				{
				}
			}
			if (!page_instance.isCurrentPage(this))
			{
				logger.error("Waited for page '{}' for {} seconds, but still is not here.", page.getName(), timeout);
				logger.error("Current page URL: {}", driver.getCurrentUrl());
				logger.error("Current page title: {}", driver.getTitle());
				logger.error("Current page source: {}", driver.getPageSource());
				throw new NoSuchElementException("Couldn't find page '" + page.getName() + "' after " + timeout + " seconds.");
			}
			logger.info("Found page '{}' after {} seconds.", page.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
		} catch (InstantiationException ex)
		{
			logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
			throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
		} catch (IllegalAccessException ex)
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
		} catch (InstantiationException ex)
		{
			logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
			throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
		} catch (IllegalAccessException ex)
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
		} catch (InstantiationException ex)
		{
			logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
			throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
		} catch (IllegalAccessException ex)
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
		logger.debug("Selecting option with display text '{}' of select list '{}' found by '{}' waiting a max timeout of {} seconds.", new Object[]
		{
			option, selectList.getName(), selectList.getFinder(), timeout
		});
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
		logger.debug("Selecting option with value '{}' of select list '{}' found by '{}' waiting a max timeout of {} seconds.", new Object[]
		{
			optionValue, selectList.getName(), selectList.getFinder(), timeout
		});
		Select selectInput = new Select(getElement(selectList, timeout));
		selectInput.selectByValue(optionValue);
	}

	@Override
	public void waitFor(PageElement element)
	{
		waitFor(element, timeout);
	}

	@Override
	public void waitFor(PageElement element, int timeout)
	{
		logger.debug("Waiting for element '{}' a max of {} seconds.", element.getName(), timeout);
		Date start_time = new Date();
		getElement(element, timeout);
		logger.info("Found element '{}' after {} seconds.", element.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
	}

	@Override
	public void waitForVisible(PageElement element)
	{
		waitForVisible(element, timeout);
	}

	@Override
	public void waitForVisible(PageElement element, int timeout)
	{
		logger.debug("Waiting a max of {} seconds for element '{}' found by {} to become visible.", new Object[]
		{
			timeout, element.getName(), element.getFindByDescription()
		});
		Calendar end_time = Calendar.getInstance();
		Date start_time = end_time.getTime();
		end_time.add(Calendar.SECOND, timeout);
		WebElement wdelement = getElement(element, timeout);
		if (RenderedWebElement.class.isAssignableFrom(wdelement.getClass()))
		{
			RenderedWebElement relement = (RenderedWebElement) wdelement;
			logger.debug("Found element '{}' after {} seconds, waiting for it to become visible.", element.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
			while (!relement.isDisplayed() && (Calendar.getInstance().before(end_time)))
			{
				try
				{
					Thread.sleep(200);
				} catch (InterruptedException e)
				{
					logger.debug("Caught interrupted exception, while waiting for element, but it shouldn't cause too much trouble: {}", e.getMessage());
				}
			}
			if (!relement.isDisplayed())
			{
				throw new ElementNotVisibleException(MessageFormatter.format("Waited {} seconds for element {} found by {} to become visible, and it never happened.", new Object[]
				{
					timeout, element.getName(), element.getFindByDescription()
				}).getMessage());
			}
			logger.debug("Element '{}' was found visisble after {} seconds.", element.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
		} else
		{
			logger.warn("The current browser doesn't return RenderedWebElements, so I can't check for visibility.  Hopefully that means webdriver won't either.");
		}
	}

	@Override
	public boolean exists(PageElement element)
	{
		return element.exists(driver, 0);
	}

	@Override
	public boolean exists(Class<? extends SelfAwarePage> page)
	{
		logger.debug("Checking for existence of page '{}'.", page.getName());
		try
		{
			SelfAwarePage page_instance = page.newInstance();
			if (!page_instance.isCurrentPage(this))
			{
				logger.debug("Checked for page '{}' , but it does not exist.", page.getName());
				return false;
			}
			logger.info("Found page '{}'.", page.getName());
			return true;
		} catch (InstantiationException ex)
		{
			logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
			throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
		} catch (IllegalAccessException ex)
		{
			logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
			throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
		}
	}

        @Override
        public String getWindowHandle()
        {
                return driver.getWindowHandle();
        }

        @Override
	public Set<String> getWindowHandles()
        {
                return driver.getWindowHandles();
        }

        @Override
	public void switchToWindowByHandle(String windowHandle)
        {
                driver.switchTo().window(windowHandle);
        }

        @Override
	public void switchToWindowByURL(String windowURL)
        {
                String switchToWindowHandle = "";
                String startWindowHandle = getWindowHandle();
                logger.debug("Switching to the window with the URL containing '{}'.", windowURL);
                for (String possibleHandle : getWindowHandles())
                {
                        switchToWindowByHandle(startWindowHandle);
                        if (getPageUrl().contains("help") == true)
                                switchToWindowHandle = possibleHandle;
                }
                if (switchToWindowHandle.isEmpty() == false)
                        switchToWindowByHandle(switchToWindowHandle);
                else
                {
                        logger.error("Unable to find window with URL containing '{}'.", windowURL);
		        throw new NoSuchWindowException("Unable to find window with URL containing '{" + windowURL + "}'");
                }

        }
}
