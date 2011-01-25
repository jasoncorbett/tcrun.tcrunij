/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class InFrameWebElement extends ProxyWebElement
{
	private String frameId;
	public InFrameWebElement(WebElement real, WebDriver driver, String frame)
	{
		super(real, driver);
		this.frameId = frame;
	}

	@Override
	public void beforeOperation()
	{
		String[] frames = frameId.split("\\.");
		for(String frame : frames)
		{
			driver.switchTo().frame(frame);
		}
	}

	@Override
	public void afterOperation()
	{
		driver.switchTo().defaultContent();
	}
}
