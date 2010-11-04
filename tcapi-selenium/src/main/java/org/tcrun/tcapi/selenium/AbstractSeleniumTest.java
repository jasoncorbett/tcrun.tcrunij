package org.tcrun.tcapi.selenium;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
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
                if (tcinfo.containsKey("remote"))
                {
                    browser = new DefaultWebDriverWrapper(DefaultWebDriverWrapper.getDriverFromBrowserName(configValue("browser", "headless"), configValue("remote")));
                } else
                {
                    browser = new DefaultWebDriverWrapper(configValue("browser", "headless"));
                }
                browser.setDefaultTimeout(30);
                PersistentBrowserPlugin.persistentBrowser = browser;
            } else
            {
                browser = PersistentBrowserPlugin.persistentBrowser;
            }
        } else
        {
            if (tcinfo.containsKey("remote"))
            {
                browser = new DefaultWebDriverWrapper(DefaultWebDriverWrapper.getDriverFromBrowserName(configValue("browser", "headless"), configValue("remote")));
            } else
            {
                browser = new DefaultWebDriverWrapper(configValue("browser", "headless"));
            }
            browser.setDefaultTimeout(30);
        }
    }

	@Override
	public void setupDebugShell(Global global)
	{
		super.setupDebugShell(global);
		global.defineProperty("browser", browser, ScriptableObject.DONTENUM);
	}
}
