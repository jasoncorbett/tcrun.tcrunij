package org.tcrun.plugins.tcapiplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Before;
import org.junit.Test;
import org.tcrun.api.Result;
import org.tcrun.api.ResultStatus;
import org.tcrun.api.TCRunContext;
import org.tcrun.tcapi.AbstractSimpleTestCase;
import org.tcrun.tcapi.NotTestedException;
import org.tcrun.tcapi.ResultBasedTestError;
import static org.junit.Assert.*;
import org.tcrun.tcapi.TestCaseError;
import org.tcrun.tcapi.TestResult;

/**
 *
 * @author jcorbett
 */
public class TCApiRunnableTestTests
{
	private TCApiRunnableTest runner;
	private EmptyContext context;

	@Before
	public void setup()
	{
		context = new EmptyContext();
		runner = new TCApiRunnableTest(TcapiMock.class, context);
	}

	@Test
	public void cleanupRunAfterSetupThrowsException()
	{
		runner.setConfigurationValue(TcapiMock.SETUP_THROW_EXCEPTION, "true");
		runner.runTest(context);
		assertNotNull("Running the test should always provide a result.", context.getLastResult());
		assertSame("Result should be a BrokenTest", ResultStatus.BROKEN_TEST, context.getLastResult().getStatus());
		assertTrue("Cleanup SHOULD ALWAYS BE RUN!", ((TcapiMock)context.getLastResult().getTest().getTestRunner().getTestInstance()).ranCleanupMethod());
	}

	@Test
	public void cleanupRunAfterTestThrowsException()
	{
		runner.setConfigurationValue(TcapiMock.TEST_THROW_EXCEPTION, "true");
		runner.runTest(context);
		assertNotNull("Running the test should always provide a result.", context.getLastResult());
		assertSame("Result should be a BrokenTest", ResultStatus.BROKEN_TEST, context.getLastResult().getStatus());
		assertTrue("Cleanup SHOULD ALWAYS BE RUN!", ((TcapiMock)context.getLastResult().getTest().getTestRunner().getTestInstance()).ranCleanupMethod());
	}

	@Test
	public void testCalledWhenHandleExceptionReturnsTrue()
	{
		runner.setConfigurationValue(TcapiMock.SETUP_THROW_EXCEPTION, "true");
		runner.setConfigurationValue(TcapiMock.HANDLE_EXCEPTION_RETURN_TRUE, "true");
		runner.runTest(context);
		assertNotNull("Running the test should always provide a result.", context.getLastResult());
		assertSame("Result should be a Skipped", ResultStatus.SKIPPED, context.getLastResult().getStatus());
		assertTrue("Test should be called when handle exception returns true.", ((TcapiMock)context.getLastResult().getTest().getTestRunner().getTestInstance()).ranTestMethod());
		assertTrue("Cleanup SHOULD ALWAYS BE RUN!", ((TcapiMock)context.getLastResult().getTest().getTestRunner().getTestInstance()).ranCleanupMethod());
	}

	@Test
	public void resultAddedForReturnValue()
	{
		runner.runTest(context);
		assertNotNull("Running the test should always provide a result.", context.getLastResult());
		assertSame("Result should be a Skipped Test", ResultStatus.SKIPPED, context.getLastResult().getStatus());
	}

	@Test
	public void overrideResultExceptionThrownInSetup()
	{
		runner.setConfigurationValue(TcapiMock.SETUP_THROW_EXCEPTION, "true");
		runner.setConfigurationValue(TcapiMock.EXCEPTION_TYPE, TcapiMock.EXCEPTION_PASS);
		runner.runTest(context);
		assertNotNull("Running the test should always provide a result.", context.getLastResult());
		assertSame("Result should be a Pass (A Result based exception was thrown with a value of PASS).", ResultStatus.PASS, context.getLastResult().getStatus());
	}

	@Test
	public void overrideResultExceptionThrownInTest()
	{
		runner.setConfigurationValue(TcapiMock.TEST_THROW_EXCEPTION, "true");
		runner.setConfigurationValue(TcapiMock.EXCEPTION_TYPE, TcapiMock.EXCEPTION_FAIL);
		runner.runTest(context);
		assertNotNull("Running the test should always provide a result.", context.getLastResult());
		assertSame("Result should be a Fail (A Result based exception was thrown with a value of FAIL).", ResultStatus.FAIL, context.getLastResult().getStatus());
	}

	@Test
	public void notTestedExceptionCasesNotTestedResult()
	{
		runner.setConfigurationValue(TcapiMock.SETUP_THROW_EXCEPTION, "true");
		runner.setConfigurationValue(TcapiMock.EXCEPTION_TYPE, TcapiMock.EXCEPTION_NOT_TESTED);
		runner.runTest(context);
		assertNotNull("Running the test should always provide a result.", context.getLastResult());
		assertSame("Result should be a Not Tested (A Result based exception was thrown with a value of NOT_TESTED).", ResultStatus.NOT_TESTED, context.getLastResult().getStatus());
	}

