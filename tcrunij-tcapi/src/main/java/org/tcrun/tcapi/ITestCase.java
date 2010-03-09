/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi;

/**
 * This interface is to define what a test case should look like.
 * @author jjones
 */
public interface ITestCase
{
	public void tcSetup();

        public void doTest();

        public void tcCleanUp();
}
