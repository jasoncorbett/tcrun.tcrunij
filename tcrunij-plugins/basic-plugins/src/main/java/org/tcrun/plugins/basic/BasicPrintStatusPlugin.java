package org.tcrun.plugins.basic;

import ch.qos.logback.core.joran.spi.JoranException;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseStep;
import org.tcrun.api.TestRunner;
import org.tcrun.api.TestWithSteps;
import org.tcrun.api.plugins.AfterTestCasePlugin;
import org.tcrun.api.plugins.BeforeTestCasePlugin;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import static ch.qos.logback.classic.Level.DEBUG;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.ContextInitializer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.tcrun.api.Result;
import org.tcrun.api.ResultStatus;
import org.tcrun.api.plugins.ShutdownTaskPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin(
{
	BeforeTestCasePlugin.class, AfterTestCasePlugin.class, CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class, ShutdownTaskPlugin.class
})
public class BasicPrintStatusPlugin implements BeforeTestCasePlugin, AfterTestCasePlugin, CommandLineOptionPlugin, CommandLineConsumerPlugin, ShutdownTaskPlugin
{

	//TODO: add verbose (logs)
	private boolean m_quiet_mode = false;
	private boolean m_verbose_mode = false;
	private boolean m_print_steps = false;
	private int m_test_number = 0;
	private int m_number_of_results = 0;

	public void beforeTestExecutes(TCRunContext p_context, TestRunner p_testrunner)
	{
		if(!m_quiet_mode)
		{
			System.out.print(String.format("%04d-%s: ", ++m_test_number, p_testrunner.getTestClass().getName()));
			System.out.flush();
			if(m_verbose_mode)
			{
				System.out.println();
				System.out.println("----------------------------------------------------------------------------");
			}
			m_number_of_results = p_context.getResultList().size();
		}
	}

	public void afterTestCase(TCRunContext p_context, TestRunner p_testrunner)
	{
		if(!m_quiet_mode)
		{
			if(m_verbose_mode)
			{
				System.out.print(String.format("%04d-%s: ", m_test_number, p_testrunner.getTestClass().getName()));
				System.out.flush();
			}
			if(p_context.getResultList().size() <= m_number_of_results)
			{
				System.out.println("????");
			} else
			{
				System.out.println(p_context.getResultList().get(m_number_of_results).getStatus());
			}
		}
		if(m_print_steps)
		{
			if(TestWithSteps.class.isAssignableFrom(p_testrunner.getTestClass()))
			{
				List<TestCaseStep> steps = ((TestWithSteps) p_testrunner.getTestInstance()).getTestSteps();
				if(steps.size() > 0)
				{
					for(int i = 0; i < steps.size(); i++)
					{
						TestCaseStep step = steps.get(i);
						System.out.println(String.format("Step %02d Description: %s", i + 1, step.getStepName()));
						System.out.println("        Expected Result: " + step.getStepExpectedResult());
					}
				} else
				{
					System.out.println("No test steps.");
				}
			} else
			{
				System.out.println("Not a test with steps.");
			}
		}
	}

	public String getPluginName()
	{
		return "Basic print status plugin";
	}

	public void addToCommandLineParser(Options options)
	{
		OptionGroup verbose_or_quiet = new OptionGroup();
		verbose_or_quiet.addOption(OptionBuilder.hasArg(false).isRequired(false).withDescription("Turn on quiet mode (no status printing).").withLongOpt("quiet").create("q"));
		verbose_or_quiet.addOption(OptionBuilder.hasArg(false).isRequired(false).withDescription("Turn on verbose mode (test logs printed to standard out).").withLongOpt("verbose").create("v"));
		options.addOptionGroup(verbose_or_quiet);
		options.addOption(OptionBuilder.hasArg(false).isRequired(false).withDescription("Print out the steps for all tests that have them.").withLongOpt("print-steps").create());
	}

	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("q"))
		{
			m_quiet_mode = true;
		}
		if(options.hasOption("v"))
		{
			m_verbose_mode = true;
			System.setProperty("tcrunij.verbose", "true");

			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			try
			{
				configurator.doConfigure(System.getProperty("logback.configurationFile"));
			} catch(JoranException ex)
			{
				ex.printStackTrace();
			}
		}
		if(options.hasOption("print-steps"))
		{
			m_print_steps = true;
		}
	}

	@Override
	public void onShutdown(TCRunContext p_context)
	{
		if(p_context.getResultList().size() > 0)
		{
			if(m_verbose_mode)
			{
				// reprint results
				System.out.println("----------------------------------------------------------------------------");
				int counter = 0;
				for(Result result: p_context.getResultList())
				{
					System.out.printf("%04d-%s: %s%n", m_test_number, result.getTest().getTestId(), result.getStatus().toString());
				}
			}

			if(!m_quiet_mode)
			{
				int total = p_context.getResultList().size();
				Map<ResultStatus, Integer> counts = new HashMap<ResultStatus, Integer>();
				for(Result result: p_context.getResultList())
				{
					if(counts.containsKey(result.getStatus()))
					{
						counts.put(result.getStatus(), counts.get(result.getStatus()) + 1);
					} else
					{
						counts.put(result.getStatus(), 1);
					}
				}

				System.out.println("----------------------------------------------------------------------------");
				for(ResultStatus status : counts.keySet())
				{
					System.out.printf("%s: %d%n", status.toString(), counts.get(status));
				}
				System.out.printf("Total: %d%n", total);
			}
		}
	}
}
