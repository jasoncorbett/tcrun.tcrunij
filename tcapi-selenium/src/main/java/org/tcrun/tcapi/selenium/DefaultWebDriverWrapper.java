package org.tcrun.tcapi.selenium;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.io.File;
import java.io.IOException;
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
import java.util.Calendar;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 *
 * @author jcorbett
 */
public class DefaultWebDriverWrapper implements WebDriverWrapper
{

	private WebDriver driver;
	private Capabilities driver_capabilities;
	private int timeout;
	private static XLogger logger = XLoggerFactory.getXLogger("test." + DefaultWebDriverWrapper.class.getName());
	private int screenshot_counter;
        private int htmlsource_counter;
        private static String original_browser_window_handle = "";

	public static WebDriver getDriverFromCapabilities(Capabilities caps)
	{
		if(caps.getCapability(RemoteDriverWithScreenshots.REMOTE_URL) == null)
		{
			if(caps.getBrowserName().equals(DesiredCapabilities.htmlUnit().getBrowserName()))
			{
				HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
				driver.setJavascriptEnabled(true);
				return driver;
			} else if(caps.getBrowserName().equals(DesiredCapabilities.firefox().getBrowserName()))
			{
				if(caps.getPlatform() == Platform.WINDOWS)
				{
					FirefoxProfile profile = new FirefoxProfile();
					profile.setPreference("general.useragent.override", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.10) Gecko/20100914 Firefox/3.6.10 (.NET CLR 2.0.40607)");
					return new FirefoxDriver(profile);
				} else
				{
					return new FirefoxDriver();
				}
			} else if(caps.getBrowserName().equals(DesiredCapabilities.internetExplorer().getBrowserName()))
			{
				return new InternetExplorerDriver();
			} else if(caps.getBrowserName().equals(DesiredCapabilities.chrome().getBrowserName()))
			{
				return new ChromeDriver();
			} else
			{
				return new FirefoxDriver();
			}
		} else
		{
			try
			{
				return new RemoteDriverWithScreenshots(caps);
			} catch (MalformedURLException ex)
			{
				logger.error("Invalid URL for remote webdriver '" + caps.getCapability(RemoteDriverWithScreenshots.REMOTE_URL) + "': ", ex);
				return null;
			}
		}
	}

	protected DefaultWebDriverWrapper(WebDriver driver)
	{
		this.driver = driver;
		screenshot_counter = 0;
                htmlsource_counter = 0;  
	}

	public DefaultWebDriverWrapper(Capabilities caps)
	{
		this(getDriverFromCapabilities(caps));
		driver_capabilities = caps;
	}

