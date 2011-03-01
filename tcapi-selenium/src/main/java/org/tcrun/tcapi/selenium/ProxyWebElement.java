/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.tcapi.selenium;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jcorbett
 */
public class ProxyWebElement implements WebElement
{
	protected WebElement real;
	protected WebDriver driver;

	public ProxyWebElement(WebElement real, WebDriver driver)
	{
		this.real = real;
		this.driver = driver;
	}

	protected void beforeOperation()
	{
	}

	protected void afterOperation()
	{
	}

	@Override
	public void click()
	{
		try
		{
			beforeOperation();
			real.click();
		} finally
		{
			afterOperation();
		}
	}

	@Override
	public void submit()
	{
		try
		{
			beforeOperation();
			real.submit();
		} finally
		{
			afterOperation();
		}
	}

	@Override
	public String getValue()
	{
		String retval = null;
		try
		{
			beforeOperation();
			retval = real.getValue();
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public void sendKeys(CharSequence... keysToSend)
	{
		try
		{
			beforeOperation();
			real.sendKeys(keysToSend);
		} finally
		{
			afterOperation();
		}
	}

	@Override
	public void clear()
	{
		try
		{
			beforeOperation();
			real.clear();
		} finally
		{
			afterOperation();
		}
	}

	@Override
	public String getTagName()
	{
		String retval = null;
		try
		{
			beforeOperation();
			retval = real.getTagName();
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public String getAttribute(String name)
	{
		String retval = null;
		try
		{
			beforeOperation();
			retval = real.getAttribute(name);
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public boolean toggle()
	{
		boolean retval = false;
		try
		{
			beforeOperation();
			retval = real.toggle();
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public boolean isSelected()
	{
		boolean retval = false;
		try
		{
			beforeOperation();
			retval = real.isSelected();
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public void setSelected()
	{
		try
		{
			beforeOperation();
			real.setSelected();
		} finally
		{
			afterOperation();
		}
	}

	@Override
	public boolean isEnabled()
	{
		boolean retval = false;
		try
		{
			beforeOperation();
			retval = real.isEnabled();
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public String getText()
	{
		String retval = null;
		try
		{
			beforeOperation();
			retval = real.getText();
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public List<WebElement> findElements(By by)
	{
		List<WebElement> retval = null;
		try
		{
			beforeOperation();
			retval = real.findElements(by);
		} finally
		{
			afterOperation();
		}
		return retval;
	}

	@Override
	public WebElement findElement(By by)
	{
		WebElement retval = null;
		try
		{
			beforeOperation();
			retval = real.findElement(by);
		} finally
		{
			afterOperation();
		}
		return retval;
	}
}
