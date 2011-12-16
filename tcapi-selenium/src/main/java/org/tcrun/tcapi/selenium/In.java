package org.tcrun.tcapi.selenium;

import org.tcrun.selenium.FrameContainer;
import org.tcrun.selenium.ParentElementContainer;
import org.tcrun.selenium.WebContainer;

/**
 *
 * @author jcorbett
 */
public class In
{
	public static WebContainer Frame(String frameId)
	{
		return new FrameContainer(frameId);
	}

	public static WebContainer Frame(PageElement element)
	{
		return new FrameContainer(element);
	}

	public static WebContainer ParentElement(PageElement parent)
	{
		return new ParentElementContainer(parent);
	}
}
