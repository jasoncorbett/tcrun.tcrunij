package org.tcrun.cmd;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.TCRunContext;

/**
 * This class holds the default tcrun context.  This allows for a different implimentation to be plugged in later.
 *
 * @author jcorbett
 */
public class TCRunContextFactory
{
	private static Class<? extends TCRunContext> s_defaultContextClass = DefaultTCRunContext.class;

	private static XLogger logger = XLoggerFactory.getXLogger(TCRunContextFactory.class);

	public static void setContextClass(Class<? extends TCRunContext> contextClass)
	{
		s_defaultContextClass = contextClass;
	}

	public static TCRunContext getNewContext()
	{
		TCRunContext retval = null;
		try
		{
			retval = s_defaultContextClass.newInstance();
		} catch(Exception ex)
		{
			logger.error("Error creating instance of TCRun Context Class '" + s_defaultContextClass + "': ", ex);
		}
		return retval;
	}

}