	@Override
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
                        saveHTMLSource();
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
		clear(locator, timeout);
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
                return getPageTitle(true);
        }

	@Override
	public String getPageTitle(boolean should_log)
	{
                if (should_log == true)
		        logger.debug("Getting current page title.");
		return driver.getTitle();
	}

        @Override
	public String getPageSource()
	{
                return getPageSource(true);
        }

	@Override
	public String getPageSource(boolean should_log)
	{
                if (should_log == true)
		        logger.debug("Getting current page html source.");
		return driver.getPageSource();
	}

        @Override
        public String getPageUrl()
	{
                return getPageUrl(true);
        }

	@Override
	public String getPageUrl(boolean should_log)
	{
                if (should_log == true)
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
                                saveHTMLSource();
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
	public <T> void handlePage(Class<? extends SelfAwarePage<T>> page, T context) throws Exception
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
		return exists(element, true);
	}

        @Override
	public boolean exists(PageElement element, boolean should_log)
	{
                if (should_log == true)
		        logger.debug("Checking for existence of element '{}'.", element.getName());
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
		logger.debug("Getting current browser window handle");
		return driver.getWindowHandle();
        }

	@Override
	public Set<String> getWindowHandles()
	{
		logger.debug("Getting all browser window handles");
		return driver.getWindowHandles();
        }

	@Override
	public void switchToWindowByHandle(String windowHandle)
	{
                logger.debug("Switching to the window with handle '{}'.", windowHandle);
                if (original_browser_window_handle.equals("") == true)
                        original_browser_window_handle = getWindowHandle();
		driver.switchTo().window(windowHandle);
	}

	@Override
	public void switchToWindowByURL(String windowURL)
	{
		switchToWindowByURL(windowURL, timeout);
	}

	@Override
	public void switchToWindowByURL(String windowURL, int timeout)
	{
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.SECOND, timeout);
		String switchToHandle = "";
		logger.debug("Looking for the window with the URL containing '{}'.", windowURL);
		while (true)
		{
			if (Calendar.getInstance().after(endTime))
			{
				logger.error("Unable to find window with URL containing '{}'.", windowURL);
				logger.error("Switching back to the original window: " + original_browser_window_handle);
				switchToWindowByHandle(original_browser_window_handle);
				throw new NoSuchWindowException("Timed out waiting for a known page");
			}
			for (String possibleHandle : getWindowHandles())
			{
				switchToWindowByHandle(possibleHandle);
                                
				if (getPageUrl(false).toLowerCase().contains(windowURL.toLowerCase()) == true)
				{
					switchToHandle = possibleHandle;
				}  else
                                {
                                        try
                                        {
                                                java.lang.Thread.sleep(1000);
                                        }
                                        catch (Exception e) {}
                                }
			}
			if (switchToHandle.isEmpty() == false)
			{
				logger.debug("Found the window with the URL containing '{}', switching to it now.", windowURL);
				switchToWindowByHandle(switchToHandle);
				break;
			}
		}

	}

	@Override
	public void closeWindow()
	{
		logger.debug("Closing current browser window");
		driver.close();
	}

	@Override
	public void closeWindow(String windowHandle)
	{
		logger.debug("Closing the the window with handle '{}'.", windowHandle);
		switchToWindowByHandle(windowHandle);
		closeWindow();
	}

        @Override
	public boolean isVisible(PageElement locator)
        {
                return isVisible(locator, true);
        }
	@Override
	public boolean isVisible(PageElement locator, boolean should_log)
	{
		boolean elementVisible = true;
                if (should_log == true)
		        logger.debug("Checking visibility on element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
		if (exists(locator) == true)
		{
			WebElement wdelement = getElement(locator, timeout);
			if (RenderedWebElement.class.isAssignableFrom(wdelement.getClass()))
			{
				RenderedWebElement relement = (RenderedWebElement) wdelement;
				elementVisible = relement.isDisplayed();
			}
			if (elementVisible)
			{
				logger.debug("Found visible element with name '{}' and found '{}'", locator.getName(), locator.getFindByDescription());
			} else
			{
				logger.debug("Element was NOT VISIBLE with name '{}' and found '{}'", locator.getName(), locator.getFindByDescription());
			}
		} else
		{
			elementVisible = false;
		}
		return elementVisible;
	}

	@Override
	public void takeScreenShot()
	{
		takeScreenShot("screenshot");
	}

	@Override
	public void takeScreenShot(String name)
	{
		if (TakesScreenshot.class.isAssignableFrom(driver.getClass()))
		{
			if (name == null)
			{
				name = "screenshot";
			}
			if (!name.toLowerCase().endsWith(".png"))
			{
				name = name + ".png";
			}
			name = ++screenshot_counter + "-" + name;
			File ss_file = DebugSupport.getOutputFile(name);
			logger.debug("Taking screenshot, output file will be {}", ss_file.getAbsolutePath());
			File ss_tmp = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			try
			{
				FileUtils.copyFile(ss_tmp, ss_file);
			} catch (IOException ex)
			{
				logger.error("Unable to copy screenshot from '" + ss_tmp.getAbsolutePath() + "' to '" + ss_file.getAbsolutePath() + "': ", ex);
			}
		} else
		{
			logger.warn("Requested screenshot by name '{}', but browser doesn't support taking screenshots.", name);
		}

	}

        @Override
	public void saveHTMLSource()
	{
		saveHTMLSource("pagesource");
	}

	@Override
	public void saveHTMLSource(String name)
	{
                if (name == null)
                {
                        name = "pagesource";
                }
                if (!name.toLowerCase().endsWith(".html"))
                {
                        name = name + ".html";
                }
                name = ++htmlsource_counter + "-" + name;
                File src_file = DebugSupport.getOutputFile(name);
                logger.debug("Saving current page HTML source, output file will be {}", src_file.getAbsolutePath());
                try
                {
                        FileUtils.writeStringToFile(src_file, getPageSource());
                } catch (IOException ex)
                {
                        logger.error("Unable to save the current page HTML source to the file '" + src_file.getAbsolutePath() + "': ", ex);
                }

	}

	@Override
	public void reopen()
	{
		driver.quit();
		driver = getDriverFromCapabilities(driver_capabilities);
	}

        @Override
        public void closeAllWindowsExceptOriginal()
        {
                logger.info("Closing all browser windows except: " + original_browser_window_handle);
                for (String windowHandle : getWindowHandles())
                {
                        if (windowHandle.equals(original_browser_window_handle) == false)
                            closeWindow(windowHandle);
                }
                switchToWindowByHandle(original_browser_window_handle);
        }
}
