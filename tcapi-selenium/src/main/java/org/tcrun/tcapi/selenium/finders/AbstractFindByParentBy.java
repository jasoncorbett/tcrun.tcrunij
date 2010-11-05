package org.tcrun.tcapi.selenium.finders;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 * By implementing the abstract methods of this abstract class you can easily create a finder
 * which looks for the value of an attribute in a list of elements.
 *
 * @author jcorbett
 */
public abstract class AbstractFindByParentBy extends By
{
	public abstract boolean matches(WebElement e);

	public abstract ArrayList<By> getParentBy();

	@Override
	public List<WebElement> findElements(SearchContext context)
	{
		ArrayList<WebElement> possible_elements = new ArrayList<WebElement>();
		for(By parentBy: getParentBy())
		{
			possible_elements.addAll(context.findElements(parentBy));
		}
		List<WebElement> retval = new ArrayList<WebElement>();
		for (WebElement possible : possible_elements)
		{
			if (matches(possible))
			{
				retval.add(possible);
			}
		}
		return retval;

	}

}
