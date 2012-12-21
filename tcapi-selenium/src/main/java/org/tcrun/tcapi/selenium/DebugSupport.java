package org.tcrun.tcapi.selenium;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.slf4j.MDC;
import org.tcrun.selenium.OutputFileSupport;

/**
 *
 * @author jcorbett
 */
public class DebugSupport implements OutputFileSupport
{

	public File getOutputFile(String filename)
	{
		return new File("results" + File.separator + MDC.get("TestCaseDir") + File.separator + filename);
	}

	public OutputStream getOutputStream(String filename) throws FileNotFoundException
	{
		return new FileOutputStream(getOutputFile(filename));
	}

	public File getSessionOutputFile(String filename)
	{
		return new File("results" + File.separator + MDC.get("TestRunId") + File.separator + filename);
	}

	public OutputStream getSessionOutputStream(String filename) throws FileNotFoundException
	{
		return new FileOutputStream(getSessionOutputFile(filename), true);
	}
}
