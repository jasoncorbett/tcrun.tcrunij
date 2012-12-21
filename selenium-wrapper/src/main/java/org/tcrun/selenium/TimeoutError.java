package org.tcrun.selenium;

import org.openqa.selenium.WebDriverException;

/**
 * This exception means that while waiting for a particular condition to be true the maximum amount
 * of time had been exceeded.
 * 
 * User: jcorbett
 * Date: 3/16/12
 * Time: 3:55 PM
 */
public class TimeoutError extends WebDriverException
{
    public TimeoutError(String message)
    {
        super(message);
    }
}
