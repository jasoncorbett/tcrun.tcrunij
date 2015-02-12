package org.tcrun.selenium;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 *
 * @author jcorbett
 */
public class DefaultWebDriverWrapper implements WebDriverWrapper {

    private WebDriver driver;
    private Capabilities driver_capabilities;
    private int timeout;
    private static XLogger logger = XLoggerFactory.getXLogger("test." + DefaultWebDriverWrapper.class.getName());
    private int screenshot_counter;
    private int htmlsource_counter;
    private static String original_browser_window_handle = "";
    private OutputFileSupport debugSupport;

    public static WebDriver getDriverFromCapabilities(Capabilities caps) {
        if (caps.getCapability(RemoteDriverWithScreenshots.REMOTE_URL) == null) {
            if (caps.getBrowserName().equals(DesiredCapabilities.htmlUnit().getBrowserName())) {
                HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_24);
                driver.setJavascriptEnabled(true);
                return driver;
            } else if (caps.getBrowserName().equals(DesiredCapabilities.firefox().getBrowserName())) {
                if (caps.getPlatform() == Platform.WINDOWS) {
                    FirefoxProfile profile = new FirefoxProfile();
                    profile.setPreference("general.useragent.override", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.10) Gecko/20100914 Firefox/3.6.10 (.NET CLR 2.0.40607)");
                    profile.setPreference("app.update.auto", false);

                    if (caps.getCapability("NativeEvents") == null || !((Boolean) caps.getCapability("NativeEvents"))) {
                        profile.setEnableNativeEvents(false);
                    }
                    return new FirefoxDriver(profile);
                } else {
                    FirefoxProfile profile = new FirefoxProfile();
                    if (caps.getCapability("NativeEvents") == null || !((Boolean) caps.getCapability("NativeEvents"))) {
                        profile.setEnableNativeEvents(false);
                    }
                    profile.setPreference("app.update.auto", false);
                    profile.setAssumeUntrustedCertificateIssuer(false);
                    return new FirefoxDriver(profile);
                }
            } else if (caps.getBrowserName().equals(DesiredCapabilities.internetExplorer().getBrowserName())) {
                return new InternetExplorerDriver();
            } else if (caps.getBrowserName().equals(DesiredCapabilities.chrome().getBrowserName())) {
                return new ChromeDriver();
            } else if (caps.getBrowserName().equals(DesiredCapabilities.phantomjs().getBrowserName())) {
                DesiredCapabilities phantomcaps = new DesiredCapabilities();
                phantomcaps.merge(caps);
                phantomcaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {"--web-security=no", "--ignore-ssl-errors=yes"});
                PhantomJSDriver driver = new PhantomJSDriver(phantomcaps);
                driver.manage().window().setSize(new Dimension(1920, 1080));
                return driver;
            } else {
                return new FirefoxDriver();
            }
        } else {
            try {
                return new RemoteDriverWithScreenshots(caps);
            } catch (MalformedURLException ex) {
                logger.error("Invalid URL for remote webdriver '" + caps.getCapability(RemoteDriverWithScreenshots.REMOTE_URL) + "': ", ex);
                return null;
            }
        }
    }

    public DefaultWebDriverWrapper(WebDriver driver, OutputFileSupport debugSupport) {
        this.driver = driver;
        screenshot_counter = 0;
        htmlsource_counter = 0;
        this.debugSupport = debugSupport;
    }

    public DefaultWebDriverWrapper(Capabilities caps, OutputFileSupport debugSupport) {
        this(getDriverFromCapabilities(caps), debugSupport);
        driver_capabilities = caps;
        if (driver_capabilities.getBrowserName().equals(DesiredCapabilities.chrome().getBrowserName()) == false) {
            original_browser_window_handle = driver.getWindowHandle();
        }
        setDefaultTimeout(30); // default timeout is 30 seconds
    }

    @Override
    public void setDefaultTimeout(int p_timeout) {
        this.timeout = p_timeout;
    }

    public WebElement getElement(PageElement locator, int p_timeout) {
        WebElement element = null;
        try {
            element = locator.getElement(driver, p_timeout);
        } catch (NoSuchElementException ex) {
            logger.error("Element with name {} and found {} was not found after {} seconds.", new Object[]{
                        locator.getName(), locator.getFindByDescription(), p_timeout
                    });
            logger.error("Current page URL: {}", driver.getCurrentUrl());
            logger.error("Current page title: {}", driver.getTitle());
            saveHTMLSource();
            takeScreenShot();
            throw ex;
        }
        return element;
    }

    @Override
    public boolean isEnabled(PageElement locator) {
        return isEnabled(locator, timeout);
    }

    @Override
    public boolean isEnabled(PageElement locator, int p_timeout) {
        logger.debug("Checking whether element is enabled with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
        return getElement(locator, p_timeout).isEnabled();
    }

    @Override
    public void setCheckboxState(PageElement locator, boolean checked) {
        setCheckboxState(locator, checked, timeout);
    }

    @Override
    public void setCheckboxState(PageElement locator, boolean checked, int p_timeout) {
        WebElement element = getElement(locator, p_timeout);
        if (checked) {
            logger.debug("setting checkbox element state to true with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
            if (!element.isSelected()) {
                element.click();
            }
        } else {
            logger.debug("setting checkbox element state to false with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
            if (element.isSelected()) {
                element.click();
            }
        }
    }

    @Override
    public void click(PageElement locator) {
        click(locator, timeout);
    }

    @Override
    public void click(PageElement locator, int p_timeout) {
        logger.debug("Clicking on element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
        //waitForVisible(locator);

        for(int tries = 0; tries < 3; tries++) {
            try {
                getElement(locator, p_timeout).click();
                break;
            } catch (StaleElementReferenceException e) {
                logger.warn("Got a stale element exception trying to click, retrying.", e);
            }
        }
    }

    @Override
    public void doubleClick(PageElement locator) {
        doubleClick(locator, timeout);
    }

    @Override
    public void doubleClick(PageElement locator, int p_timeout) {
        WebElement element = getElement(locator, p_timeout);
        logger.debug("Double clicking element '{}' located by '{}'.", locator.getName(), locator.getFindByDescription());
        /*
         Actions builder = new Actions(driver);
         Action dblclick = builder.doubleClick(element).build();
         dblclick.perform();
         *
         */
        try {
            WebElement realElement = element;
            if (InFrameWebElement.class.isAssignableFrom(element.getClass())) // if we're in a frame
            {
                ((InFrameWebElement) element).beforeOperation();
                realElement = ((InFrameWebElement) element).real;
            }
            Actions builder = new Actions(driver);
            Action dblclick = builder.doubleClick(realElement).build();
            dblclick.perform();
            //hoping we're firefox, because this won't work on IE
            // no reliable way to tell the difference if running remote
/*
             ((JavascriptExecutor) getDriver()).executeScript("var evt = document.createEvent('MouseEvents');"
             + "evt.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
             + "arguments[0].dispatchEvent(evt);", realElement);
             * 
             */
        } finally {
            if (InFrameWebElement.class.isAssignableFrom(element.getClass())) // if we're in a frame
            {
                ((InFrameWebElement) element).afterOperation();
            }
        }
    }

    @Override
    public void clear(PageElement locator) {
        clear(locator, timeout);
    }

    @Override
    public void clear(PageElement locator, int p_timeout) {
        logger.debug("Clearing the text from element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
        getElement(locator, p_timeout).clear();
    }

    @Override
    public void submit(PageElement locator) {
        submit(locator, timeout);
    }

    @Override
    public void submit(PageElement locator, int p_timeout) {
        logger.debug("Submitting an element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
        getElement(locator, p_timeout).submit();
    }

    @Override
    public void type(PageElement locator, String text, int p_timeout, boolean should_log) {
        clear(locator, p_timeout);
        if (should_log == true) {
            logger.debug("Typing text '{}' in element with name '{}' and found '{}'.", new Object[]{
                        text, locator.getName(), locator.getFindByDescription()
                    });
        }
        getElement(locator, p_timeout).sendKeys(text);
    }

    @Override
    public void type(PageElement locator, String text, int p_timeout) {
        type(locator, text, p_timeout, true);
    }

    @Override
    public void type(PageElement locator, String text, boolean should_log) {
        type(locator, text, timeout, should_log);
    }

    @Override
    public void type(PageElement locator, String text) {
        type(locator, text, timeout, true);
    }

    @Override
    public String getText(PageElement locator) {
        logger.debug("Getting text from element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
        return getText(locator, timeout);
    }

    @Override
    public String getText(PageElement locator, int p_timeout) {
        return getElement(locator, p_timeout).getText();
    }

    @Override
    public void setSelected(PageElement locator, int p_timeout) {
        logger.debug("Setting selected element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
        WebElement element = getElement(locator, p_timeout);
        if (!element.isSelected()) {
            element.click();
        }
    }

    @Override
    public void setSelected(PageElement locator) {
        setSelected(locator, timeout);
    }

    @Override
    public boolean isSelected(PageElement locator, int p_timeout) {
        logger.debug("Checking if is selected element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
        return getElement(locator, p_timeout).isSelected();
    }

    @Override
    public boolean isSelected(PageElement locator) {
        return isSelected(locator, timeout);
    }

    @Override
    public String getAttribute(PageElement locator, String attribute) {
        return getAttribute(locator, timeout, attribute);
    }

    @Override
    public String getAttribute(PageElement locator, int p_timeout, String attribute) {
        logger.debug("Getting attribute '" + attribute + "' from element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
        return getElement(locator, p_timeout).getAttribute(attribute);
    }

    @Override
    public String getPageTitle() {
        return getPageTitle(true);
    }

    @Override
    public String getPageTitle(boolean should_log) {
        if (should_log == true) {
            logger.debug("Getting current page title.");
        }
        return driver.getTitle();
    }

    @Override
    public String getPageSource() {
        return getPageSource(true);
    }

    @Override
    public String getPageSource(boolean should_log) {
        if (should_log == true) {
            logger.debug("Getting current page html source.");
        }
        return driver.getPageSource();
    }

    @Override
    public String getPageUrl() {
        return getPageUrl(true);
    }

    @Override
    public String getPageUrl(boolean should_log) {
        if (should_log == true) {
            logger.debug("Getting current page url.");
        }
        return driver.getCurrentUrl();
    }

    @Override
    public void goTo(String url) {
        logger.debug("Going to page '{}'.", url);
        driver.get(url);
    }

    @Override
    public void goBack() {
        logger.debug("Going back in the browser.");
        driver.navigate().back();
    }

    @Override
    public void goForward() {
        logger.debug("Going forward in the browser.");
        driver.navigate().forward();
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public void waitFor(Class<? extends SelfAwarePage> page) {
        waitFor(page, timeout);
    }

    @Override
    public void waitFor(Class<? extends SelfAwarePage> page, int p_timeout) {
        logger.debug("Waiting for page '{}' a max of {} seconds.", page.getName(), p_timeout);
        try {
            SelfAwarePage page_instance = page.newInstance();
            Date start_time = new Date();
            Calendar end_time = Calendar.getInstance();
            end_time.add(Calendar.SECOND, p_timeout);
            while (Calendar.getInstance().before(end_time) && !page_instance.isCurrentPage(this)) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                }
            }
            if (!page_instance.isCurrentPage(this)) {
                logger.error("Waited for page '{}' for {} seconds, but still is not here.", page.getName(), p_timeout);
                logger.error("Current page URL: {}", driver.getCurrentUrl());
                logger.error("Current page title: {}", driver.getTitle());
                saveHTMLSource();
                takeScreenShot();
                throw new NoSuchElementException("Couldn't find page '" + page.getName() + "' after " + p_timeout + " seconds.");
            }
            logger.info("Found page '{}' after {} seconds.", page.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
        } catch (InstantiationException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        } catch (IllegalAccessException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        }
    }

    @Override
    public <T> void handlePage(Class<? extends SelfAwarePage<T>> page, T context) throws Exception {
        try {
            SelfAwarePage<T> page_instance = page.newInstance();
            page_instance.handlePage(this, context);
        } catch (InstantiationException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        } catch (IllegalAccessException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        }
    }

    @Override
    public boolean isCurrentPage(Class<? extends SelfAwarePage> page) {
        try {
            SelfAwarePage page_instance = page.newInstance();
            return page_instance.isCurrentPage(this);
        } catch (InstantiationException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        } catch (IllegalAccessException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        }
    }

    @Override
    public void selectByOptionText(PageElement selectList, String option) {
        selectByOptionText(selectList, option, timeout);
    }

    @Override
    public void selectByOptionText(PageElement selectList, String option, int p_timeout) {
        logger.debug("Selecting option with display text '{}' of select list '{}' found by '{}' waiting a max timeout of {} seconds.", new Object[]{option, selectList.getName(), selectList.getFinder(), p_timeout});
        Select selectInput = new Select(getElement(selectList, p_timeout));
        selectInput.selectByVisibleText(option);
    }

    @Override
    public void selectByOptionValue(PageElement selectList, String optionValue) {
        selectByOptionValue(selectList, optionValue, timeout);
    }

    @Override
    public void selectByOptionValue(PageElement selectList, String optionValue, int p_timeout) {
        logger.debug("Selecting option with value '{}' of select list '{}' found by '{}' waiting a max timeout of {} seconds.", new Object[]{optionValue, selectList.getName(), selectList.getFinder(), p_timeout});
        Select selectInput = new Select(getElement(selectList, p_timeout));
        selectInput.selectByValue(optionValue);
    }

    @Override
    public void waitFor(PageElement element) {
        waitFor(element, timeout);
    }

    @Override
    public void waitFor(PageElement element, int p_timeout) {
        logger.debug("Waiting for element '{}' a max of {} seconds.", element.getName(), p_timeout);
        Date start_time = new Date();
        getElement(element, p_timeout);
        logger.info("Found element '{}' after {} seconds.", element.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
    }

    @Override
    public void waitForVisible(PageElement element) {
        waitForVisible(element, timeout);
    }

    @Override
    public void waitForVisible(PageElement element, int p_timeout) {
        logger.debug("Waiting a max of {} seconds for element '{}' found by {} to become visible.", new Object[]{p_timeout, element.getName(), element.getFindByDescription()});
        Calendar end_time = Calendar.getInstance();
        Date start_time = end_time.getTime();
        end_time.add(Calendar.SECOND, p_timeout);
        WebElement wdelement = getElement(element, p_timeout);
        logger.debug("Found element '{}' after {} seconds, waiting for it to become visible.", element.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
        while (!wdelement.isDisplayed() && (Calendar.getInstance().before(end_time))) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.debug("Caught interrupted exception, while waiting for element, but it shouldn't cause too much trouble: {}", e.getMessage());
            }
        }
        if (!wdelement.isDisplayed()) {
            throw new ElementNotVisibleException(MessageFormatter.format("Waited {} seconds for element {} found by {} to become visible, and it never happened.", new Object[]{p_timeout, element.getName(), element.getFindByDescription()}).getMessage());
        }
        logger.debug("Element '{}' was found visisble after {} seconds.", element.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
    }

    @Override
    public boolean exists(PageElement element) {
        return exists(element, true);
    }

    @Override
    public boolean exists(PageElement element, boolean should_log) {
        if (should_log == true) {
            logger.debug("Checking for existence of element '{}'.", element.getName());
        }
        return element.exists(driver, 0);
    }

    @Override
    public boolean exists(Class<? extends SelfAwarePage> page) {
        logger.debug("Checking for existence of page '{}'.", page.getName());

        try {
            SelfAwarePage page_instance = page.newInstance();
            if (!page_instance.isCurrentPage(this)) {
                logger.debug("Checked for page '{}' , but it does not exist.", page.getName());
                return false;
            }
            logger.info("Found page '{}'.", page.getName());
            return true;
        } catch (InstantiationException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        } catch (IllegalAccessException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        }
    }

    @Override
    public String getWindowHandle() {
        logger.debug("Getting current browser window handle");
        return driver.getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles() {
        logger.debug("Getting all browser window handles");
        return driver.getWindowHandles();
    }

    @Override
    public void switchToWindowByHandle(String windowHandle) {
        logger.debug("Switching to the window with handle '{}'.", windowHandle);
        driver.switchTo().window(windowHandle);
    }

    @Override
    public void switchToWindowByURLContains(String partialWindowURL) {
        switchToWindowByURLContains(partialWindowURL, timeout);
    }

    @Override
    public void switchToWindowByURLContains(String partialWindowURL, int p_timeout) {
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.SECOND, p_timeout);
        String switchToHandle = "";
        logger.debug("Looking for the window with the URL containing '{}'.", partialWindowURL);

        while (true) {
            if (Calendar.getInstance().after(endTime)) {
                logger.error("Unable to find window with URL containing '{}'.", partialWindowURL);
                if (original_browser_window_handle.equals("") == false) {
                    logger.error("Switching back to the original window: " + original_browser_window_handle);
                    switchToWindowByHandle(original_browser_window_handle);
                } else {
                    logger.error("original_browser_window_handle was not set, cannot switch back to the original browser window.  Reopening browser instead.");
                    reopen();
                }
                throw new NoSuchWindowException("Timed out waiting for a known page");
            }
            for (String possibleHandle : getWindowHandles()) {
                switchToWindowByHandle(possibleHandle);
                logger.info("Current browser URL: " + getPageUrl(false).toLowerCase());

                if (getPageUrl(false).toLowerCase().contains(partialWindowURL.toLowerCase()) == true) {
                    switchToHandle = possibleHandle;
                } else {
                    try {
                        java.lang.Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
            if (switchToHandle.isEmpty() == false) {
                logger.debug("Found the window with the URL containing '{}', switching to it now.", partialWindowURL);
                switchToWindowByHandle(switchToHandle);
                break;
            }
        }
    }

    @Override
    public void switchToWindowByURL(String windowURL) {
        switchToWindowByURL(windowURL, timeout);
    }

    @Override
    public void switchToWindowByURL(String windowURL, int p_timeout) {
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.SECOND, p_timeout);
        String switchToHandle = "";
        logger.debug("Looking for the window with the URL matching '{}'.", windowURL);

        while (true) {
            if (Calendar.getInstance().after(endTime)) {
                logger.error("Unable to find window with URL matching '{}'.", windowURL);
                if (original_browser_window_handle.equals("") == false) {
                    logger.error("Switching back to the original window: " + original_browser_window_handle);
                    switchToWindowByHandle(original_browser_window_handle);
                } else {
                    logger.error("original_browser_window_handle was not set, cannot switch back to the original browser window.  Reopening browser instead.");
                    reopen();
                }
                throw new NoSuchWindowException("Timed out waiting for a known page");
            }
            for (String possibleHandle : getWindowHandles()) {
                switchToWindowByHandle(possibleHandle);
                logger.info("Current browser URL: " + getPageUrl(false).toLowerCase());

                if (getPageUrl(false).toLowerCase().equals(windowURL.toLowerCase()) == true) {
                    switchToHandle = possibleHandle;
                } else {
                    try {
                        java.lang.Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
            if (switchToHandle.isEmpty() == false) {
                logger.debug("Found the window with the URL matching '{}', switching to it now.", windowURL);
                switchToWindowByHandle(switchToHandle);
                break;
            }
        }
    }

    @Override
    public void closeWindow() {
        logger.debug("Closing current browser window");
        driver.close();
    }

    @Override
    public void closeWindow(String windowHandle) {
        logger.debug("Closing the the window with handle '{}'.", windowHandle);
        switchToWindowByHandle(windowHandle);
        closeWindow();
    }

    @Override
    public boolean isVisible(PageElement locator) {
        return isVisible(locator, true);
    }

    @Override
    public boolean isVisible(PageElement locator, boolean should_log) {
        boolean elementVisible = true;

        if (should_log == true) {
            logger.debug("Checking visibility on element with name '{}' and found '{}'.", locator.getName(), locator.getFindByDescription());
        }
        if (exists(locator) == true) {
            WebElement wdelement = getElement(locator, timeout);
            elementVisible = wdelement.isDisplayed();

            if (elementVisible) {
                logger.debug("Found visible element with name '{}' and found '{}'", locator.getName(), locator.getFindByDescription());
            } else {
                logger.debug("Element was NOT VISIBLE with name '{}' and found '{}'", locator.getName(), locator.getFindByDescription());
            }
        } else {
            elementVisible = false;
        }
        return elementVisible;
    }

    @Override
    public void takeScreenShot() {
        takeScreenShot("screenshot");
    }

    @Override
    public void takeScreenShot(String name) {
        if (TakesScreenshot.class.isAssignableFrom(driver.getClass())) {
            if (name == null) {
                name = "screenshot";
            }
            if (!name.toLowerCase().endsWith(".png")) {
                name = name + ".png";
            }
            name = ++screenshot_counter + "-" + name;
            File ss_file = debugSupport.getOutputFile(name);
            logger.debug("Taking screenshot, output file will be {}", ss_file.getAbsolutePath());
            File ss_tmp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            try {
                FileUtils.copyFile(ss_tmp, ss_file);
            } catch (IOException ex) {
                logger.error("Unable to copy screenshot from '" + ss_tmp.getAbsolutePath() + "' to '" + ss_file.getAbsolutePath() + "': ", ex);
            }
        } else {
            logger.warn("Requested screenshot by name '{}', but browser doesn't support taking screenshots.", name);
        }

    }

    @Override
    public void saveHTMLSource() {
        saveHTMLSource("pagesource");
    }

    @Override
    public void saveHTMLSource(String name) {
        if (name == null) {
            name = "pagesource";
        }
        if (!name.toLowerCase().endsWith(".html")) {
            name = name + ".html";
        }
        name = ++htmlsource_counter + "-" + name;
        File src_file = debugSupport.getOutputFile(name);
        logger.debug("Saving current page HTML source, output file will be {}", src_file.getAbsolutePath());

        try {
            FileUtils.writeStringToFile(src_file, getPageSource());
        } catch (IOException ex) {
            logger.error("Unable to save the current page HTML source to the file '" + src_file.getAbsolutePath() + "': ", ex);
        }

    }

    @Override
    public void reopen() {
        try {
            driver.quit();
        } catch (WebDriverException wde) {
            logger.error("Caught an Exception when trying to quit the driver in reopen()", wde);
        }
        driver = getDriverFromCapabilities(driver_capabilities);

        if (driver_capabilities.getBrowserName().equals(DesiredCapabilities.chrome().getBrowserName()) == false) {
            original_browser_window_handle = driver.getWindowHandle();
        }
    }

    @Override
    public void closeAllWindowsExceptOriginal() {
        if (original_browser_window_handle.equals("") == false) {
            logger.info("Closing all browser windows except: " + original_browser_window_handle);

            for (String windowHandle : getWindowHandles()) {
                if (windowHandle.equals(original_browser_window_handle) == false) {
                    closeWindow(windowHandle);
                }
            }
            switchToWindowByHandle(original_browser_window_handle);
        } else {
            logger.info("original_browser_window_handle is not set, no other windows to close");
        }
    }

    @Override
    public void logToSessionFile(String filename, String logString) {
        String sessionOutputFileName = debugSupport.getSessionOutputFile(filename).getAbsolutePath();
        logger.info("Writing to session file, session file will be " + sessionOutputFileName);

        try {
            OutputStream os = debugSupport.getSessionOutputStream(filename);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(logString);
            bw.newLine();
            bw.close();
        } catch (IOException ex) {
            logger.error("Unable to write to the session file '" + sessionOutputFileName + "': ", ex);
        } catch (NotFoundException ex) {
            logger.error("Unable to write to the session file '" + sessionOutputFileName + "': ", ex);
        }
    }

    @Override
    public String getFirstSelectedOptionText(PageElement selectList) {
        return getFirstSelectOptionText(selectList, timeout);
    }

    @Override
    public String getFirstSelectOptionText(PageElement selectList, int p_timeout) {
        logger.debug("Getting first selected option as text from of select list '{}' found by '{}' waiting a max timeout of {} seconds.", new Object[]{selectList.getName(), selectList.getFinder(), p_timeout});
        Select selectInput = new Select(getElement(selectList, p_timeout));
        return selectInput.getFirstSelectedOption().getText();
    }

    @Override
    public void hover(PageElement locator) {
        hover(locator, timeout);
    }

    @Override
    public void hover(PageElement locator, int p_timeout) {
        WebElement element = getElement(locator, p_timeout);
        logger.debug("Hovering mouse over element '{}' located by '{}'.", locator.getName(), locator.getFindByDescription());
        Actions builder = new Actions(driver);
        Action hover = builder.moveToElement(element, 2, 2).build();
        hover.perform();
    }

    @Override
    public void waitForNotVisible(PageElement element) {
        waitForNotVisible(element, timeout);
    }

    @Override
    public void waitForNotVisible(PageElement element, int p_timeout) {
        logger.debug("Waiting a max of {} seconds for element '{}' found by {} to become invisible.", new Object[]{p_timeout, element.getName(), element.getFindByDescription()});

        Calendar end_time = Calendar.getInstance();
        Date start_time = end_time.getTime();
        end_time.add(Calendar.SECOND, p_timeout);
        WebElement wdelement = getElement(element, p_timeout);
        logger.debug("Found element '{}' after {} seconds, waiting for it to become invisible.", element.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);

        while (Calendar.getInstance().before(end_time)) {
            try {
                if (!wdelement.isDisplayed()) {
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    logger.debug("Caught interrupted exception, while waiting for element, but it shouldn't cause too much trouble: {}", e.getMessage());
                }
            } catch (StaleElementReferenceException e) {
            }
        }
        if (wdelement.isDisplayed()) {
            throw new ElementNotVisibleException(MessageFormatter.format("Waited {} seconds for element {} found by {} to become invisible, and it never happened.", new Object[]{p_timeout, element.getName(), element.getFindByDescription()}).getMessage());
        }

        logger.debug("Element '{}' was not found visible after {} seconds.", element.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
    }

    @Override
    public void waitForDoesNotExist(PageElement element) {
        waitForDoesNotExist(element, timeout);
    }

    @Override
    public void waitForDoesNotExist(PageElement element, int p_timeout) {
        logger.debug("Waiting a max of {} seconds for element '{}' found by {} to no longer exist.", new Object[]{p_timeout, element.getName(), element.getFindByDescription()});

        Calendar end_time = Calendar.getInstance();
        Date start_time = end_time.getTime();
        end_time.add(Calendar.SECOND, p_timeout);
        logger.debug("Waiting for element '{}' to no longer exist.", element.getName());

        while (element.exists(driver, 0) && (Calendar.getInstance().before(end_time))) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.debug("Caught interrupted exception, while waiting for element, but it shouldn't cause too much trouble: {}", e.getMessage());
            }
        }
        if (element.exists(driver, 1)) {
            throw new NoSuchElementException(MessageFormatter.format("Waited {} seconds for element {} found by {} to no longer exist, and it never happened.", new Object[]{p_timeout, element.getName(), element.getFindByDescription()}).getMessage());
        }

        logger.debug("Element '{}' no longer exists after {} seconds.", element.getName(), ((new Date()).getTime() - start_time.getTime()) / 1000);
    }

    @Override
    public void waitForTextNotEmpty(PageElement element) {
        waitForTextNotEmpty(element, this.timeout);
    }

    @Override
    public void waitForTextNotEmpty(PageElement element, int p_timeout) {
        logger.debug("Waiting a maximum of {} seconds for element '{}' found by {} to exist and for it's text to be not empty.", new Object[]{p_timeout, element.getName(), element.getFindByDescription()});
        Date beginning = new Date();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.SECOND, p_timeout);
        do {
            String elementText = getText(element, p_timeout);
            if (elementText != null && !elementText.isEmpty()) {
                logger.info("Found element '{}' with text '{}' after {} seconds.", new Object[]{element.getName(), elementText, (((new Date()).getTime() - beginning.getTime()) / 1000)});
                return;
            }
        } while (Calendar.getInstance().before(endTime));
        logger.error("Waited {} seconds for the text of element '{}' found by {} to not be empty.", new Object[]{(((new Date()).getTime() - beginning.getTime()) / 1000), element.getName(), element.getFindByDescription()});
        throw new TimeoutError("Timeout of " + p_timeout + " seconds exceeded while waiting for element '" + element.getName() + "' to provide non empty text.");
    }

    @Override
    public <T extends PageWithActions> T on(Class<T> page) {
        logger.debug("Creating instance of page '{}'.", page.getName());

        try {
            PageWithActions page_instance = page.newInstance();
            page_instance.initializePage(this);
            logger.info("Returning instance of page '{}'.", page.getName());
            return (T) page_instance;
        } catch (InstantiationException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        } catch (IllegalAccessException ex) {
            logger.error("Unable to create instance of page class " + page.getName() + ".", ex);
            throw new IllegalStateException("Unable to create instance of page class " + page.getName() + ".", ex);
        }
    }
}
