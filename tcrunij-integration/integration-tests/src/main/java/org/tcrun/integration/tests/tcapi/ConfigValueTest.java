package org.tcrun.itegration.tests.tcapi;

import java.util.UUID;
import org.tcrun.api.annotations.ConfigValue;
import org.tcrun.api.annotations.TestName;
import org.tcrun.tcapi.AbstractSimpleTestCase;
import org.tcrun.tcapi.TestResult;
import static org.tcrun.tcapi.assertlib.MatchBuilder.*;

/**
 *
 * @author jcorbett
 */
@TestName("ConfigValue annotation basic test")
public class ConfigValueTest extends AbstractSimpleTestCase
{
	@ConfigValue("example.config")
	private String example;

	@Override
	public TestResult test() throws Exception
	{
		check.that(example, Is.Not.Null());
		check.that(example, Is.StringContaining("default"));
		return TestResult.PASS;
	}

	@Override
	public UUID getTestUUID()
	{
		throw new UnsupportedOperationException("Not needed.");
	}
	
}
