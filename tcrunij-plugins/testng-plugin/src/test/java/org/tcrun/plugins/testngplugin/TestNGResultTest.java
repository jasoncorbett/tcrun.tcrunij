package org.tcrun.plugins.testngplugin;

import java.math.BigDecimal;
import org.tcrun.api.ResultStatus;
import org.testng.ITestResult;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 *
 * @author jcorbett
 */
public class TestNGResultTest
{
	private ITestResult mockResult;
	private TestNGResult result;

	@BeforeTest
	public void setup()
	{
		mockResult = mock(ITestResult.class);
		result = new TestNGResult(mockResult);
	}

	@Test
	public void checkSuccessStatus()
	{
		when(mockResult.getStatus()).thenReturn(ITestResult.SUCCESS);
		ResultStatus testResult = result.getStatus();
		assertThat(testResult).isNotNull();
		assertThat(testResult).isEqualTo(ResultStatus.PASS);
	}

	@Test
	public void checkSuccessPercentageFailureStatus()
	{
		when(mockResult.getStatus()).thenReturn(ITestResult.SUCCESS_PERCENTAGE_FAILURE);
		ResultStatus testResult = result.getStatus();
		assertThat(testResult).isNotNull();
		assertThat(testResult).isEqualTo(ResultStatus.PASS);
	}

	@Test
	public void checkFailureStatus()
	{
		when(mockResult.getStatus()).thenReturn(ITestResult.FAILURE);
		ResultStatus testResult = result.getStatus();
		assertThat(testResult).isNotNull();
		assertThat(testResult).isEqualTo(ResultStatus.FAIL);
	}

	@Test
	public void checkStartedStatus()
	{
		when(mockResult.getStatus()).thenReturn(ITestResult.STARTED);
		ResultStatus testResult = result.getStatus();
		assertThat(testResult).isNotNull();
		assertThat(testResult).isEqualTo(ResultStatus.NOT_TESTED);
	}

	@Test
	public void checkSkipStatus()
	{
		when(mockResult.getStatus()).thenReturn(ITestResult.SKIP);
		ResultStatus testResult = result.getStatus();
		assertThat(testResult).isNotNull();
		assertThat(testResult).isEqualTo(ResultStatus.SKIPPED);
	}

    @Test
    public void checkFailReason()
    {
        String message = "An Example error message from an exception.";
        TestingError error = new TestingError(message);
        when(mockResult.getStatus()).thenReturn(ITestResult.FAILURE);
        when(mockResult.getThrowable()).thenReturn(error);
        String reason = result.getReason();
        assertThat(reason).contains(message);
        assertThat(reason).contains(TestingError.class.getName());
    }
    
    @Test
    public void checkPassReason()
    {
        when(mockResult.getStatus()).thenReturn(ITestResult.SUCCESS);
        String reason = result.getReason();
        assertThat(reason).isNotNull();
        assertThat(reason).containsIgnoringCase("returned success");
    }

    @Test
    public void checkSkipReason()
    {
        when(mockResult.getStatus()).thenReturn(ITestResult.SKIP);
        String reason = result.getReason();
        assertThat(reason).isNotNull();
        assertThat(reason).containsIgnoringCase("returned SKIP");
    }

    @Test
    public void checkSuccessPercentageReason()
    {
        when(mockResult.getStatus()).thenReturn(ITestResult.SUCCESS_PERCENTAGE_FAILURE);
        String reason = result.getReason();
        assertThat(reason).isNotNull();
        assertThat(reason).containsIgnoringCase("failed but the method has been annotated with successPercentage and this failure still keeps it within the success percentage requested.");
    }

    @Test
    public void checkStartedReason()
    {
        when(mockResult.getStatus()).thenReturn(ITestResult.STARTED);
        String reason = result.getReason();
        assertThat(reason).isNotNull();
        assertThat(reason).containsIgnoringCase("returned STARTED");
    }

}

class TestingError extends Exception
{
    public TestingError(String message)
    {
        super(message);
    }
}
