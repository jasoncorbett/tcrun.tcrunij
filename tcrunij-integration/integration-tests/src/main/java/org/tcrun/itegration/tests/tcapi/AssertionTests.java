/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.itegration.tests.tcapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.tcrun.api.annotations.TestGroup;
import org.tcrun.api.annotations.TestName;
import org.tcrun.tcapi.AbstractSimpleTestCase;
import org.tcrun.tcapi.TestResult;
import org.tcrun.tcapi.assertlib.AssertionFailure;
import static org.tcrun.tcapi.assertlib.MatchBuilder.*;

/**
 *
 * @author jcorbett
 */
@TestName("TCRunIJ: Assertion Library Test")
@TestGroup({"tcrunij","basic"})
public class AssertionTests extends AbstractSimpleTestCase
{
	private UUID uuid = UUID.fromString("819e424f-d7b2-4a66-86f4-c9ba444bb696");

	@Override
	public TestResult test() throws Exception
	{
		TestResult retval = TestResult.PASS;

		step("Checking Is.Null", "No exceptions thrown / no assertion failures.");
		String testString = "a non null string";
		check.that(testString, Is.Not.Null());
		testString = null;
		check.that(testString, Is.Null());
		step("Checking Is.True, and Is.False", "No exceptions thrown / no assertion failures.");
		check.that(true, Is.True());
		check.that(false, Is.Not.True());
		check.that(false, Is.False());
		check.that(true, Is.Not.False());
		step("Checking Is.EmptyString, Is.EmptyCollection, and Is.EmptyIterable", "No exceptions thrown / no assertion failures.");
		check.that("", Is.EmptyString());
		check.that("foo", Is.Not.EmptyString());
		check.that(new ArrayList<String>(), Is.EmptyCollection());
		check.that(Arrays.asList("hello", "world"), Is.Not.EmptyCollection());
		check.that(new ArrayList<String>(), Is.EmptyIterable());
		check.that(Arrays.asList("hello", "world"), Is.Not.EmptyIterable());
		step("Checking Is.StringContainingAllSubstringsInOrder, Is.StringMatchingRegex, Is.EqualToIgnoringCase, and Is.EqualToIgnoringWhitespace", "No exceptions thrown / no assertion failures.");
		check.that("hello foo world bar", Is.StringContainingAllSubstringsInOrder(Arrays.asList("hello", "world")));
		check.that("hello foo world bar", Is.Not.StringContainingAllSubstringsInOrder(Arrays.asList("bar", "foo")));
		check.that("hello foo world bar", Is.StringMatchingRegex("hello.*world"));
		check.that("hello foo world bar", Is.Not.StringMatchingRegex("bar.*foo"));
		check.that("Hello World", Is.EqualToIgnoringCase("hello world"));
		check.that("Hello World", Is.EqualToIgnoringWhitespace(" Hello  World  "));

		// negative tests
		step("Doing Negative tests (making sure failed assertions throw exceptions).", "Exception thrown for each failed assertion.");
		try
		{
			testString = null;
			check.that(testString, Is.Not.Null());
			tclog.info("FAIL: No exception thrown, expected previous check to fail.");
			retval = TestResult.FAIL;
		} catch(AssertionFailure failure)
		{
			tclog.info("Successful failure of assertion: '{}'.", failure.getMessage());
		}

		try
		{
			testString = "a non null string";
			check.that(testString, Is.Null());
			tclog.info("FAIL: No exception thrown, expected previous check to fail.");
			retval = TestResult.FAIL;
		} catch(AssertionFailure failure)
		{
			tclog.info("Successful failure of assertion: '{}'.", failure.getMessage());
		}

		return TestResult.PASS;
	}

	@Override
	public UUID getTestUUID()
	{
		return uuid;
	}
}
