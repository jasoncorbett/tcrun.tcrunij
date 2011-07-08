package org.tcrun.plugins.slickij;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.status.Status;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.slickij.api.ResultResource;
import org.tcrun.slickij.api.data.LogEntry;
import org.tcrun.slickij.api.data.LogLevel;
import org.tcrun.slickij.api.data.Result;

/**
 *
 * @author jcorbett
 */
public class ResultLogAppender extends AppenderBase<ILoggingEvent> implements Appender<ILoggingEvent>, Runnable
{
	private static XLogger logger = XLoggerFactory.getXLogger(ResultLogAppender.class);

	private Result result;

	private ConcurrentMap<Result, List<LogEntry>> toupload;

	private boolean stop;

	private Thread logUpdateThread;

	private ResultResource resultApi;

	public ResultLogAppender(ResultResource resultApi)
	{
		toupload = new ConcurrentHashMap<Result, List<LogEntry>>(); // we're accessing it from multiple threads
		logUpdateThread = new Thread(this);
		logUpdateThread.start();
		this.resultApi = resultApi;
	}

	@Override
	protected void append(ILoggingEvent eventObject)
	{
		if(result != null)
		{
			// we don't need a synchronized list, because we synchronize around the instance
			if(!toupload.containsKey(result))
				toupload.putIfAbsent(result, new ArrayList<LogEntry>());
			List<LogEntry> entryList = toupload.get(result);
			LogEntry entry = new LogEntry();
			entry.setEntryTime(new Date(eventObject.getTimeStamp()));
			entry.setLevel(LogLevel.valueOf(eventObject.getLevel().toString()));
			entry.setLoggerName(eventObject.getLoggerName());
			entry.setMessage(eventObject.getMessage());
			if(eventObject.getThrowableProxy() != null)
			{
				IThrowableProxy ex = eventObject.getThrowableProxy();
				entry.setExceptionClassName(ex.getClassName());
				entry.setExceptionMessage(ex.getMessage());
				StackTraceElementProxy[] stackTraceElements = ex.getStackTraceElementProxyArray();
				List<String> stacktrace = new ArrayList<String>(stackTraceElements.length);
				for(StackTraceElementProxy element : stackTraceElements)
					stacktrace.add(element.getSTEAsString());
				entry.setExceptionStackTrace(stacktrace);
			}
			synchronized(entryList)
			{
				entryList.add(entry);
			}

		}
	}

	public Result getResult()
	{
		return result;
	}

	public void setResult(Result result)
	{
		this.result = result;
	}

	public synchronized boolean getStop()
	{
		return stop;
	}

	public synchronized void setStop(boolean stop)
	{
		this.stop = stop;
	}

	@Override
	public void run()
	{
		while(!getStop())
		{
			uploadResults();
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException ex)
			{

			}
		}
		uploadResults();
	}

	private void uploadResults()
	{
		for(Result key : toupload.keySet())
		{
			List<LogEntry> entryList = toupload.get(key);
			List<LogEntry> logentries = null;
			synchronized(entryList)
			{
				logentries = new ArrayList<LogEntry>(entryList);
				entryList.clear();
			}
			try
			{
				resultApi.addToLog(key.getId(), logentries);
			} catch(ClientResponseFailure error)
			{
				// don't worry about infinate loops, this logger is only attached to test cases
				logger.error("Unable to upload logs to slickij", error);
				error.getResponse().releaseConnection();
				synchronized(entryList)
				{
					// push the logs back on the stack, retry
					entryList.addAll(0, logentries);
				}
			}
		}
	}


}
