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
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.PluginManager;
import org.tcrun.api.PluginManagerFactory;
import org.tcrun.api.Result;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.ResultWatcherPlugin;

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

	private static XLogger logger = XLoggerFactory.getXLogger(DefaultTCRunContext.class);

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

		logger.debug("Looking for root of tcrunij directory based on location of tcrunij-api jar file.");
		// get the location of the tcrunij-api jar file, then get the parent directory (lib), and get the parent
		// of that (tcrunij root)
		// thanks to http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
		m_tcrunRoot = (new File(TCRunContext.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getParentFile();
		logger.debug("TCRunIJ Home should be in '{}'", m_tcrunRoot);
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

	public void addResult(Result result)
	{
		logger.entry(result);
		PluginManager plugin_manager = PluginManagerFactory.getPluginManager();
		List<ResultWatcherPlugin> result_watchers = plugin_manager.getPluginsFor(ResultWatcherPlugin.class);
		logger.debug("There are '{}' result watchers, calling each one for result from test class '{}' with status '{}'.", new Object[] {result_watchers.size(), result.getTest().getTestRunner().getTestClass(), result.getStatus()});
		for(ResultWatcherPlugin plugin : result_watchers)
		{
			logger.debug("Calling result watcher '{}' (class: '{}').", plugin.getPluginName(), plugin.getClass().getCanonicalName());
			plugin.onResultFiled(result);
			logger.debug("Done calling result watcher '{}' (class: '{}').", plugin.getPluginName(), plugin.getClass().getCanonicalName());
		}
		logger.debug("Done calling result watchers, adding result to list.");
		m_resultList.add(result);
	}
}
