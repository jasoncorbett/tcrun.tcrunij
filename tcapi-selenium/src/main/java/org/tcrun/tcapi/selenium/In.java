package org.tcrun.tcapi.selenium;

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

	public static WebContainer ParentElement(PageElement parent)
	{
		return new ParentElementContainer(parent);
	}
}