	@Test
	public void byDefaultTestNotCalledWhenHandleExceptionReturnsFalse()
	{
		runner.setConfigurationValue(TcapiMock.SETUP_THROW_EXCEPTION, "true");
		runner.setConfigurationValue(TcapiMock.EXCEPTION_TYPE, TcapiMock.EXCEPTION_NOT_TESTED);
		runner.runTest(context);
		assertNotNull("Running the test should always provide a result.", context.getLastResult());
		assertSame("Result should be a Not Tested (A Result based exception was thrown with a value of NOT_TESTED).", ResultStatus.NOT_TESTED, context.getLastResult().getStatus());
		assertFalse("Test should not be called when handle exception returns false.", ((TcapiMock)context.getLastResult().getTest().getTestRunner().getTestInstance()).ranTestMethod());
	}
}

class EmptyContext implements TCRunContext
{
	private Result result;

	public EmptyContext()
	{
		this.result = null;
	}

	@Override
	public ConcurrentMap<String, String> getTCRunConfiguration()
	{
		return new ConcurrentHashMap<String, String>();
	}

	@Override
	public ConcurrentMap<String, String> getTestCaseConfiguration()
	{
		return new ConcurrentHashMap<String, String>();
	}

	@Override
	public String getTestRunID()
	{
		return "EMPTYTRID";
	}

	@Override
	public File getTCRunRoot()
	{
		return null;
	}

	@Override
	public void addResult(Result result)
	{
		this.result = result;
	}

	public Result getLastResult()
	{
		return result;
	}

	@Override
	public List<Result> getResultList()
	{
		return new ArrayList<Result>();
	}
	
}

class TcapiMock extends AbstractSimpleTestCase
{
	public static String SETUP_THROW_EXCEPTION = "setupThrowException";
	public static String TEST_THROW_EXCEPTION = "testThrowException";
	public static String CLEANUP_THROW_EXCEPTION = "cleanupThrowException";
	public static String EXCEPTION_TYPE = "exceptionType";
	public static String EXCEPTION_FAIL = "fail";
	public static String EXCEPTION_PASS = "pass";
	public static String EXCEPTION_NOT_TESTED = "not tested";
	public static String HANDLE_EXCEPTION_RETURN_TRUE = "handle exception return true";

	private boolean ranTest = false;
	private boolean ranCleanup = false;
	private Exception exception = null;

	@Override
	public void setup() throws Exception
	{
		if(configValue(SETUP_THROW_EXCEPTION, "false").equalsIgnoreCase("true"))
		{
			throwException();
		}
	}

	public void throwException() throws Exception
	{
		if(tcinfo.containsKey(EXCEPTION_TYPE))
		{
			if(configValue(EXCEPTION_TYPE).equalsIgnoreCase(EXCEPTION_FAIL))
			{
				throw new ResultBasedTestError("Test Exception Fail") {

					@Override
					public TestResult getResult()
					{
						return TestResult.FAIL;
					}
				};
			} else if(configValue(EXCEPTION_TYPE).equalsIgnoreCase(EXCEPTION_NOT_TESTED))
			{
				throw new NotTestedException("Test Exception Not Tested");
			} else if(configValue(EXCEPTION_TYPE).equalsIgnoreCase(EXCEPTION_PASS))
			{
				throw new ResultBasedTestError("Test Exception Pass") {

					@Override
					public TestResult getResult()
					{
						return TestResult.PASS;
					}
				};
			}
		} else
		{
			throw new Exception("Test Exception");
		}
	}

	@Override
	public boolean handleException(Exception e)
	{
		exception = e;
		try
		{
			if (configValue(HANDLE_EXCEPTION_RETURN_TRUE, "false").equalsIgnoreCase("true"))
			{
				return true;
			}
		} catch (TestCaseError ex)
		{
			return false;
		}
		return false;
	}

	@Override
	public TestResult test() throws Exception
	{
		ranTest = true;
		if(configValue(TEST_THROW_EXCEPTION, "false").equalsIgnoreCase("true"))
			throwException();
		return TestResult.SKIPPED;
	}

	@Override
	public void cleanup() throws Exception
	{
		ranCleanup = true;
		if(configValue(CLEANUP_THROW_EXCEPTION, "false").equalsIgnoreCase("true"))
			throwException();
	}

	@Override
	public UUID getTestUUID()
	{
		return UUID.fromString("fdae10b5-a501-449c-b25d-8ca57ccb2574");
	}

	public boolean ranTestMethod()
	{
		return ranTest;
	}

	public boolean ranCleanupMethod()
	{
		return ranCleanup;
	}

	public Exception handleExceptionMethodException()
	{
		return exception;
	}

}

