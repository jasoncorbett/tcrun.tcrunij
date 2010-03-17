package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.TCRunContext;

/**
 * A startup task is a generic task that get's executed on startup, and can stop the tcrunij from running any tests.
 * These plugins get's run after the command line is parsed and configuration is loaded.
 *
 * @author jcorbett
 */
public interface StartupTaskPlugin extends Plugin
{
	public void onStartup(TCRunContext context) throws StartupError;
}
