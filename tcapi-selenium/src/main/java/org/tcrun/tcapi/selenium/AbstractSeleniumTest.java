package org.tcrun.tcapi.selenium;

import org.tcrun.tcapi.AbstractSimpleTestCase;
/**
 *
 * @author jcorbett
 */
public abstract class AbstractSeleniumTest extends AbstractSimpleTestCase
{
	protected WebDriverWrapper browser;

	@Override
	public void frameworkSetup() throws Exception
	{
		super.frameworkSetup();
		browser = new DefaultWebDriverWrapper(configValue("browser", "headless"));
		browser.setDefaultTimeout(30);
	}
}
