/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.assertlib;

/**
 *
 * @author jcorbett
 */
public class AssertionFailure extends Exception
{
	public AssertionFailure(String message)
	{
		super(message);
	}
}
