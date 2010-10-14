package org.tcrun.tcapi.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import org.openqa.selenium.SearchContext;
import java.lang.String;

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

