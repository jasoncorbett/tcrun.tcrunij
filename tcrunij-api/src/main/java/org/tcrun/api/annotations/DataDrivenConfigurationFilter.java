package org.tcrun.api.annotations;

import java.util.Map;

/**
 *
 * @author jcorbett
 */
public interface DataDrivenConfigurationFilter
{
	public enum ConfigurationState
	{
		USE_FOR_DATA_DRIVEN_TEST,
		EXCLUDE_FOR_DATA_DRIVEN_TEST
	};

	public ConfigurationState filterConfiguration(Map<String, String> configuration);
}
