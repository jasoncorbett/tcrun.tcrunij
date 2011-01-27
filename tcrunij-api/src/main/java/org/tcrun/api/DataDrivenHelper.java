package org.tcrun.api;

import java.util.List;
import java.util.Map;
import org.tcrun.api.annotations.DataDriven;
import org.tcrun.api.annotations.DataDrivenExclude;
import org.tcrun.api.annotations.DataDrivenFilter;

/**
 * This class can help test loaders handle the data driven annotations in a reliable and easy way.  It provides
 * static methods which do most of the work of determining the custom configurations for data driven tests.
 *
 * @author jcorbett
 */
public class DataDrivenHelper
{
	public static List<Map<String, String>> getCustomConfigurations(DataDriven datadriven, Map<String, String> configuration)
	{
		return getCustomConfigurations(datadriven, configuration, null, null);
	}

	public static List<Map<String, String>> getCustomConfigurations(DataDriven datadriven, Map<String, String> configuration, DataDrivenExclude exclude)
	{
		return getCustomConfigurations(datadriven, configuration, exclude, null);
	}

	public static List<Map<String, String>> getCustomConfigurations(DataDriven datadriven, Map<String, String> configuration, DataDrivenExclude exclude, DataDrivenFilter filter)
	{
		return null;
	}

}
