package org.tcrun.tcapi.selenium;

import org.openqa.selenium.By;
import org.tcrun.tcapi.selenium.finders.FindByAlt;
import org.tcrun.tcapi.selenium.finders.FindBySrc;

/**
 *
 * @author slambson
 */
public abstract class FindBy extends By
{
	/**
	 * Find an image by it's source.  This must match exactly.
	 *
	 * @param srcValue The exact value of the src attribute.
	 * @return a By instance that finds web elements in web driver.
	 */
	public static By src(String srcValue)
	{
	    return new FindBySrc(srcValue);
	}

	/**
	 * Find an image by the "alt" attribute value.  Lot's of images have an alt attribute providing
	 * alternate text to display.  Use this method to find an image by this text.
	 *
	 * @param alt The text of the alt attribute to search for.
	 *
	 * @return a By instance that finds web elements in web driver.
	 */
	public static By alt(String alt)
	{
	    return new FindByAlt(alt);
	}
}

