package org.tcrun.plugins.junitxmlreport;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.MDC;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.Result;
import org.tcrun.api.ResultStatus;
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
@ImplementsPlugin({CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class, BeforeTestListRunnerPlugin.class, ResultWatcherPlugin.class, ShutdownTaskPlugin.class})
public class JUnitXMLReportPlugin implements CommandLineOptionPlugin, CommandLineConsumerPlugin, BeforeTestListRunnerPlugin, ResultWatcherPlugin, ShutdownTaskPlugin
{

	private boolean generate = false;
	private Document document;
	private int tests;
	private int fail;
	private int broken;
	private int skipped;
	private int nottested;

	@Override
	public String getPluginName()
	{
		return "JUnit XML Report Plugin";
	}

	@Override
	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.hasArg(false)
		                  .withLongOpt("junit-report")
						  .create());
	}

	@Override
	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("junit-report"))
			generate = true;
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
		if(generate)
		{
			tests++;
			if(result.getStatus() == ResultStatus.FAIL)
				fail++;
			if(result.getStatus() == ResultStatus.BROKEN_TEST)
				broken++;
			if(result.getStatus() == ResultStatus.SKIPPED)
				skipped++;
			if(result.getStatus() == ResultStatus.NOT_TESTED)
				nottested++;
			if(result.getStatus() != ResultStatus.NOT_TESTED &&
		       result.getStatus() != ResultStatus.SKIPPED)
			{
				Element testcase = document.getRootElement().addElement("testcase");
				String[] parts = result.getTest().getTestId().split("#");
				testcase.addAttribute("classname", parts[0]);

				// if the id is separated by a # then the first part is likely the classname the second is either a method
				// or a data driven component
				if(parts.length > 1)
				{
					testcase.addAttribute("name", parts[1]);
				}

				if(result.getStatus() == ResultStatus.FAIL)
				{
					Element fail = testcase.addElement("failure");
					fail.addAttribute("message", result.getReason().replace("\r\n", "").replace("\n", ""));
					fail.addAttribute("type", "");
					// Add log from test case
				}

				if(result.getStatus() == ResultStatus.BROKEN_TEST)
				{
					Element error = testcase.addElement("error");
					error.addAttribute("message", result.getReason().replace("\r\n", "").replace("\n", ""));
					error.addAttribute("type", "");
					// Add log from test case
				}
			}
		}
	}

	@Override
	public void onShutdown(TCRunContext p_context)
	{
		if(generate)
		{
			document.getRootElement().addAttribute("tests", Integer.toString(tests));
			document.getRootElement().addAttribute("errors", Integer.toString(broken));
			document.getRootElement().addAttribute("failures", Integer.toString(fail));
			document.getRootElement().addAttribute("skipped", Integer.toString(skipped));
			document.getRootElement().addAttribute("nottested", Integer.toString(nottested));
			document.getRootElement().addElement("system-out").addCDATA(null);
			document.getRootElement().addElement("system-err").addCDATA(null);

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
