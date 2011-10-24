package org.tcrun.tcapi.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.tcrun.selenium.WebContainer;

/**
 * A subclass of tcrun selenium wrapper's PageElement for backward compatibility.
 * @author jcorbett
 */
public class PageElement extends org.tcrun.selenium.PageElement
{
    public PageElement(String name, WebContainer container, By finder) 
	{
		super(name, container, finder);
	}
   
	public PageElement(String name, By finder) 
	{
		super(name, finder);
	}

	public PageElement(By finder) 
	{
		super(finder);
	}

	public PageElement(WebElement element)
	{
		super(element);
	}
	
}
