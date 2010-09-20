package org.tcrun.plugins.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.TestListPlugin;
import org.tcrun.api.plugins.TestLoaderPlugin;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({TestListPlugin.class, CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class})
public class BasicTestListPlugin implements TestListPlugin, CommandLineOptionPlugin, CommandLineConsumerPlugin
{
	private static XLogger logger = XLoggerFactory.getXLogger(BasicTestListPlugin.class);

	private List<String> m_test_ids;

	public BasicTestListPlugin()
	{
		m_test_ids = new ArrayList<String>();
	}

	public List<RunnableTest> getTests(TCRunContext p_context, List<TestLoaderPlugin> p_loaders)
	{
		logger.debug("There are '{}' test loader plugins, going through each one.", p_loaders.size());
		List<RunnableTest> retval = new ArrayList<RunnableTest>();

		for(TestLoaderPlugin plugin : p_loaders)
		{
			logger.debug("Going to initialize and iterate through tests on plugin class '{}' with name '{}'.", plugin.getClass().getName(), plugin.getPluginName());
			plugin.initialize(p_context);
			for(RunnableTest test : plugin)
			{
				if(m_test_ids.contains(test.getTestId()))
				{
					logger.debug("Found test '{}' from plugin '{}'.", test.getTestId(), plugin.getPluginName());
					retval.add(test);
				}
			}
		}
		logger.debug("Returning '{}' tests.", retval.size());
		return retval;
	}

	public String getPluginName()
	{
		return "Basic Test List Plugin";
	}

	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.withLongOpt("id")
			         .isRequired(false)
				 .withDescription("Specify a test by it's id.")
				 .withArgName("ID")
				 .hasArg(true)
				 .create());
	}

	public void consumeCommandLineOptions(CommandLine options)
	{
		String[] ids = options.getOptionValues("id");
		if(ids == null || ids.length < 1)
		{
			logger.debug("There are no test ids to load.");
			return;
		}
		m_test_ids.addAll(Arrays.asList(ids));
		logger.debug("There are '{}' test ids to load.", m_test_ids.size());
	}
}
