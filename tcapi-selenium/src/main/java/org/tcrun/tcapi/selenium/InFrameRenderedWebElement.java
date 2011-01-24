/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.selenium;

import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class InFrameRenderedWebElement extends ProxyRenderedWebElement implements RenderedWebElement
{
	private String frame;

	public InFrameRenderedWebElement(WebElement real, WebDriver driver, String frame)
	{
		super(real, driver);
		this.frame = frame;
	}

	@Override
	protected void beforeOperation()
	{
		driver.switchTo().frame(frame);
	}

	@Override
	protected void afterOperation()
	{
		driver.switchTo().defaultContent();
	}
}
