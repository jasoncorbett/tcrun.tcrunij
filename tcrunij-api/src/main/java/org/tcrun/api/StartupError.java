package org.tcrun.api;

/**
 * An exception that can be thrown by a startup task.  It should contain a end user descriptive reason for not allowing
 * tcrunij to run.
 *
 * @author jcorbett
 */
public class StartupError extends Exception
{
	public StartupError(String reason)
	{
		super(reason);
	}
}
