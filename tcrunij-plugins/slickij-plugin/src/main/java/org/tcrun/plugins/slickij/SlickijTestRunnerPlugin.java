package org.tcrun.plugins.slickij;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import eu.medsea.mimeutil.MimeUtil2;
import java.io.File;
import java.io.FileInputStream;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bson.types.ObjectId;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.RunnableTest;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseFilter;
import org.tcrun.api.plugins.AfterTestCasePlugin;
import org.tcrun.api.plugins.BeforeTestCasePlugin;
import org.tcrun.api.plugins.BeforeTestListRunnerPlugin;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.FilterListPlugin;
import org.tcrun.api.plugins.StartupError;
import org.tcrun.api.plugins.TestListRunnerPlugin;
import org.tcrun.slickij.api.ConfigurationResource;
import org.tcrun.slickij.api.ProjectResource;
import org.tcrun.slickij.api.ResultResource;
import org.tcrun.slickij.api.StoredFileResource;
import org.tcrun.slickij.api.TestcaseResource;
import org.tcrun.slickij.api.TestrunResource;
import org.tcrun.slickij.api.data.*;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin({CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class, TestListRunnerPlugin.class, FilterListPlugin.class, BeforeTestListRunnerPlugin.class})
public class SlickijTestRunnerPlugin implements CommandLineOptionPlugin, CommandLineConsumerPlugin, TestListRunnerPlugin, FilterListPlugin, TestCaseFilter, BeforeTestListRunnerPlugin
{
	private static XLogger s_logger = XLoggerFactory.getXLogger(SlickijTestRunnerPlugin.class);
	private TCRunContext context;
	private boolean askedToRun = false;
	private String slickBaseUrl = null;
	private String slickUsername = null;
	private String slickPassword = null;
	private Map<String, RunnableTest> testCatalog;
	private ApacheHttpClient4Executor executor = null;
	private ProjectResource projectApi = null;
	private ConfigurationResource configApi = null;
	private TestrunResource testrunApi = null;
	private TestcaseResource testcaseApi = null;
	private ResultResource resultApi = null;
	private StoredFileResource filesApi = null;
	private ResultLogAppender logAppender = null;
	private MimeUtil2 mimeDetector = null;

	private ObjectId previousTestrunId = null;
	private ObjectId previousConfigId = null;


