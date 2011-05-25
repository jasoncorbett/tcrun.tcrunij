package org.tcrun.tcapi.selenium;

import java.util.Calendar;
import java.util.Date;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class PageElement 
{
	/**
	 * The amount of time (in milliseconds) which a cached WebElement (found by this class' getElement)
	 * is valid for, set to 3 seconds by default.
	 */
	public static int CACHE_VALID_FOR = 3000;

    private String name;
    private By finder;
    private WebContainer container;
    private WebElement cache;
    private Date lastCacheSave;

    public PageElement(String name, WebContainer container, By finder) {
        this.name = name;
        this.container = container;
        this.finder = finder;
	this.cache = null;
	this.lastCacheSave = null;
    }

    public PageElement(String name, By finder) {
        this.name = name;
        this.finder = finder;
        this.container = null;
	this.cache = null;
	this.lastCacheSave = null;
    }

    public PageElement(By finder) {
        name = finder.toString();
        this.finder = finder;
        this.container = null;
	this.cache = null;
	this.lastCacheSave = null;
    }

	/**
	 * Create a PageElement from an existing WebElement.  Normally you shouldn't do this, and by doing this exists()
	 * will always return true.  This is primarily here so we can return a list of child PageElements.
	 * 
	 * @param element 
	 */
	public PageElement(WebElement element)
	{
			name = "Pre Existing Webdriver WebElement";
			this.finder = null;
			this.container = null;
			this.cache = element;
			this.lastCacheSave = null;
	}

    public By getFinder() {
        return finder;
    }

    public String getName() {
        return name;
    }

    public boolean cacheIsValid()
    {
		if (lastCacheSave == null)
			return true; // special case: if we have a cached element, and no save date we always want to use that element
		if (((new Date()).getTime()) < (lastCacheSave.getTime() + PageElement.CACHE_VALID_FOR))
			return true;
		return false;
    }

    public WebElement getElement(WebDriver browser, int timeout) throws NoSuchElementException 
	{
		// use the cache
		if (cache != null && cacheIsValid())
			return cache;

        WebElement element = null;
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.SECOND, timeout);
        // Fixing Issue #17 - Need to be able to pass in a timeout of 0, or have no timeout to the PageElement.exists function
        // We need to ensure this executes at least once such as in
        // the case of the timeout being zero
        do {
            try {
                if (container == null) {
                    element = browser.findElement(finder);
                } else {
                    element = container.findElement(browser, this);
                }
                if (element != null) {
                    element.isEnabled(); // cause a NoSuchElementException if it can't find it
                }
            } catch (NoSuchElementException ex) {
                element = null;
            } catch (StaleElementReferenceException ex) {
                element = null;
            } catch (NoSuchFrameException ex) {
                element = null;
            } catch (WebDriverException ex) {
				element = null;
				if (ex.getMessage().contains("this.getWindow() is null"))
					browser.switchTo().defaultContent(); // hack for issue http://code.google.com/p/selenium/issues/detail?id=1438
			}
            // This is the same check as the while loop, but we don't want to sleep if we're already over
            // time (in the case that timeout == 0).
            if (element == null && Calendar.getInstance().before(endTime)) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                }
            }
        } while (Calendar.getInstance().before(endTime) && element == null);

        if (element == null) {
            throw new NoSuchElementException("Was unable to find element " + getName() + ", to be found by " + getFindByDescription());
        }
		cache = element;
		lastCacheSave = new Date();
        return element;
    }

    public String getFindByDescription() {
		if (cache != null && lastCacheSave == null)
		{
			// We created the page element from an existing WebElement
			return "Page Element created from an existing webdriver WebElement.";
		}
        if (container != null) {
            return container.getFindByDescription() + " " + finder.toString();
        } else {
            return finder.toString();
        }
    }

    public boolean exists(WebDriver browser, int timeout) {
        try {
            getElement(browser, timeout);
        } catch (NoSuchElementException ex) {
            return false;
        }

        return true;
    }
}
