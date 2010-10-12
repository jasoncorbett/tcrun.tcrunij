package org.tcrun.plugins.basic;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.annotations.TestName;
import org.tcrun.api.plugins.AfterTestCasePlugin;
import org.tcrun.api.plugins.BeforeTestCasePlugin;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.TestListRunnerPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({TestListRunnerPlugin.class, CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class})
public class BasicListingTestListRunner implements TestListRunnerPlugin, CommandLineOptionPlugin, CommandLineConsumerPlugin
{
	private boolean listingMode = false;
	private TCRunContext m_context;
	private List<RunnableTest> tests;

	public BasicListingTestListRunner()
	{
		tests = new ArrayList<RunnableTest>();
		listingMode = false;
	}

	@Override
	public void initialize(TCRunContext p_context)
	{
		m_context = p_context;
	}

	@Override
	public int getArbitraryImportanceRating()
	{
		// we're either on or off

		if(listingMode)
			return Integer.MAX_VALUE;
		else
			return Integer.MIN_VALUE;
	}

	@Override
	public void addTests(List<RunnableTest> p_tests)
	{
		tests.addAll(p_tests);
	}

	@Override
	public List<RunnableTest> getTests()
	{
		return tests;
	}

	@Override
	public void runTests(List<BeforeTestCasePlugin> p_beforetcplugins, List<AfterTestCasePlugin> p_aftertcplugins)
	{
		int counter = 0;
		for(RunnableTest test : tests)
		{
			if(test.getTestRunner().getTestClass().isAnnotationPresent(TestName.class))
			{
				System.out.println(++counter + " - id:" + test.getTestId() + ", name:" + test.getTestRunner().getTestClass().getAnnotation(TestName.class).value());
			} else
			{
				System.out.println(++counter + " - id:" + test.getTestId());
			}
		}
		System.out.println("------------------------------------------------------------");
		System.out.println("Total: " + tests.size() + " test(s).");
	}

	@Override
	public String getPluginName()
	{
		return "Non-executing, but listing to stdout, test list runner";
	}

	@Override
	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.hasArg(false)
		                  .withLongOpt("list")
						  .withDescription("List the tests instead of running them.")
						  .create("l"));
	}

	@Override
	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("list"))
			listingMode = true;
	}
}
