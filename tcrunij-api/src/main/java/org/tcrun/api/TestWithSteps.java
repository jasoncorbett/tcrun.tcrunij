/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.api;

import java.util.List;

/**
 * Any test case that has a list of "steps" included should implement this interface.  This will allow plugins to
 * properly output and use step information.
 *
 * @author jcorbett
 */
public interface TestWithSteps
{
	List<TestCaseStep> getTestSteps();
}
