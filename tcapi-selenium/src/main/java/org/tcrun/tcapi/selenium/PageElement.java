package org.tcrun.tcapi.selenium;

import java.util.Calendar;
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
public class PageElement {

    private String name;
    private By finder;
    private WebContainer container;

    public PageElement(String name, WebContainer container, By finder) {
        this.name = name;
        this.container = container;
        this.finder = finder;
    }

    public PageElement(String name, By finder) {
        this.name = name;
        this.finder = finder;
        this.container = null;
    }

    public PageElement(By finder) {
        name = finder.toString();
        this.finder = finder;
        this.container = null;
    }

    public By getFinder() {
        return finder;
    }

    public String getName() {
        return name;
    }

    public WebElement getElement(WebDriver browser, int timeout) throws NoSuchElementException {
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
        return element;
    }

    public String getFindByDescription() {
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
