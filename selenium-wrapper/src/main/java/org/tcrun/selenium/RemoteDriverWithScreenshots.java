package org.tcrun.selenium;

import java.net.MalformedURLException;
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
	public static String REMOTE_URL = "remote.url";

	public RemoteDriverWithScreenshots(URL url, Capabilities capabilities)
	{
		super(url, capabilities);
	}

	public RemoteDriverWithScreenshots(Capabilities capabilities) throws MalformedURLException
	{
		super(new URL((String)capabilities.getCapability(REMOTE_URL)), capabilities);
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> ot) throws WebDriverException
	{
		String base64 = execute(DriverCommand.SCREENSHOT).getValue().toString();
    	return ot.convertFromBase64Png(base64);
	}
}
