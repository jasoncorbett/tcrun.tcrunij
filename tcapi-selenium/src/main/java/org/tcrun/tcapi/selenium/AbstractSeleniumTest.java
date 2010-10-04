package org.tcrun.tcapi.selenium;

import java.util.Map;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.tcapi.AbstractSimpleTestCase;
/**
 *
 * @author jcorbett
 */
public abstract class AbstractSeleniumTest extends AbstractSimpleTestCase
{
	protected WebDriverWrapper browser;

	@Override
	public void tcSetup(Map<String, String> configuration) throws Exception
	{
		tcinfo = configuration;
		tclog = XLoggerFactory.getXLogger("test." + AbstractSeleniumTest.class.getName());
		browser = new DefaultWebDriverWrapper(configValue("browser", "headless"));
		browser.setDefaultTimeout(30);
		super.tcSetup(configuration);
	}
}
