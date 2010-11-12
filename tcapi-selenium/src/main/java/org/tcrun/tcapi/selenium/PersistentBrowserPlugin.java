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

/**
 *
 * @author leeard
 */
@ImplementsPlugin(ShutdownTaskPlugin.class)
public class PersistentBrowserPlugin implements ShutdownTaskPlugin
{

    public static WebDriverWrapper persistentBrowser = null;
    public static WebDriverWrapper nonPersistentBrowser = null;
    private static XLogger logger = XLoggerFactory.getXLogger(PersistentBrowserPlugin.class);

    @Override
    public void onShutdown(TCRunContext p_context)
    {

        if (persistentBrowser != null)
        {
            logger.debug("closing persistent browser");
            persistentBrowser.getDriver().close();
        }
        if (nonPersistentBrowser != null)
        {
            logger.debug("closing the final non-persistent browser window");
            nonPersistentBrowser.getDriver().close();
        }
    }

    @Override
    public String getPluginName()
    {
        return "Selenium Persistent Browser Plugin";
    }
}
