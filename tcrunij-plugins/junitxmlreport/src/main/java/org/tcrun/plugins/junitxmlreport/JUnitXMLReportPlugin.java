package org.tcrun.plugins.junitxmlreport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.MDC;
import org.tcrun.api.Result;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.BeforeTestListRunnerPlugin;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.ResultWatcherPlugin;
import org.tcrun.api.plugins.ShutdownTaskPlugin;
import org.tcrun.api.plugins.StartupError;
import org.tcrun.api.plugins.TestListRunnerPlugin;

/**
 *
 * @author jcorbett
 */
public class JUnitXMLReportPlugin implements CommandLineOptionPlugin, CommandLineConsumerPlugin, BeforeTestListRunnerPlugin, ResultWatcherPlugin, ShutdownTaskPlugin
{

	private boolean generate = false;
	private Document document;

	@Override
	public String getPluginName()
	{
		return "JUnit XML Report Plugin";
	}

	@Override
	public void addToCommandLineParser(Options options)
	{
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void consumeCommandLineOptions(CommandLine options)
	{
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void beforTestListRunnerExecutes(TCRunContext p_context, TestListRunnerPlugin p_testlistrunner) throws StartupError
	{
		if(generate)
		{
			DocumentFactory factory = new DocumentFactory();
			document = factory.createDocument(factory.createElement("testsuite"));
			Element properties = document.getRootElement().addElement("properties");
			for(String key : p_context.getTestCaseConfiguration().keySet())
			{
				properties.addElement("property").addAttribute(key, p_context.getTestCaseConfiguration().get(key));
			}
		}
	}

	@Override
	public void onResultFiled(Result result)
	{
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onShutdown(TCRunContext p_context)
	{
		if(generate)
		{
			try
			{
				File destdir = new File("results");
				destdir = new File(destdir, MDC.get("TestRunId"));
				XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());
				writer.setOutputStream(new BufferedOutputStream(new FileOutputStream(new File(destdir, "TEST-results.xml"))));
				writer.write(document);
				writer.close();
			} catch(IOException ex)
			{
			}
		}
	}
}
