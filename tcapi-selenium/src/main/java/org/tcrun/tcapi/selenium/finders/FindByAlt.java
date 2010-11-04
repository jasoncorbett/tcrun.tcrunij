
package org.tcrun.tcapi.selenium.finders;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class FindByAlt extends AbstractFindByParentBy
{

	String altText;

	public FindByAlt(String altText)
	{
		this.altText = altText;
	}

	@Override
	public String toString()
	{
		return String.format("By alt text '{}'.", altText);
	}

	@Override
	public boolean matches(WebElement e)
	{
		String attrValue = e.getAttribute("alt");
		return attrValue != null && attrValue.equals(altText);
	}

	@Override
	public By getParentBy()
	{
		return By.tagName("img");
	}
}
