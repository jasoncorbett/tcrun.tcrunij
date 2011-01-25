package org.tcrun.api.annotations;

/**
 *
 * @author jcorbett
 */
public @interface DataDrivenFilter
{
	public Class<? extends DataDrivenConfigurationFilter> value();
}
