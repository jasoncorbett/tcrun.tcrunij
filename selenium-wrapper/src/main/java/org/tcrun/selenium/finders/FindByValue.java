package org.tcrun.selenium.finders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	public ArrayList<By> getParentBy()
	{
		return new ArrayList<By>(Arrays.asList(By.tagName("input")));
	}

	@Override
	public String toString()
	{
		return String.format("By value attribute matching '%s'.", valueText);
	}

}
