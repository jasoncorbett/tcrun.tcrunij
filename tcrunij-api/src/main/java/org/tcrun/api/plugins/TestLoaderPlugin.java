/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tcrun.api.plugins;

import org.tcrun.api.Plugin;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;

/**
 * A test loader is responsible loading tests one by one.  In particular it needs to create
 * instances of RunnableTest which include operations such as:
 * <ol>
 *   <li>Find tests using whatever method it likes.</li>
 *   <li>Obtain a list of attributes using AttributeProviderPlugins</li>
 *   <li>Create a RunnableTestInstance</li>
 *   <li>Return the RunnableTest instances from an Iterator</li>
 * </ol>
 *
 * The iterator pattern works well here, not forcing the loader to load all the instances at
 * once.
 *
 * @author jcorbett
 */
public interface TestLoaderPlugin extends Iterable<RunnableTest>, Plugin
{
	/**
	 * initialize the loader plugin
	 * @param p_context The context to use in initialization
	 */
	public void initialize(TCRunContext p_context);
}
