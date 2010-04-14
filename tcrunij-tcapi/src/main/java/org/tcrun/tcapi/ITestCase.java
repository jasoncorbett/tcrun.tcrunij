/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi;

import java.util.Map;

/**
 * This interface is to define what a test case should look like.
 * @author jjones
 */
public interface ITestCase
{
	public void tcSetup(Map<String, String> configuration) throws Exception;

        public void doTest() throws Exception;

        public void tcCleanUp() throws Exception;

	public void handleException(Exception e);
}
