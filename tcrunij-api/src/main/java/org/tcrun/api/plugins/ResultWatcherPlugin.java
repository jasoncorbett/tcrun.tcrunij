/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.Result;

/**
 * A plugin registered as a ResultWatcherPlugin will get called every time a result is added to the result list.
 *
 * @author jcorbett
 */
public interface ResultWatcherPlugin extends Plugin
{
	/**
	 * This method is called every time a result is added to the TCRunContext.
	 *
	 * @param result The new result to add to the list.
	 */
	public void onResultFiled(Result result);
}
