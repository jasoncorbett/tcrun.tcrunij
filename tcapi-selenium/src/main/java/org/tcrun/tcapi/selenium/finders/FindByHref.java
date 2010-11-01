package org.tcrun.tcapi.selenium.finders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author slambson
 */
public class FindByHref extends AbstractFindByParentBy
{
	private String hrefText;

	public FindByHref(String hrefText)
	{
		this.hrefText = hrefText;
	}

	@Override
	public boolean matches(WebElement e)
	{
		String attrValue = e.getAttribute("href");
		return attrValue != null && attrValue.equals(hrefText);
	}

	@Override
	public By getParentBy()
	{
		return By.tagName("a");
	}

	@Override
	public String toString()
	{
		return String.format("By href attribute matching '{}'.", hrefText);
	}

}
