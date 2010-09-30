/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.selenium;

/**
 *
 * @author jcorbett
 */
public interface SelfAwarePage<T>
{
	public boolean isCurrentPage(WebDriverWrapper browser);
	public void handlePage(WebDriverWrapper browser, T context);
}
