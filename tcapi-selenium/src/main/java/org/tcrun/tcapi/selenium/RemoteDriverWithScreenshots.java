package org.tcrun.tcapi.selenium;

import java.net.URL;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 *
 * @author jcorbett
 */
public class RemoteDriverWithScreenshots extends RemoteWebDriver implements TakesScreenshot
{
	public RemoteDriverWithScreenshots(URL url, Capabilities capabilities)
	{
		super(url, capabilities);
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> ot) throws WebDriverException
	{
		String base64 = execute(DriverCommand.SCREENSHOT).getValue().toString();
    	return ot.convertFromBase64Png(base64);
	}
}
