package org.tcrun.plugins.basic;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestRunner;
import org.tcrun.api.plugins.AfterTestCasePlugin;
import org.tcrun.api.plugins.BeforeTestCasePlugin;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({BeforeTestCasePlugin.class, AfterTestCasePlugin.class, CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class})
public class BasicPrintStatusPlugin implements BeforeTestCasePlugin, AfterTestCasePlugin, CommandLineOptionPlugin, CommandLineConsumerPlugin
{

	//TODO: add verbose (logs)
	private boolean m_quiet_mode = false;
	private int m_test_number = 0;
	private int m_number_of_results = 0;

	public void beforeTestExecutes(TCRunContext p_context, TestRunner p_testrunner)
	{
		if(!m_quiet_mode)
		{
			System.out.print(String.format("%04d-%s: ", ++m_test_number, p_testrunner.getTestClass().getName()));
			m_number_of_results = p_context.getResultList().size();
		}
	}

	public void afterTestCase(TCRunContext p_context, TestRunner p_testrunner)
	{
		if(!m_quiet_mode)
		{
			if(p_context.getResultList().size() <= m_number_of_results)
			{
				System.out.println("????");
			} else
			{
				System.out.println(p_context.getResultList().get(m_number_of_results).getStatus());
			}
		}
	}

	public String getPluginName()
	{
		return "Basic print status plugin";
	}

	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.hasArg(false)
		                  .isRequired(false)
				  .withDescription("Turn on quiet mode (no status printing).")
				  .withLongOpt("quiet")
				  .create("q"));

	}

	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("q"))
		{

			m_quiet_mode = true;
		}
	}

}
