package org.tcrun.tcapi.selenium;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.slf4j.MDC;

/**
 *
 * @author jcorbett
 */
public class DebugSupport
{
	public static File getOutputFile(String filename)
	{
		return new File("results" + File.separator + MDC.get("TestCaseDir") + File.separator + filename);
	}

	public static OutputStream getOutputStream(String filename) throws FileNotFoundException
	{
		return new FileOutputStream(getOutputFile(filename));
	}

        public static File getSessionOutputFile(String filename)
	{
		return new File("results" + File.separator + MDC.get("TestRunId") + File.separator + filename);
	}

	public static OutputStream getSessionOutputStream(String filename) throws FileNotFoundException
	{
		return new FileOutputStream(getOutputFile(filename), true);
	}
}
