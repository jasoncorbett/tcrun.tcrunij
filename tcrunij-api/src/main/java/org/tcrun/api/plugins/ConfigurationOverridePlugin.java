package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.TCRunContext;

/**
 * Configuration override plugins are executed after all configuration source plugins have finished.  They get the
 * chance to "override" what was loaded by configuration source plugins.
 *
 * @author jcorbett
 */
public interface ConfigurationOverridePlugin extends Plugin
{
	public void handleConfiguration(TCRunContext context);
}