	@Override
	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.withLongOpt("slick-run")
		                 .hasArg(true)
						 .withArgName("SLICKURL")
						 .create());
	}

	@Override
	public String getPluginName()
	{
		return "Slick Test Runner";
	}

	@Override
	public void consumeCommandLineOptions(CommandLine options)
	{
		if(options.hasOption("slick-run"))
		{
			askedToRun = true;
			slickBaseUrl = options.getOptionValue("slick-run");
		}
		if(options.hasOption("slick-username"))
		{
			slickUsername = options.getOptionValue("slick-username");
		}
		if(options.hasOption("slick-password"))
		{
			slickPassword = options.getOptionValue("slick-password");
		}
	}

	@Override
	public void initialize(TCRunContext p_context)
	{
		context = p_context;
	}

	@Override
	public int getArbitraryImportanceRating()
	{
		// we are all important if activated
		if(askedToRun)
			return Integer.MAX_VALUE;
		else
			return Integer.MIN_VALUE;
	}

	@Override
	public void addTests(List<RunnableTest> p_tests)
	{
		if(testCatalog == null)
			testCatalog = new HashMap<String, RunnableTest>();
		for(RunnableTest t : p_tests)
		{
			testCatalog.put(t.getTestId(), t);
		}
	}

	@Override
	public List<RunnableTest> getTests()
	{
		return new ArrayList<RunnableTest>(testCatalog.values());
	}

	@Override
	public void runTests(List<BeforeTestCasePlugin> p_beforetcplugins, List<AfterTestCasePlugin> p_aftertcplugins)
	{
		mimeDetector = new MimeUtil2();
		mimeDetector.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
		String hostname = "unknown";
        String envhostname = System.getenv("TCRUNIJHOSTNAME");
        if(envhostname != null && !envhostname.isEmpty())
        {
            hostname = envhostname;
        } else
        {
    		try
    		{
    			java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
    			hostname = addr.getHostName();
    		} catch(UnknownHostException ex)
    		{
    			// do nothing
    		}
        }
		TestRunParameter parameter = new TestRunParameter();
		parameter.setAutomationTool("tcrunij");
		parameter.setHostname(hostname);

		Result current = null;
		try
		{
			current = resultApi.getNextToBeRun(parameter);
		} catch(RuntimeException e)
		{
			System.out.println("Error occurred while trying to get tests to run: " + e.getMessage());
		} catch(Exception e)
		{
			System.out.println("Error occurred while trying to get tests to run: " + e.getMessage());
		}
		while(current != null)
		{
			logAppender.setResult(current);
			if(previousTestrunId == null)
				previousTestrunId = current.getTestrun().getTestrunObjectId();
			if(previousConfigId == null)
			{
				previousConfigId = current.getConfig().getConfigObjectId();
				// we need to retry
				for(int i = 0; i < 3; i++)
				{
					try
					{
						Configuration config = configApi.getConfiguration(current.getConfig().getConfigId());
						context.getTestCaseConfiguration().putAll(config.getConfigurationData());
						if(current.getConfigurationOverride() != null && current.getConfigurationOverride().size() > 0)
						{
							for(ConfigurationOverride override : current.getConfigurationOverride())
							{
								context.getTestCaseConfiguration().put(override.getKey(), override.getValue());
							}
						}
						i = 3;
						break;
					} catch(RuntimeException e)
					{
						if(i == 2)
						{
							System.out.println("Unable to retrieve configuration.");
							return;
						}
						try
						{
							Thread.sleep(500);
						} catch (InterruptedException ex)
						{
						}
					} catch(Exception e)
					{
						if(i == 2)
						{
							System.out.println("Unable to retrieve configuration.");
							return;
						}
						try
						{
							Thread.sleep(500);
						} catch (InterruptedException ex)
						{
						}
					}
				}
			}

			if(!previousTestrunId.equals(current.getTestrun().getTestrunObjectId()))
				break;
			if(!previousConfigId.equals(current.getConfig().getConfigId()))
				break;

            for(int i = 0; i < 3; i++)
            {
                try
                {
                    Testrun run = testrunApi.getTestrun(current.getTestrun().getTestrunId());
                    if(run.getState() == RunStatus.TO_BE_RUN)
                    {
                        Testrun newrun = new Testrun();
                        newrun.setState(RunStatus.RUNNING);
                        newrun.setRunStarted(new Date());
                        testrunApi.updateTestrun(current.getTestrun().getTestrunId(), newrun);
                    }
                    i = 3;
                    break;
                } catch(RuntimeException e)
                {
                    if(i == 2)
                    {
                        System.out.println("Unable to set testrun state to RUNNING.");
                        s_logger.warn("Error occurred trying to set testrun state to RUNNING: ", e);
                        return;
                    }
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException ex)
                    {
                    }
                } catch(Exception e)
                {
                    if(i == 2)
                    {
                        System.out.println("Unable to set testrun state to RUNNING.");
                        s_logger.warn("Error occurred trying to set testrun state to RUNNING: ", e);
                        return;
                    }
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException ex)
                    {
                    }
                }
            }

			RunnableTest test = testCatalog.get(current.getTestcase().getAutomationId());
			if(test == null)
			{
				// we don't have the test, log it on the server and then set the result
				System.out.println("Could not find test with id: " + current.getTestcase().getAutomationId());
				try
				{
					resultApi.deleteResult(current.getId());
                    testcaseApi.deleteTestcase(current.getTestcase().getTestcaseId());
				} catch(ClientResponseFailure error)
				{
					s_logger.error("Unable to delete the result, and the test for an test that couldn't be found.", error);
					error.getResponse().releaseConnection();
				}
				
			} else
			{
				int result_list_size = context.getResultList().size();
				// get the configuration
				Configuration config = configApi.getConfiguration(current.getConfig().getConfigId());
				test.getTestRunner().getConfiguration().putAll(config.getConfigurationData());
				if (current.getBuild() != null)
					test.getTestRunner().getConfiguration().put("build.name", current.getBuild().getName());

                if(current.getConfigurationOverride() != null && current.getConfigurationOverride().size() > 0)
                {
                    for(ConfigurationOverride override : current.getConfigurationOverride())
                    {
                        test.getTestRunner().setConfigurationValue(override.getKey(), override.getValue());
                    }
                }
				s_logger.debug("Calling '{}' before test case plugins for test with id '{}'.", p_beforetcplugins.size(), test.getTestId());
				for(BeforeTestCasePlugin plugin : p_beforetcplugins)
				{
					s_logger.debug("Calling beforeTestExecutes from plugin with class '{}' and name '{}' on test with id '{}'.", new Object[] {plugin.getClass().getName(), plugin.getPluginName(), test.getTestId()});
					plugin.beforeTestExecutes(context, test.getTestRunner());
				}
				s_logger.debug("Done calling BeforeTestCasePlugin plugins, running test '{}'.", test.getTestId());
				test.getTestRunner().runTest(context);
				s_logger.info("Test with id '{}' is now finished running.", test.getTestId());
				s_logger.debug("Calling '{}' AfterTestCasePlugin plugins for test with id '{}'.", p_aftertcplugins.size(), test.getTestId());
				for(AfterTestCasePlugin plugin: p_aftertcplugins)
				{
					s_logger.debug("Calling afterTestCase from plugin with class '{}' and name '{}' on test with id '{}'.", new Object[] {plugin.getClass().getName(), plugin.getPluginName(), test.getTestId()});
					plugin.afterTestCase(context, test.getTestRunner());
				}
				s_logger.debug("Done calling AfterTestCasePlugin plugins.");

				Result updateToCurrent = new Result();
				updateToCurrent.setId(current.getObjectId());
				updateToCurrent.setRunstatus(RunStatus.FINISHED);
				updateToCurrent.setStatus(ResultStatus.valueOf(context.getResultList().get(result_list_size).getStatus().toString()));
				updateToCurrent.setReason(context.getResultList().get(result_list_size).getReason());

				// add files
				List<StoredFile> storedfiles = new ArrayList<StoredFile>();
				File[] files = new File("./results/" + MDC.get("TestCaseDir")).listFiles();
				for(File file : files)
				{
					if(!file.getName().endsWith("test.log") && file.isFile())
					{
						try
						{
							String name = file.getName().replaceAll(".*" + File.pathSeparator, "");
							StoredFile storedfile = new StoredFile();
							storedfile.setFilename(name);
							storedfile.setMimetype(mimeDetector.getMostSpecificMimeType(mimeDetector.getMimeTypes(file)).toString());
							for(int i = 0; i < 3; i++)
							{
								try
								{
									storedfile = filesApi.createStoredFile(storedfile);
									i = 3;
									break;
								} catch(RuntimeException e)
								{
									Thread.sleep(500);
								} catch(Exception e)
								{
									Thread.sleep(500);
								}

							}
							if(storedfile.getId() == null)
								continue;
							byte[] data = new byte[(int) file.length()];
							FileInputStream dataInputStream = new FileInputStream(file);
							dataInputStream.read(data);
							dataInputStream.close();
							for(int i = 0; i < 3; i++)
							{
								try
								{
									storedfile = filesApi.setFileContent(storedfile.getObjectId(), data);
									storedfiles.add(storedfile);
									i = 3;
									break;
								} catch(RuntimeException e)
								{
									Thread.sleep(500);
								} catch(Exception e)
								{
									Thread.sleep(500);
								}
							}
						} catch(RuntimeException e)
						{
							s_logger.error("Error adding files.", e);
						} catch(Exception e)
						{
							s_logger.error("Error adding files.", e);
						}
					}
				}
				updateToCurrent.setFiles(storedfiles);
				for(int i = 0; i < 3; i++)
				{
					try
					{
						resultApi.updateResult(updateToCurrent.getId(), updateToCurrent);
						i = 3;
						break;
					} catch(ClientResponseFailure error)
					{
						s_logger.error("Unable to update the result status on slick.", error);
						error.getResponse().releaseConnection();
					} catch(RuntimeException e)
					{
						s_logger.error("Caught unknown exception while trying to update the result status on slick.", e);
						try
						{
							Thread.sleep(500);
						} catch (InterruptedException ex)
						{
						}
					} catch(Exception e)
					{
						s_logger.error("Caught unknown exception while trying to update the result status on slick.", e);
						try
						{
							Thread.sleep(500);
						} catch (InterruptedException ex)
						{
						}
					}
				}

			}
			try
			{
				current = resultApi.getNextToBeRun(parameter);
			} catch(RuntimeException e)
			{
				System.out.println("Error occurred while trying to get tests to run: " + e.getMessage());
			} catch(Exception e)
			{
				System.out.println("Error occurred while trying to get tests to run: " + e.getMessage());
			}
		}
	}

	@Override
	public List<TestCaseFilter> getFilters(TCRunContext p_context)
	{
		List<TestCaseFilter> retval = new ArrayList<TestCaseFilter>();
		if(askedToRun)
		{
			retval.add(this);
		}
		return retval;
	}

	@Override
	public void filterTests(List<RunnableTest> toberun, List<RunnableTest> available)
	{
		if(askedToRun)
			toberun.addAll(available);
	}

	@Override
	public String describeFilter()
	{
		return "Add All Tests to the SlickijTestRunnerCatalog";
	}

	@Override
	public void beforTestListRunnerExecutes(TCRunContext p_context, TestListRunnerPlugin p_testlistrunner) throws StartupError
	{
		context = p_context;
		if(askedToRun)
		{
			if(slickBaseUrl == null || "".equals(slickBaseUrl))
				throw new StartupError("You must provide the base url of your slick installation as a paramter to option slick-run.");
			if(slickUsername == null || slickPassword == null)
				throw new StartupError("You must supply a username and password for slick (--slick-username and --slick-password).");

			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(slickUsername, slickPassword));
			executor = new ApacheHttpClient4Executor(httpclient);

			projectApi = ProxyFactory.create(ProjectResource.class, slickBaseUrl, executor);
			configApi = ProxyFactory.create(ConfigurationResource.class, slickBaseUrl, executor);
			testrunApi = ProxyFactory.create(TestrunResource.class, slickBaseUrl, executor);
			testcaseApi = ProxyFactory.create(TestcaseResource.class, slickBaseUrl, executor);
			resultApi = ProxyFactory.create(ResultResource.class, slickBaseUrl, executor);
			filesApi = ProxyFactory.create(StoredFileResource.class, slickBaseUrl, executor);

			DefaultHttpClient httpclient2 = new DefaultHttpClient();
			httpclient2.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(slickUsername, slickPassword));
			ApacheHttpClient4Executor executor2 = new ApacheHttpClient4Executor(httpclient2);
			ResultResource resultApi2 = ProxyFactory.create(ResultResource.class, slickBaseUrl, executor2);
			logAppender = new ResultLogAppender(resultApi2);
			logAppender.setName("SLICK");

			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
			Logger testcase_logger = lc.getLogger("test");
			logAppender.setContext(testcase_logger.getLoggerContext());
			testcase_logger.addAppender(logAppender);
			logAppender.start();
		}
	}
}
