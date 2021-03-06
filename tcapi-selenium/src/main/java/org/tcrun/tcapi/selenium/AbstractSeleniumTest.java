package org.tcrun.tcapi.selenium;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
import org.tcrun.selenium.CapabilitiesFactory;
import org.tcrun.selenium.DefaultWebDriverWrapper;
import org.tcrun.selenium.WebDriverWrapper;
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
            browser = new DefaultWebDriverWrapper(CapabilitiesFactory.getCapabilitiesFor(configValue("browser", "ff"), remoteUrl), new DebugSupport());
        } else
        {
            browser = new DefaultWebDriverWrapper(CapabilitiesFactory.getCapabilitiesFor(configValue("browser", "ff")), new DebugSupport());
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
                browser.getDriver().close();
            }
        }
        /**
         * Closes the browser and reopens
         */
        public void reopenBrowser() throws Exception
        {
            browser.getDriver().close();
            if(configValue("browser.persistent", "true").equalsIgnoreCase("true"))
            {
                PersistentBrowserPlugin.persistentBrowser = null;
            }
            this.frameworkSetup();
        }
		/**
		 * Capture page source and take a screen shot when an exception is thrown.
		 */
		@Override
		public boolean handleException(Exception e)
		{
			try
			{
				tclog.error("Current page URL: {}", browser.getDriver().getCurrentUrl());
				tclog.error("Current page title: {}", browser.getDriver().getTitle());
				tclog.error("Exception thrown:  ", e);
				browser.saveHTMLSource();
				browser.takeScreenShot("Exception_" + e.getClass().getSimpleName());
				if(steps.size() > 0)
				{
					tclog.info("Steps to reproduce:");
					for (int i = 0; i < steps.size(); i++)
					{
						tclog.info("Step {}: {}", i + 1, steps.get(i).getStepName());
					}
				}
			}
			catch (RuntimeException rte)
			{
				tclog.error("Caught a RuntimeException in handleException, need to reopen the browser", rte);
				browser.reopen();
			}
			catch (Exception ex)
			{
				tclog.error("Caught an Exception in handleException, need to reopen the browser", ex);
				browser.reopen();
			}
			return false;
		}
}

