package org.tcrun.selenium;

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
