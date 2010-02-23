/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.tcrun.tcrunij.api;

import java.util.List;

/**
 *
 * @author jcorbett
 */
public interface IPluginManager
{
	public void initialize(IRuntimeInformation runtime_information);

	public <T extends IPlugin> List<T> getPluginsFor(Class<T> clazz);
}
