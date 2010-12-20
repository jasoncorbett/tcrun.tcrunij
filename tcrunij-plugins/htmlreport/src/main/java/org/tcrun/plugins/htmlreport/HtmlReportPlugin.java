package org.tcrun.plugins.htmlreport;

import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.MDC;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.Result;
import org.tcrun.api.TestWithName;
import org.tcrun.api.annotations.TestName;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.ResultWatcherPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin(
{
	CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class, ResultWatcherPlugin.class
})
public class HtmlReportPlugin implements CommandLineOptionPlugin, CommandLineConsumerPlugin, ResultWatcherPlugin
{

	private boolean generate_report = false;
	private Report report;
	private static XLogger logger = XLoggerFactory.getXLogger(HtmlReportPlugin.class);
	private File destdir;

	@Override
	public String getPluginName()
	{
		return "HTML Report Plugin";
	}

	@Override
	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.hasOptionalArg().withDescription("Generate a HTML Report with the results of the tests, optionally specify a title.").withLongOpt("html-report").withArgName("REPORT-TITLE").create());
	}

	@Override
	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("html-report"))
		{
			generate_report = true;
			report = new Report();
			String title = null;
			if((title = options.getOptionValue("html-report")) != null)
			{
				report.setTitle(title);
			} else
			{
				report.setTitle("Automated Test Results");
			}

			if(options.hasOption("env"))
			{
				report.setEnvironment(options.getOptionValue("env"));
			} else
			{
				report.setEnvironment("None Specified");
			}

			if(options.hasOption("plan"))
			{
				report.setPlan(options.getOptionValue("plan"));
			} else
			{
				report.setPlan("None Specified");
			}
						// copy images, html, and css to output directory
			destdir = new File("results");
			destdir = new File(destdir, MDC.get("TestRunId"));
			FileUtils.copyResourcesRecursively(this.getClass().getResource("images"), new File(destdir, "images"));
			FileUtils.copyResourcesRecursively(this.getClass().getResource("index.html"), new File(destdir, "index.html"));
			FileUtils.copyResourcesRecursively(this.getClass().getResource("results.css"), new File(destdir, "results.css"));
		}
	}

	@Override
	public void onResultFiled(Result result)
	{
		if(generate_report)
		{
			JsonResult json_result = new JsonResult();
			json_result.setId(result.getTest().getTestId());
			json_result.setResult(result.getStatus().toString());

			// it's intentional to use the '/' character instead of File.separator as http resources always use '/'
			json_result.setLog((new File(MDC.get("TestCaseDir"))).getName() + "/test.log");
			String name = json_result.getId();
			if(result.getTest().getTestRunner().getTestInstance() instanceof TestWithName)
			{
				name = ((TestWithName) result.getTest().getTestRunner().getTestInstance()).getTestName();
			} else if(result.getTest().getTestRunner().getTestClass().isAnnotationPresent(TestName.class))
			{
				name = result.getTest().getTestRunner().getTestClass().getAnnotation(TestName.class).value();
			}
			json_result.setName(name);
			report.getResults().add(json_result);

			ObjectMapper mapper = new ObjectMapper();
			try
			{
				mapper.writeValue(new File(destdir, "results.json"), report);
			} catch (IOException ex)
			{
				logger.error("Unable to generate json for html report: ", ex);
			}

		}

	}

}
