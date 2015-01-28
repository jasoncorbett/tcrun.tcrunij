package org.tcrun.itegration.tests.tcapi;

import java.util.UUID;
import org.tcrun.api.annotations.TestGroup;
import org.tcrun.api.annotations.TestName;
import org.tcrun.tcapi.AbstractSimpleTestCase;
import org.tcrun.tcapi.TestResult;

/**
 *
 * @author jcorbett
 */
@TestName("TCRunIJ: Configuration test.")
@TestGroup("tcrunij")
public class ConfigurationTest extends AbstractSimpleTestCase
{
	private UUID uuid = UUID.fromString("0b5ce455-7c6f-4ce2-ad24-b5de21e3171e");

	@Override
	public TestResult test() throws Exception
	{
		for(String key : tcinfo.keySet())
		{
			tclog.info("{} = {}", key, tcinfo.get(key));
		}
		return TestResult.PASS;
	}

	@Override
	public UUID getTestUUID()
	{
		return uuid;
	}
}
