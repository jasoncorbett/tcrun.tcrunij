package org.tcrun.plugins.testngplugin;

import java.util.Map;
import org.tcrun.api.Result;
import org.tcrun.api.ResultStatus;
import org.tcrun.api.RunnableTest;
import org.testng.ITestResult;

/**
 *
 * @author jcorbett
 */
public class TestNGResult implements Result
{
	private ITestResult testngResult;

	public TestNGResult(ITestResult testngResult)
	{
		this.testngResult = testngResult;
	}

	@Override
	public ResultStatus getStatus()
	{
		switch(testngResult.getStatus())
		{
			case ITestResult.SUCCESS:
			case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
				return ResultStatus.PASS;
			case ITestResult.FAILURE:
				return ResultStatus.FAIL;
			case ITestResult.SKIP:
				return ResultStatus.SKIPPED;
			case ITestResult.STARTED:
				return ResultStatus.NOT_TESTED;
			default:
				// shouldn't ever happen, but just in case
				return ResultStatus.BROKEN_TEST;
		}
	}

	@Override
	public String getReason()
	{
        switch(testngResult.getStatus())
        {
            case ITestResult.FAILURE:
                return "Exception thrown (" + testngResult.getThrowable().getClass().getName() + "): " + testngResult.getThrowable().getMessage();
            case ITestResult.SUCCESS:
                return "TestNG returned SUCCESS status";
            case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
                return "TestNG reported that the test failed but the method has been annotated with successPercentage and this failure still keeps it within the success percentage requested.";
            case ITestResult.SKIP:
                return "TestNG returned SKIP status.";
            case ITestResult.STARTED:
                return "TestNG returned STARTED status.";
            default:
                return "TestNG returned an unknown result status: " + testngResult.getStatus();
        }
	}

	@Override
	public Map<String, String> getConfiguration()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setResultAttribute(String key, String value)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean resultAttributeExists(String key)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getResultAttribute(String key)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getResultAttribute(String key, String defaultValue)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, String> getAttributes()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public RunnableTest getTest()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
