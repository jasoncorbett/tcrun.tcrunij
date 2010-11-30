/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.api;

/**
 * A simple representation of a test step, a name and an expected result.
 *
 * @author jcorbett
 */
public interface TestCaseStep
{

	public String getStepName();
	public String getStepExpectedResult();
}
