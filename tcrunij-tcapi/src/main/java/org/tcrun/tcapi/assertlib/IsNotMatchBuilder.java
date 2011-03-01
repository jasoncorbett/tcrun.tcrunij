package org.tcrun.tcapi.assertlib;

import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;

/**
 *
 * @author jcorbett
 */
public class IsNotMatchBuilder extends IsMatchBuilder
{
	@Override
	protected <T> Matcher<T> wrapper(Matcher<T> matcher)
	{
		return new Is<T>(new IsNot<T>(matcher));
	}
}
