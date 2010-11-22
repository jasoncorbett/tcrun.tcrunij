package org.tcrun.tcapi.selenium.finders;

import java.util.ArrayList;
import java.util.Arrays;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author slambson
 */
public class FindByHrefContains extends AbstractFindByParentBy
{
	private String hrefContainsText;

	public FindByHrefContains(String hrefContainsText)
	{
		this.hrefContainsText = hrefContainsText;
	}

	@Override
	public boolean matches(WebElement e)
	{
		String attrValue = e.getAttribute("href");
		return attrValue != null && attrValue.contains(hrefContainsText);
	}

	@Override
	public ArrayList<By> getParentBy()
	{
		return new ArrayList<By>(Arrays.asList(By.tagName("a")));
	}

	@Override
	public String toString()
	{
		return String.format("By href attribute containing '{}'.", hrefContainsText);
	}

}
