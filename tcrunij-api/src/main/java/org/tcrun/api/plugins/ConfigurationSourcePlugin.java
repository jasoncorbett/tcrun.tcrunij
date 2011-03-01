package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.TCRunContext;

/**
 * A plugin that is responsible for loading configuration into the context.  This could be test case configuration
 * data, or TCRunIJ configuration data.  Doesn't matter.  This is one of the first plugins to execute (after
 * command line option plugins).
 *
 * @author jcorbett
 */
public interface ConfigurationSourcePlugin extends Plugin
{
	public void loadConfiguration(TCRunContext context);
}
