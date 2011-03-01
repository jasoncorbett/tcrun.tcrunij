package org.tcrun.plugins.slickij;

import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.Result;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.ResultWatcherPlugin;
import org.tcrun.api.plugins.BeforeTestListRunnerPlugin;
import org.tcrun.api.plugins.StartupError;
import org.tcrun.api.plugins.TestListRunnerPlugin;
import org.tcrun.slickij.api.ConfigurationResource;
import org.tcrun.slickij.api.ProjectResource;
import org.tcrun.slickij.api.ResultResource;
import org.tcrun.slickij.api.TestcaseResource;
import org.tcrun.slickij.api.TestrunResource;
import org.tcrun.slickij.api.data.Configuration;
import org.tcrun.slickij.api.data.Project;
import org.tcrun.slickij.api.data.Testrun;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class, ResultWatcherPlugin.class, BeforeTestListRunnerPlugin.class})
public class SlickijPlugin implements CommandLineOptionPlugin, CommandLineConsumerPlugin, ResultWatcherPlugin, BeforeTestListRunnerPlugin
{
	private boolean report = false;
	private String slickBaseUrl = null;
	private String slickUsername = null;
	private String slickPassword = null;
	private String projectName = null;
	private ApacheHttpClient4Executor executor = null;
	
	private ProjectResource projectApi = null;
	private ConfigurationResource configApi = null;
	private TestrunResource testrunApi = null;
	private TestcaseResource testcaseApi = null;
	private ResultResource resultApi = null;

	private Project project = null;
	private Testrun testrun = null;
	private Configuration config = null;

	private CommandLine options = null;

	@Override
	public String getPluginName()
	{
		return "Slickv2 Connector";
	}

	@Override
	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.withLongOpt("slick-report")
		                 .hasOptionalArg()
						 .withDescription("Post the results to slick")
						 .create());
		options.addOption(OptionBuilder.withLongOpt("slick-project-name")
		                 .hasArg()
						 .withDescription("Specify the project name in slick")
						 .create());
		options.addOption(OptionBuilder.withLongOpt("slick-username")
		                 .hasArg()
						 .withDescription("Provide the slick username")
						 .create());
		options.addOption(OptionBuilder.withLongOpt("slick-password")
		                 .hasArg()
						 .withDescription("Provide the slick password")
						 .create());
	}

	@Override
	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("slick-report"))
		{
			report = true;
			slickBaseUrl = options.getOptionValue("slick-report");
		}

		if(options.hasOption("slick-project-name"))
		{
			projectName = options.getOptionValue("slick-project-name");
		}
		if(options.hasOption("slick-username"))
		{
			slickUsername = options.getOptionValue("slick-username");
		}
		if(options.hasOption("slick-password"))
		{
			slickPassword = options.getOptionValue("slick-password");
		}
		this.options = options;
	}

	@Override
	public void onResultFiled(Result result)
	{
	}

	@Override
	public void beforTestListRunnerExecutes(TCRunContext p_context, TestListRunnerPlugin p_testlistrunner) throws StartupError
	{
		if(report)
		{
			if(slickBaseUrl == null)
			{
				if(p_context.getTestCaseConfiguration().containsKey("Slickv2.url"))
					slickBaseUrl = p_context.getTestCaseConfiguration().get("Slickv2.url");
				else
					throw new StartupError("No configuration for slick base url, please either include on command line or add configuration option Slickv2.url");
			}
			if(projectName == null)
			{
				if(p_context.getTestCaseConfiguration().containsKey("Slickv2.project.name"))
					slickBaseUrl = p_context.getTestCaseConfiguration().get("Slickv2.project.name");
				else
					throw new StartupError("No configuration for slick project name, please either include on command line or add configuration option Slickv2.project.name");
			}
			if(slickUsername == null)
			{
				if(p_context.getTCRunConfiguration().containsKey("Slickv2.username"))
					slickUsername = p_context.getTCRunConfiguration().get("Slickv2.username");
				else
					throw new StartupError("No configuration for slick username, please either include on command line or add configuration option Slickv2.username");
			}
			if(slickPassword == null)
			{
				if(p_context.getTCRunConfiguration().containsKey("Slickv2.password"))
					slickPassword = p_context.getTCRunConfiguration().get("Slickv2.password");
				else
					throw new StartupError("No configuration for slick password, please either include on command line or add configuration option Slickv2.password");
			}
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(slickUsername, slickPassword));
			executor = new ApacheHttpClient4Executor(httpclient);

			projectApi = ProxyFactory.create(ProjectResource.class, slickBaseUrl, executor);
			configApi = ProxyFactory.create(ConfigurationResource.class, slickBaseUrl, executor);
			testrunApi = ProxyFactory.create(TestrunResource.class, slickBaseUrl, executor);
			testcaseApi = ProxyFactory.create(TestcaseResource.class, slickBaseUrl, executor);
			resultApi = ProxyFactory.create(ResultResource.class, slickBaseUrl, executor);

			try
			{
				project = projectApi.getProjectByName(projectName);
			} catch(ClientResponseFailure error)
			{
				throw new StartupError("Invalid Slickv2 Project: " + projectName);
			}

			if(options.hasOption("e"))
			{
				String env = options.getOptionValue("e");
				List<Configuration> configs = configApi.getMatchingConfigurations(null, "ENVIRONMENT", env + ".ini");
				if(configs.size() > 0)
					config = configs.get(0);
				else
				{
					config = new Configuration();
					config.setName("Environment " + env);
					config.setConfigurationType("ENVIRONMENT");
					config.setConfigurationData(p_context.getTestCaseConfiguration());
					config = configApi.addNewConfiguration(config);
				}


			}
		}
	}
}
