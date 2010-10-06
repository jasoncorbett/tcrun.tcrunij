package org.tcrun.tcapi.selenium;

import org.tcrun.tcapi.AbstractSimpleTestCase;

/**
 *
 * @author jcorbett
 */
public abstract class AbstractSeleniumTest extends AbstractSimpleTestCase
{

    protected WebDriverWrapper browser;

    @Override
    public void frameworkSetup() throws Exception
    {
        super.frameworkSetup();
        if (configValue("browser.persistent", "true").equalsIgnoreCase("true"))
        {
            if (PersistentBrowserPlugin.persistentBrowser == null)
            {
                browser = new DefaultWebDriverWrapper(configValue("browser", "headless"));
                browser.setDefaultTimeout(30);
                PersistentBrowserPlugin.persistentBrowser = browser;
            } else
            {
                browser = PersistentBrowserPlugin.persistentBrowser;
            }
        } else
        {
            browser = new DefaultWebDriverWrapper(configValue("browser", "headless"));
            browser.setDefaultTimeout(30);
        }
    }
}
