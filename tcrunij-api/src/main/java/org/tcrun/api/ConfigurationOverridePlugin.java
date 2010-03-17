package org.tcrun.api;

/**
 * Configuration override plugins are executed after all configuration source plugins have finished.  They get the
 * chance to "override" what was loaded by configuration source plugins.
 *
 * @author jcorbett
 */
public interface ConfigurationOverridePlugin
{
	public void handleConfiguration(TCRunContext context);
}
