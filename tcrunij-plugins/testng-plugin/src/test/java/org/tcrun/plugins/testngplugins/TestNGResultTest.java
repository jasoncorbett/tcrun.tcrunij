package org.tcrun.plugins.testngplugins;

import java.math.BigDecimal;
import org.tcrun.api.ResultStatus;
import org.tcrun.plugins.testngplugin.TestNGResult;
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

}
