/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi;

/**
 *
 * @author jcorbett
 */
public class BrokenTestError extends Exception
{
	public BrokenTestError(String message)
	{
		super(message);
	}
}
