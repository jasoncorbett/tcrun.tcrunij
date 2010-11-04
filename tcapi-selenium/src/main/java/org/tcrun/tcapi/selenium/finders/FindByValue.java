package org.tcrun.tcapi.selenium.finders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class FindByValue extends AbstractFindByParentBy
{
	private String valueText;

	public FindByValue(String valueText)
	{
		this.valueText = valueText;
	}

	@Override
	public boolean matches(WebElement e)
	{
		String attrValue = e.getAttribute("value");
		return attrValue != null && attrValue.equals(valueText);
	}

	@Override
	public By getParentBy()
	{
		return By.tagName("input");
	}

	@Override
	public String toString()
	{
		return String.format("By src attribute matching '{}'.", valueText);
	}

}
