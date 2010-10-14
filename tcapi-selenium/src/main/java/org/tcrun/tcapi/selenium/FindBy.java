package org.tcrun.tcapi.selenium;

import org.openqa.selenium.By;

/**
 *
 * @author slambson
 */
public abstract class FindBy extends By
{
	public static By src(String alt)
	{
	    return By.xpath("//*[@src='" + alt + "']");
	}

	public static By alt(String alt)
	{
	    return By.xpath("//*[@alt='" + alt + "']");
	}
}

