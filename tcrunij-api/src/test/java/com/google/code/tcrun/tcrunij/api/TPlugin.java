/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.tcrun.tcrunij.api;

/**
 * Why name it TPlugin?  Well because if it's called TestPlugin, maven will try to run it
 * as a test.
 * @author jcorbett
 */
public class TPlugin implements ITestPlugin
{

	public String getPluginName()
	{
		return "Test Plugin";
	}
}
