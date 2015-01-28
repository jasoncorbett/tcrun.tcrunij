package org.tcrun.selenium;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.net.MalformedURLException;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author jcorbett
 */
public class CapabilitiesFactory
{
	private static XLogger logger = XLoggerFactory.getXLogger("test." + CapabilitiesFactory.class.getName());

	public static DesiredCapabilities getCapabilitiesFor(String browserName, String remoteUrl)
	{
		DesiredCapabilities caps = getCapabilitiesFor(browserName);
		caps.setCapability(RemoteDriverWithScreenshots.REMOTE_URL, remoteUrl);
		return caps;
	}

	public static DesiredCapabilities getCapabilitiesFor(String browserName)
	{
		if(browserName.equalsIgnoreCase("ff") ||
		   browserName.equalsIgnoreCase("firefox"))
		{
			return DesiredCapabilities.firefox();
		} else if(browserName.equalsIgnoreCase("ffwin") ||
		          browserName.equalsIgnoreCase("firefoxonwindows"))
		{
			DesiredCapabilities caps = DesiredCapabilities.firefox();
			caps.setPlatform(Platform.WINDOWS);
			return caps;
        } else if(browserName.equalsIgnoreCase("ffnative"))
        {
            DesiredCapabilities caps = DesiredCapabilities.firefox();
            caps.setCapability("NativeEvents", true);
            return caps;
		} else if(browserName.equalsIgnoreCase("ie") ||
		          browserName.equalsIgnoreCase("InternetExplorer"))
		{
			return DesiredCapabilities.internetExplorer();
		} else if(browserName.equalsIgnoreCase("chrome"))
		{
			return DesiredCapabilities.chrome();
		} else if(browserName.equalsIgnoreCase("chromewin") ||
		          browserName.equalsIgnoreCase("chromeonwindows"))
		{
			DesiredCapabilities caps = DesiredCapabilities.chrome();
			caps.setPlatform(Platform.WINDOWS);
			return caps;
		} else if(
		          browserName.equalsIgnoreCase("htmlunit"))
		{
			return DesiredCapabilities.htmlUnit();
		} else if (browserName.equalsIgnoreCase("headless") ||
				   browserName.equalsIgnoreCase("phantomjs"))
		{
			return DesiredCapabilities.phantomjs();
		} else
		{
			logger.error("Unknown or invalid browser name {}, returning headless as a default.", browserName);
			return DesiredCapabilities.htmlUnit();
		}

	}

}
