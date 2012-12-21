/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.selenium;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author jcorbett
 */
public class InFrameWebElement extends ProxyWebElement
{
	private String frameId;
    private WebElement frameWebElement = null;
	private By finder;

	public InFrameWebElement(By finder, WebDriver driver, String frame)
	{
		super(null, driver);
		this.frameId = frame;
		this.finder = finder;
	}

    public InFrameWebElement(By finder, WebDriver driver, WebElement frame)
	{
		super(null, driver);
		this.frameWebElement = frame;
		this.finder = finder;
	}

	public InFrameWebElement(WebElement element, WebDriver driver, String frame)
	{
		super(element, driver);
		this.frameId = frame;
		this.finder = null;
	}

    public InFrameWebElement(WebElement element, WebDriver driver, WebElement frame)
	{
		super(element, driver);
		this.frameWebElement = frame;
		this.finder = null;
	}

	@Override
	public void beforeOperation()
	{
		//driver.switchTo().defaultContent();
                if (frameWebElement == null)
                {
                        String[] frames = frameId.split("\\.");
                        for(String frame : frames)
                        {
                                driver.switchTo().frame(frame);
                        }
                }
                else
                {
					if(InFrameWebElement.class.isAssignableFrom(frameWebElement.getClass()))
					{
						((InFrameWebElement)frameWebElement).beforeOperation();
						driver.switchTo().frame(((InFrameWebElement)frameWebElement).real);
					} else
					{
                        driver.switchTo().frame(frameWebElement);
					}
                }
				if(finder != null)
					real = driver.findElement(finder);
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
            if (frameWebElement == null)
			    retval.add(new InFrameWebElement(element, driver, frameId));
            else
                retval.add(new InFrameWebElement(element, driver, frameWebElement));
		}
		return retval;
	}

	@Override
	public WebElement findElement(By by)
	{
                if (frameWebElement == null)
		    return new InFrameWebElement(super.findElement(by), driver, frameId);
                else
                    return new InFrameWebElement(super.findElement(by), driver, frameWebElement);
	}
}
