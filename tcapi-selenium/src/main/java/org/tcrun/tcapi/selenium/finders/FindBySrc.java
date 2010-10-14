package org.tcrun.tcapi.selenium.finders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class FindBySrc extends AbstractFindByParentBy
{
	private String srcText;

	public FindBySrc(String srcText)
	{
		this.srcText = srcText;
	}

	@Override
	public boolean matches(WebElement e)
	{
		String attrValue = e.getAttribute("src");
		return attrValue != null && attrValue.equals(srcText);
	}

	@Override
	public By getParentBy()
	{
		return By.tagName("img");
	}

	@Override
	public String toString()
	{
		return String.format("By src attribute matching '{}'.", srcText);
	}

}
