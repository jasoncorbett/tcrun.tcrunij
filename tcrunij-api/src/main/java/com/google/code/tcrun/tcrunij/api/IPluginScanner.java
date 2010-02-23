/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.tcrun.tcrunij.api;

/**
 * Plugin scanner.  A class implementing this interface is responsible for
 * locating plugins and then adding them to the plugin manager.  Why not
 * just have the plugin manager do it?  Well because how you locate plugins
 * may be very dependent on what kind of environment you run in.  This
 * functionality needs to be replacable.
 *
 * @author jcorbett
 */
public interface IPluginScanner
{
	public void scan(IPluginManager manager, IRuntimeInformation runtime_info);
}
