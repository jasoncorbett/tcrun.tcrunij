package org.tcrun.selenium.finders;

import java.util.ArrayList;
import java.util.Arrays;
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
	public ArrayList<By> getParentBy()
	{
		return new ArrayList<By>(Arrays.asList(By.tagName("a")));
	}

	@Override
	public String toString()
	{
		return String.format("By href attribute matching '%s'.", hrefText);
	}

}
