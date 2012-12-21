package org.tcrun.selenium;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

/**
 * You must implement this to get support for saving screenshots and other files from
 * the webdriver wrapper.
 * 
 * @author jcorbett
 */
public interface OutputFileSupport
{
	public File getOutputFile(String filename);
	public OutputStream getOutputStream(String filename) throws FileNotFoundException;
	public File getSessionOutputFile(String filename);
	public OutputStream getSessionOutputStream(String filename) throws FileNotFoundException;
	
}
