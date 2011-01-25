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
	private String frameId;

	public InFrameRenderedWebElement(WebElement real, WebDriver driver, String frame)
	{
		super(real, driver);
		this.frameId = frame;
	}

	@Override
	protected void beforeOperation()
	{
		String[] frames = frameId.split("\\.");
		for(String frame : frames)
		{
			driver.switchTo().frame(frame);
		}
	}

	@Override
	protected void afterOperation()
	{
		driver.switchTo().defaultContent();
	}
}
