package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.TCRunContext;

/**
 * A shutdown plugin is called after everthing else executes, and is meant for cleanup tasks.
 *
 * @author jcorbett
 */
public interface ShutdownTaskPlugin extends Plugin
{
	/**
	 * Override this method to get called on shutdown.
	 * @param p_context TCRun Context containing all information about the run and it's configuration and results.
	 */
	public void onShutdown(TCRunContext p_context);
}
