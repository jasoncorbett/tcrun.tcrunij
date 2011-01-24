/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.selenium;

import java.awt.Dimension;
import java.awt.Point;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class ProxyRenderedWebElement extends ProxyWebElement implements RenderedWebElement
{
	protected RenderedWebElement rwebElement;

	public ProxyRenderedWebElement(WebElement real, WebDriver driver)
	{
		super(real, driver);
		rwebElement = (RenderedWebElement) real;
	}

	@Override
	public boolean isDisplayed()
	{
		boolean retval = false;
		try
		{
			beforeOperation();
			retval = rwebElement.isDisplayed();
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public Point getLocation()
	{
		Point retval = null;
		try
		{
			beforeOperation();
			retval = rwebElement.getLocation();
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public Dimension getSize()
	{
		Dimension retval = null;
		try
		{
			beforeOperation();
			retval = rwebElement.getSize();
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public void hover()
	{
		try
		{
			beforeOperation();
			rwebElement.hover();
		} finally
		{
			afterOperation();
		}
	}

	@Override
	public void dragAndDropBy(int moveRightBy, int moveDownBy)
	{
		try
		{
			beforeOperation();
			rwebElement.dragAndDropBy(moveRightBy, moveDownBy);
		} finally
		{
			afterOperation();
		}
	}

	@Override
	public void dragAndDropOn(RenderedWebElement element)
	{
		try
		{
			beforeOperation();
			rwebElement.dragAndDropOn(element);
		} finally
		{
			afterOperation();
		}
	}

	@Override
	public String getValueOfCssProperty(String propertyName)
	{
		String retval = null;
		try
		{
			beforeOperation();
			retval = rwebElement.getValueOfCssProperty(propertyName);
		} finally
		{
			afterOperation();
		}
		return retval;
	}
}
