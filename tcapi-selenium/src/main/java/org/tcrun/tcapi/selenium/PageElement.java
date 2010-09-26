package org.tcrun.tcapi.selenium;

import java.util.Calendar;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author jcorbett
 */
public class PageElement
{
	private String name;
	private By finder;

	public PageElement(String name, By finder)
	{
		this.name = name;
		this.finder = finder;
	}

	public PageElement(By finder)
	{
		name = finder.toString();
		this.finder = finder;
	}

	public By getFinder()
	{
		return finder;
	}

	public String getName()
	{
		return name;
	}

	public boolean exists(WebDriver browser, int timeout)
	{
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.SECOND, timeout);
		boolean exists = false;
		while(Calendar.getInstance().before(endTime) && !exists)
		{
			try
			{
				browser.findElement(finder).isEnabled();
				exists = true;
			} catch(NoSuchElementException ex)
			{
			} catch(StaleElementReferenceException ex)
			{
			}
			if(!exists)
			{
				try
				{
					Thread.sleep(200);
				} catch(InterruptedException ex)
				{
				}
			}
		}
		return exists;
	}
}
