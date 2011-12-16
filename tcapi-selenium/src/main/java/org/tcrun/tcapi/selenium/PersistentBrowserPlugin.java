/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.selenium;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.ShutdownTaskPlugin;
import org.tcrun.selenium.WebDriverWrapper;

/**
 *
 * @author leeard
 */
@ImplementsPlugin(ShutdownTaskPlugin.class)
public class PersistentBrowserPlugin implements ShutdownTaskPlugin
{

    public static WebDriverWrapper persistentBrowser = null;
    private static XLogger logger = XLoggerFactory.getXLogger(PersistentBrowserPlugin.class);

    @Override
    public void onShutdown(TCRunContext p_context)
    {

        if (persistentBrowser != null)
        {
            logger.debug("closing persistent browser");
            persistentBrowser.getDriver().close();
        }
    }

    @Override
    public String getPluginName()
    {
        return "Selenium Persistent Browser Plugin";
    }
}
