package org.tcrun.tcapi.selenium;

/**
 * A "Self Aware Page" is a class that can determine if the browser's current page is itself.  It also knows how
 * to handle any forms on the page, to easily allow automation to continue.  To help provide information to this
 * "Self Aware Page" when you implement this interface you will need to define the type of context object that is
 * used to handle the page.
 *
 * @author jcorbett
 */
public interface SelfAwarePage<T>
{
	/**
	 * Determine if the browser's current page is the one represented by this class.  It is VERY important that this
	 * be a quick check, not one that waits for 30 seconds before returning false.  You should pass in a 0 timeout to
	 * whatever elements you look for in the browser.
	 *
	 * @param browser The web driver based browser object.
	 * @return true if the current page is the one this class represents, false otherwise.
	 */
	public boolean isCurrentPage(WebDriverWrapper browser);

	/**
	 * Handle any elements on the page, in accordance to information provided by the context object.
	 *
	 * @param browser The web driver based browser implementation.
	 * @param context The context in which to handle the elements on the page.
	 */
	public void handlePage(WebDriverWrapper browser, T context);
}
