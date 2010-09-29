package org.tcrun.tcapi.selenium;

import java.util.Map;
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
		super.tcSetup(configuration);
		browser = new DefaultWebDriverWrapper(configValue("browser", "headless"));
		browser.setDefaultTimeout(30);
	}
}
