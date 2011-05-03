/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.selenium;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author jcorbett
 */
public class InFrameRenderedWebElement extends ProxyRenderedWebElement implements RenderedWebElement, Locatable
{
	private XLogger logger = XLoggerFactory.getXLogger(InFrameRenderedWebElement.class);
	private String frameId;
        private WebElement frameWebElement = null;

	public InFrameRenderedWebElement(WebElement real, WebDriver driver, String frame)
	{
		super(real, driver);
		this.frameId = frame;
	}

        public InFrameRenderedWebElement(WebElement real, WebDriver driver, WebElement frame)
        {
                super(real, driver);
                this.frameWebElement = frame;
        }

	@Override
	protected void beforeOperation()
	{
                //driver.switchTo().defaultContent();
                if (frameWebElement == null)
                {
                    // we have a frame name
                    String[] frames = frameId.split("\\.");
                    for(String frame : frames)
                    {
                            driver.switchTo().frame(frame);
                    }
                }
                else
                {
                    driver.switchTo().frame(frameWebElement);
                }
	}

	@Override
	protected void afterOperation()
	{
		//logger.debug("Inside afterOperation()");
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
			    retval.add(new InFrameRenderedWebElement(element, driver, frameId));
                        else
                            retval.add(new InFrameRenderedWebElement(element, driver, frameWebElement));
		}
		return retval;
	}

	@Override
	public WebElement findElement(By by)
	{
                if (frameWebElement == null)
		    return new InFrameRenderedWebElement(super.findElement(by), driver, frameId);
                else
                    return new InFrameRenderedWebElement(super.findElement(by), driver, frameWebElement);
	}

	@Override
	public Point getLocationOnScreenOnceScrolledIntoView()
	{
		this.beforeOperation();
		Point retval = ((Locatable)this.rwebElement).getLocationOnScreenOnceScrolledIntoView();
		this.afterOperation();
		return retval;
	}

	@Override
	public Coordinates getCoordinates()
	{
		this.beforeOperation();
		Coordinates retval = ((Locatable)this.rwebElement).getCoordinates();
		this.afterOperation();
		return retval;
	}

}
