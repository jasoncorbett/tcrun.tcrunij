/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.selenium;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
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
		driver.switchTo().defaultContent();
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

	@Override
	public List<WebElement> findElements(By by)
	{
		List<WebElement> orig = super.findElements(by);

		List<WebElement> retval = new ArrayList<WebElement>();

		for(WebElement element : orig)
		{
			retval.add(new InFrameWebElement(element, driver, frameId));
		}
		return retval;
	}

	@Override
	public WebElement findElement(By by)
	{
		return new InFrameWebElement(super.findElement(by), driver, frameId);
	}
}
