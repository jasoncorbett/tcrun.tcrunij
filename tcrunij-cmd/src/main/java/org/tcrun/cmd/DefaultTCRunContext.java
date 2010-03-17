package org.tcrun.cmd;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.MDC;
import org.tcrun.api.Result;
import org.tcrun.api.TCRunContext;

/**
 * This is the default implimentation of the tcrun context.  The only special thing it does, is find
 * the root of the tcrun hierarchy by assuming that the tcrunij-api jar is in the lib/ sub directory (standard).
 *
 * @author jcorbett
 */
public class DefaultTCRunContext implements TCRunContext
{
	private File m_tcrunRoot;
	private ConcurrentMap<String, String> m_tcrunConfig;
	private ConcurrentMap<String, String> m_testCaseConfig;
	private String m_testRunId;
	private List<Result> m_resultList;

	public DefaultTCRunContext() throws URISyntaxException
	{
		// Initialize testrun id
		m_testRunId = System.getProperty("TESTRUNID");
		if(m_testRunId == null)
		{
			m_testRunId = System.getenv("TESTRUNID");
		}

		// this is not an else if on purpose, because after the first if m_testRunId may still be null.
		if(m_testRunId == null)
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			m_testRunId = format.format(new Date());
		}
		// this is usable from within logging
		MDC.put("TestRunId", m_testRunId);

		// get the location of the tcrunij-api jar file, then get the parent directory (lib), and get the parent
		// of that (tcrunij root)
		m_tcrunRoot = (new File(TCRunContext.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getParentFile();
		m_tcrunConfig = new ConcurrentHashMap<String, String>();
		m_testCaseConfig = new ConcurrentHashMap<String, String>();
		m_resultList = new Vector<Result>();
	}

	public ConcurrentMap<String, String> getTCRunConfiguration()
	{
		return m_tcrunConfig;
	}

	public ConcurrentMap<String, String> getTestCaseConfiguration()
	{
		return m_testCaseConfig;
	}

	public String getTestRunID()
	{
		return m_testRunId;
	}

	public File getTCRunRoot()
	{
		return m_tcrunRoot;
	}

	public List<Result> getResultList()
	{
		return m_resultList;
	}
}
