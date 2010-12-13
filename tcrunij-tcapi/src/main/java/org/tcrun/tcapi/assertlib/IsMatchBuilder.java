/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.assertlib;

import java.util.Collection;
import java.util.regex.Pattern;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.hamcrest.core.StringEndsWith;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsSame;
import org.hamcrest.text.IsEmptyString;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.hamcrest.text.IsEqualIgnoringWhiteSpace;
import org.hamcrest.text.StringContainsInOrder;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.core.Is;
/**
 * Static factory for matchers.
 * @author jcorbett
 */
public class IsMatchBuilder
{
	public static final IsNotMatchBuilder Not = new IsNotMatchBuilder();

	protected <T> Matcher<T> wrapper(Matcher<T> matcher)
	{
		return new Is<T>(matcher);
	}

	public Matcher<Object> Null()
	{
		return wrapper(new IsNull<Object>());
	}

	public Matcher<Boolean> True()
	{
		return wrapper(new IsEqual<Boolean>(Boolean.TRUE));
	}

	public Matcher<Boolean> False()
	{
		return wrapper(new IsEqual<Boolean>(Boolean.FALSE));
	}

	public Matcher<String> EmptyString()
	{
		return wrapper(new IsEmptyString());
	}

	public Matcher<Collection<Object>> EmptyCollection()
	{
		return wrapper(new IsEmptyCollection<Object>());
	}

	public Matcher<Iterable<Object>> EmptyIterable()
	{
		return wrapper(new IsEmptyIterable<Object>());
	}

	public Matcher<String> EqualToIgnoringCase(String expected)
	{
		return wrapper(new IsEqualIgnoringCase(expected));
	}

	public Matcher<String> EqualToIgnoringWhitespace(String expected)
	{
		return wrapper(new IsEqualIgnoringWhiteSpace(expected));
	}

	public Matcher<String> StringContainingAllSubstringsInOrder(Iterable<String> expected)
	{
		return wrapper(new StringContainsInOrder(expected));
	}

	public Matcher<String> StringContaining(String expected)
	{
		return wrapper(new StringContains(expected));
	}

	public Matcher<String> StringStartingWith(String expected)
	{
		return wrapper(new StringStartsWith(expected));
	}

	public Matcher<String> StringEndingWith(String expected)
	{
		return wrapper(new StringEndsWith(expected));
	}

	public Matcher<String> StringMatchingRegex(String expected)
	{
		return wrapper(new RegularExpressionMatcher(expected));
	}
	public Matcher<String> StringMatchingRegex(Pattern expected)
	{
		return wrapper(new RegularExpressionMatcher(expected));
	}

	public Matcher<Object> InstanceOf(Class<?> expected)
	{
		return wrapper(new IsInstanceOf(expected));
	}

	public Matcher<Object> EqualTo(Object expected)
	{
		return wrapper(new IsEqual<Object>(expected));
	}

	public Matcher<Object> SameAs(Object expected)
	{
		return wrapper(new IsSame<Object>(expected));
	}
}
