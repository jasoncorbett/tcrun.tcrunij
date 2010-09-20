package org.tcrun.tcapi.assertlib;

/**
 *
 * @author jcorbett
 */
public class IsNotMatchBuilder extends IsMatchBuilder
{
	@Override
	protected boolean isNegativeMatch()
	{
		return true;
	}
}
