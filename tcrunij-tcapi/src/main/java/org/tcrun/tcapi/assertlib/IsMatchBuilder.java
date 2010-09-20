/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.assertlib;

import java.util.Collection;
import java.util.regex.Pattern;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.hamcrest.core.StringEndsWith;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.text.IsEmptyString;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.hamcrest.text.IsEqualIgnoringWhiteSpace;
import org.hamcrest.text.StringContainsInOrder;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsEmptyIterable;
/**
 * Static factory for matchers.
 * @author jcorbett
 */
public class IsMatchBuilder
{
	public static final IsNotMatchBuilder Not = new IsNotMatchBuilder();

	protected boolean isNegativeMatch()
	{
		return false;
	}

	public Matcher<Object> Null()
	{
		if(isNegativeMatch())
			return new IsNot<Object>(new IsNull<Object>());
		else
			return new IsNull<Object>();
	}

	public Matcher<Boolean> True()
	{
		if(isNegativeMatch())
			return new IsNot<Boolean>(new IsEqual<Boolean>(Boolean.TRUE));
		else
			return new IsEqual<Boolean>(Boolean.TRUE);
	}


	public Matcher<Boolean> False()
	{
		if(isNegativeMatch())
			return new IsNot<Boolean>(new IsEqual<Boolean>(Boolean.FALSE));
		else
			return new IsEqual<Boolean>(Boolean.FALSE);
	}

	public Matcher<String> EmptyString()
	{
		if(isNegativeMatch())
			return new IsNot<String>(new IsEmptyString());
		else
			return new IsEmptyString();
	}

	public Matcher<Collection<Object>> EmptyCollection()
	{
		if(isNegativeMatch())
			return new IsNot<Collection<Object>>(new IsEmptyCollection<Object>());
		else
			return new IsEmptyCollection<Object>();
	}

	public Matcher<Iterable<Object>> EmptyIterable()
	{
		if(isNegativeMatch())
			return new IsNot<Iterable<Object>>(new IsEmptyIterable<Object>());
		else
			return new IsEmptyIterable<Object>();
	}

	public Matcher<String> StringEqualsIgnoreingCase(String expected)
	{
		if(isNegativeMatch())
			return new IsNot<String>(new IsEqualIgnoringCase(expected));
		else
			return new IsEqualIgnoringCase(expected);
	}

	public Matcher<String> StringEqualsIgnoringWhitespace(String expected)
	{
		if(isNegativeMatch())
			return new IsNot<String>(new IsEqualIgnoringWhiteSpace(expected));
		else
			return new IsEqualIgnoringWhiteSpace(expected);
	}

	public Matcher<String> StringContainingAllSubstringsInOrder(Iterable<String> expected)
	{
		if(isNegativeMatch())
			return new IsNot<String>(new StringContainsInOrder(expected));
		else
			return new StringContainsInOrder(expected);
	}

	public Matcher<String> StringContaining(String expected)
	{
		if(isNegativeMatch())
			return new IsNot<String>(new StringContains(expected));
		else
			return new StringContains(expected);
	}

	public Matcher<String> StringStartingWith(String expected)
	{
		if(isNegativeMatch())
			return new IsNot<String>(new StringStartsWith(expected));
		else
			return new StringStartsWith(expected);
	}

	public Matcher<String> StringEndingWith(String expected)
	{
		if(isNegativeMatch())
			return new IsNot<String>(new StringEndsWith(expected));
		else
			return new StringEndsWith(expected);
	}

	public Matcher<String> StringMatchingRegex(String expected)
	{
		if(isNegativeMatch())
			return new IsNot<String>(new RegularExpressionMatcher(expected));
		else
			return new RegularExpressionMatcher(expected);
	}
	public Matcher<String> StringMatchingRegex(Pattern expected)
	{
		if(isNegativeMatch())
			return new IsNot<String>(new RegularExpressionMatcher(expected));
		else
			return new RegularExpressionMatcher(expected);
	}

	public Matcher<Object> InstanceOf(Class<?> expected)
	{
		if(isNegativeMatch())
			return new IsNot<Object>(new IsInstanceOf(expected));
		else
			return new IsInstanceOf(expected);
	}
}
