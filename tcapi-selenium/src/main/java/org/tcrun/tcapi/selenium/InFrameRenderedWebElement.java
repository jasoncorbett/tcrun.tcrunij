/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.selenium;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author jcorbett
 */
public class InFrameRenderedWebElement extends ProxyRenderedWebElement implements RenderedWebElement
{
	private XLogger logger = XLoggerFactory.getXLogger(InFrameRenderedWebElement.class);
	private String frameId;

	public InFrameRenderedWebElement(WebElement real, WebDriver driver, String frame)
	{
		super(real, driver);
		this.frameId = frame;
	}

	@Override
	protected void beforeOperation()
	{
		logger.debug("Inside beforeOperation()");
		driver.switchTo().defaultContent();
		String[] frames = frameId.split("\\.");
		for(String frame : frames)
		{
			driver.switchTo().frame(frame);
		}
	}

	@Override
	protected void afterOperation()
	{
		logger.debug("Inside afterOperation()");
		driver.switchTo().defaultContent();
	}


	@Override
	public List<WebElement> findElements(By by)
	{
		List<WebElement> orig = super.findElements(by);

		List<WebElement> retval = new ArrayList<WebElement>();

		for(WebElement element : orig)
		{
			retval.add(new InFrameRenderedWebElement(element, driver, frameId));
		}
		return retval;
	}

	@Override
	public WebElement findElement(By by)
	{
		return new InFrameRenderedWebElement(super.findElement(by), driver, frameId);
	}

}
