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

	private void setBrowserObject() throws Exception
	{
        if (tcinfo.containsKey("remote"))
        {
			String remoteUrl = "http://" + configValue("remote") + ":4444/wd/hub";
            browser = new DefaultWebDriverWrapper(CapabilitiesFactory.getCapabilitiesFor(configValue("browser", "headless"), remoteUrl));
        } else
        {
            browser = new DefaultWebDriverWrapper(CapabilitiesFactory.getCapabilitiesFor(configValue("browser", "headless")));
        }
        browser.setDefaultTimeout(Integer.parseInt(configValue("defaults.timeout", "30")));
	}

    @Override
    public void frameworkSetup() throws Exception
    {
        super.frameworkSetup();
        if (configValue("browser.persistent", "true").equalsIgnoreCase("true"))
        {
            if (PersistentBrowserPlugin.persistentBrowser == null)
            {
				setBrowserObject();
                PersistentBrowserPlugin.persistentBrowser = browser;
            } else
            {
                browser = PersistentBrowserPlugin.persistentBrowser;
            }
        } else
        {
			setBrowserObject();
        }
    }

	@Override
	public void setupDebugShell(Global global)
	{
		super.setupDebugShell(global);
		global.defineProperty("browser", browser, ScriptableObject.DONTENUM);
	}

        @Override
        public void frameworkCleanup() throws Exception
        {
            super.frameworkCleanup();
            if (browser != null && configValue("browser.persistent", "true").equalsIgnoreCase("false"))
            {
                browser.getDriver().quit();
            }
        }
        /**
         * Closes the browser and reopens
         */
        public void reopenBrowser() throws Exception
        {
            browser.getDriver().quit();
            if(configValue("browser.persistent", "true").equalsIgnoreCase("true"))
            {
                PersistentBrowserPlugin.persistentBrowser = null;
            }
            this.frameworkSetup();
        }
}
