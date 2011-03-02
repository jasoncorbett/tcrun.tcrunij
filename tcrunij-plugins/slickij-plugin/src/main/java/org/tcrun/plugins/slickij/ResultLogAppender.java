package org.tcrun.plugins.slickij;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.tcrun.slickij.api.data.LogEntry;
import org.tcrun.slickij.api.data.LogLevel;
import org.tcrun.slickij.api.data.Result;

/**
 *
 * @author jcorbett
 */
public class ResultLogAppender extends AppenderBase<ILoggingEvent> implements Appender<ILoggingEvent>
{
	private Result result;

	@Override
	protected void append(ILoggingEvent eventObject)
	{
		if(result != null)
		{
			if(result.getLog() == null)
				result.setLog(new ArrayList<LogEntry>());
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
			result.getLog().add(entry);
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
}
