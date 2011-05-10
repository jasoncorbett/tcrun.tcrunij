package org.tcrun.plugins.slickij;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import eu.medsea.mimeutil.MimeUtil2;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bson.types.ObjectId;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.api.ImplementsPlugin;
import org.tcrun.api.Result;
import org.tcrun.api.TCRunContext;
import org.tcrun.api.TestCaseStep;
import org.tcrun.api.TestRunner;
import org.tcrun.api.TestWithName;
import org.tcrun.api.TestWithSteps;
import org.tcrun.api.TestWithUUID;
import org.tcrun.api.annotations.TestGroup;
import org.tcrun.api.plugins.BeforeTestCasePlugin;
import org.tcrun.api.plugins.CommandLineOptionPlugin;
import org.tcrun.api.plugins.CommandLineConsumerPlugin;
import org.tcrun.api.plugins.ResultWatcherPlugin;
import org.tcrun.api.plugins.BeforeTestListRunnerPlugin;
import org.tcrun.api.plugins.ShutdownTaskPlugin;
import org.tcrun.api.plugins.StartupError;
import org.tcrun.api.plugins.TestListRunnerPlugin;
import org.tcrun.slickij.api.ConfigurationResource;
import org.tcrun.slickij.api.ProjectResource;
import org.tcrun.slickij.api.ResultResource;
import org.tcrun.slickij.api.StoredFileResource;
import org.tcrun.slickij.api.TestcaseResource;
import org.tcrun.slickij.api.TestrunResource;
import org.tcrun.slickij.api.data.Build;
import org.tcrun.slickij.api.data.Component;
import org.tcrun.slickij.api.data.ComponentReference;
import org.tcrun.slickij.api.data.Configuration;
import org.tcrun.slickij.api.data.ConfigurationReference;
import org.tcrun.slickij.api.data.Project;
import org.tcrun.slickij.api.data.Release;
import org.tcrun.slickij.api.data.ResultStatus;
import org.tcrun.slickij.api.data.RunStatus;
import org.tcrun.slickij.api.data.Step;
import org.tcrun.slickij.api.data.StoredFile;
import org.tcrun.slickij.api.data.Testcase;
import org.tcrun.slickij.api.data.TestcaseReference;
import org.tcrun.slickij.api.data.Testrun;

/**
 *
 * @author jcorbett
 */
@ImplementsPlugin(
{
	CommandLineOptionPlugin.class, CommandLineConsumerPlugin.class, ResultWatcherPlugin.class, BeforeTestListRunnerPlugin.class, BeforeTestCasePlugin.class, ShutdownTaskPlugin.class
})
public class SlickijPlugin implements CommandLineOptionPlugin, CommandLineConsumerPlugin, ResultWatcherPlugin, BeforeTestListRunnerPlugin, BeforeTestCasePlugin, ShutdownTaskPlugin
{

	private static XLogger logger = XLoggerFactory.getXLogger(SlickijPlugin.class);
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
	private StoredFileResource filesApi = null;
	private ResultLogAppender logAppender = null;
	private Project project = null;
	private Testrun testrun = null;
	private Configuration config = null;
	private org.tcrun.slickij.api.data.Result result;
	private boolean testIsNew = false;
	private CommandLine options = null;
	private MimeUtil2 mimeDetector = null;

	public SlickijPlugin()
	{
		mimeDetector = new MimeUtil2();
		mimeDetector.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
	}

	@Override
	public String getPluginName()
	{
		return "Slickv2 Connector";
	}

	@Override
	public void addToCommandLineParser(Options options)
	{
		options.addOption(OptionBuilder.withLongOpt("slick-report").hasOptionalArg().withDescription("Post the results to slick").create());
		options.addOption(OptionBuilder.withLongOpt("slick-project-name").hasArg().withDescription("Specify the project name in slick").create());
		options.addOption(OptionBuilder.withLongOpt("slick-username").hasArg().withDescription("Provide the slick username").create());
		options.addOption(OptionBuilder.withLongOpt("slick-password").hasArg().withDescription("Provide the slick password").create());
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
		if(report && this.result != null)
		{
			if(testIsNew && TestWithSteps.class.isAssignableFrom(result.getTest().getTestRunner().getTestClass()))
			{
				List<Step> steps = new ArrayList<Step>();
				List<TestCaseStep> realSteps = ((TestWithSteps) result.getTest().getTestRunner().getTestInstance()).getTestSteps();
				for(TestCaseStep realStep : realSteps)
				{
					Step step = new Step();
					step.setName(realStep.getStepName());
					step.setExpectedResult(realStep.getStepExpectedResult());
					steps.add(step);
				}
				Testcase update = new Testcase();
				update.setSteps(steps);
				try
				{
					testcaseApi.updateTestcase(this.result.getTestcase().getTestcaseId(), update);
				} catch(ClientResponseFailure error)
				{
					logger.error("Error adding steps to testcase.", error);
					error.getResponse().releaseConnection();
				}
			}
			// add files to slick
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
						storedfile = filesApi.createStoredFile(storedfile);
						byte[] data = new byte[(int) file.length()];
						FileInputStream dataInputStream = new FileInputStream(file);
						dataInputStream.read(data);
						dataInputStream.close();
						storedfile = filesApi.setFileContent(storedfile.getObjectId(), data);
						storedfiles.add(storedfile);
					} catch(ClientResponseFailure error)
					{
						logger.error("Error adding files.", error);
						error.getResponse().releaseConnection();
					} catch(RuntimeException e)
					{
						logger.error("Error adding files.", e);
					} catch(Exception e)
					{
						logger.error("Error adding files.", e);
					}
				}
			}
			org.tcrun.slickij.api.data.Result update = new org.tcrun.slickij.api.data.Result();
			update.setStatus(ResultStatus.valueOf(result.getStatus().toString()));
			update.setRunstatus(RunStatus.FINISHED);
			update.setReason(result.getReason());
			update.setFiles(storedfiles);
			try
			{
				resultApi.updateResult(this.result.getId(), update);
			} catch(ClientResponseFailure error)
			{
				logger.error("Unable to update the result status on slick.", error);
				error.getResponse().releaseConnection();
			}
		}
	}

	@Override
	public void beforTestListRunnerExecutes(TCRunContext p_context, TestListRunnerPlugin p_testlistrunner) throws StartupError
	{
		if(report)
		{
			if(slickBaseUrl == null)
			{
				if(p_context.getTestCaseConfiguration().containsKey("Slickv2.url"))
				{
					slickBaseUrl = p_context.getTestCaseConfiguration().get("Slickv2.url");
				} else
				{
					throw new StartupError("No configuration for slick base url, please either include on command line or add configuration option Slickv2.url");
				}
			}
			if(projectName == null)
			{
				if(p_context.getTestCaseConfiguration().containsKey("Slickv2.project.name"))
				{
					projectName = p_context.getTestCaseConfiguration().get("Slickv2.project.name");
				} else
				{
					throw new StartupError("No configuration for slick project name, please either include on command line or add configuration option Slickv2.project.name");
				}
			}
			if(slickUsername == null)
			{
				if(p_context.getTestCaseConfiguration().containsKey("Slickv2.username"))
				{
					slickUsername = p_context.getTestCaseConfiguration().get("Slickv2.username");
				} else
				{
					throw new StartupError("No configuration for slick username, please either include on command line or add configuration option Slickv2.username");
				}
			}
			if(slickPassword == null)
			{
				if(p_context.getTestCaseConfiguration().containsKey("Slickv2.password"))
				{
					slickPassword = p_context.getTestCaseConfiguration().get("Slickv2.password");
				} else
				{
					throw new StartupError("No configuration for slick password, please either include on command line or add configuration option Slickv2.password");
				}
			}
			DefaultHttpClient httpclient = new DefaultHttpClient();
			httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(slickUsername, slickPassword));
			executor = new ApacheHttpClient4Executor(httpclient);
			if(!slickBaseUrl.endsWith("api") && !slickBaseUrl.endsWith("api/"))
			{
				if(slickBaseUrl.endsWith("/"))
					slickBaseUrl += "api";
				else
					slickBaseUrl += "/api";
			}

			projectApi = ProxyFactory.create(ProjectResource.class, slickBaseUrl, executor);
			configApi = ProxyFactory.create(ConfigurationResource.class, slickBaseUrl, executor);
			testrunApi = ProxyFactory.create(TestrunResource.class, slickBaseUrl, executor);
			testcaseApi = ProxyFactory.create(TestcaseResource.class, slickBaseUrl, executor);
			resultApi = ProxyFactory.create(ResultResource.class, slickBaseUrl, executor);
			filesApi = ProxyFactory.create(StoredFileResource.class, slickBaseUrl, executor);

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
				List<Configuration> configs = new ArrayList<Configuration>();
				try
				{
					configs = configApi.getMatchingConfigurations(null, "ENVIRONMENT", env + ".ini");
				} catch(ClientResponseFailure error)
				{
					logger.error("Unable to fetch list of matching configuration objects from slick v2.", error);
					error.getResponse().releaseConnection();
				}
				if(configs.size() > 0)
				{
					config = configs.get(0);
				} else
				{
					config = new Configuration();
					config.setName("Environment " + env);
					config.setConfigurationType("ENVIRONMENT");
					config.setConfigurationData(p_context.getTestCaseConfiguration());
					config.setFilename(env + ".ini");
					try
					{
						config = configApi.addNewConfiguration(config);
					} catch(ClientResponseFailure error)
					{
						config = null;
						logger.error("Unable to create configuration object in slick v2.", error);
						error.getResponse().releaseConnection();
					}
				}
			}

			testrun = new Testrun();
			String name = "TCRunIJ Run";
			if(options.hasOption("plan"))
			{
				name += " for plan " + options.getOptionValue("plan");
			} else if(options.hasOption("a"))
			{
				name += " of all available tests";
			}
			//name += " starting at " + (new Date()).toString();
			testrun.setName(name);
			if(project != null)
			{
				testrun.setProject(project.createReference());
				Release release = project.findRelease(project.getDefaultRelease());
				if(release != null)
				{
					testrun.setRelease(release.createReference());
					Build build = release.findBuild(release.getDefaultBuild());
					if(build != null)
					{
						testrun.setBuild(build.createReference());
					}
				}
			}
			if(config != null)
			{
				ConfigurationReference ref = new ConfigurationReference();
				ref.setConfigId(new ObjectId(config.getId()));
				ref.setName(config.getName());
				ref.setFilename(config.getFilename());
				testrun.setConfig(ref);
			}
			try
			{
				testrun = testrunApi.createNewTestrun(testrun);
			} catch(ClientResponseFailure error)
			{
				error.getResponse().releaseConnection();
				throw new StartupError("Unable to start test run on slick: " + error.getMessage());
			}
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

	@Override
	public void beforeTestExecutes(TCRunContext p_context, TestRunner p_testrunner)
	{
		if(report)
		{
			testIsNew = false;
			String featurename = "unknown";
			try
			{
				featurename = (new File(p_testrunner.getTestClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getName();
				Pattern endOfJarFilename = Pattern.compile("((-\\d+\\..*)?\\.jar)$");
				Matcher match = endOfJarFilename.matcher(featurename);
				if(match.find())
				{
					featurename = featurename.substring(0, match.start());
				}
			} catch(URISyntaxException ex)
			{
			}
			Component comp = project.findComponentByCode(featurename);
			if(comp == null)
			{
				comp = new Component();
				comp.setCode(featurename);
				String compname = featurename;
				compname = Character.toUpperCase(compname.charAt(0)) + compname.substring(1);
				compname = compname.replace("_", " ");
				compname = compname.replace("-", " ");
				compname = compname.replaceAll("([a-z])([A-Z])", "$1 $2");
				comp.setName(compname);
				try
				{
					comp = projectApi.addComponent(project.getId(), comp);
				} catch(ClientResponseFailure error)
				{
					logger.error("Unable to create component with name " + comp.getName() + " and code " + comp.getCode() + " on slick.", error);
					comp = null;
					error.getResponse().releaseConnection();
				}
				if(comp != null)
				{
					project.getComponents().add(comp);
				}
			}

			ComponentReference compref = new ComponentReference();
			if(comp != null)
			{
				compref.setId(comp.getObjectId());
				compref.setName(comp.getName());
				compref.setCode(comp.getCode());
			} else
			{
				compref = null;
			}

			result = new org.tcrun.slickij.api.data.Result();
			result.setTestrun(testrun.createReference());
			result.setProject(testrun.getProject());
			result.setConfig(testrun.getConfig());
			result.setRelease(testrun.getRelease());
			result.setBuild(testrun.getBuild());
			result.setStatus(ResultStatus.NO_RESULT);
			result.setRunstatus(RunStatus.RUNNING);
			result.setComponent(compref);
			String hostname = "unknown";
			try
			{
				java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
				hostname = addr.getHostName();
			} catch(UnknownHostException ex)
			{
				// do nothing
			}
			result.setHostname(hostname);

			TestcaseReference testref = new TestcaseReference();
			testref.setAutomationTool("tcrunij");
			if(TestWithUUID.class.isAssignableFrom(p_testrunner.getTestClass()))
			{
				try
				{
					TestWithUUID test = (TestWithUUID) p_testrunner.getTestInstance();
					testref.setAutomationKey(test.getTestUUID().toString());
				} catch(RuntimeException e)
				{
					// anytime we call something on the test we need to prepare for exceptions
					logger.warn("Recieved exception when trying to get uuid from test.", e);
				}
			}
			if(TestWithName.class.isAssignableFrom(p_testrunner.getTestClass()))
			{
				try
				{
					TestWithName test = (TestWithName) p_testrunner.getTestInstance();
					testref.setName(test.getTestName());
				} catch(RuntimeException e)
				{
					// anytime we call something on the test we need to prepare for exceptions
					logger.warn("Recieved exception when trying to get uuid from test.", e);
				}
			} else
			{
				String name = p_testrunner.getTestClass().getSimpleName();
				// underscores become spaces
				name = name.replace("_", " ");
				// camelCase get's spaces inbetween, ex CamelCase becomes Camel Case
				name = name.replaceAll("([a-z])([A-Z])", "$1 $2");
				testref.setName(name);
				if(!name.contains(" "))
				{
					// use the package name as the first part of the name
					name = p_testrunner.getTestClass().getPackage().getName().replace(".*\\.", "");
					// same changes as above
					name = name.replace("_", " ");
					name = name.replaceAll("([a-z])([A-Z])", "$1 $2");
					// add the class name onto the end of the name, no need for substitutions, they didn't do anything
					// before
					name = name + " - " + p_testrunner.getTestClass().getSimpleName();
				}
				testref.setName(name);

			}
			// TODO: figure out a way to get the real test case id
			testref.setAutomationId(p_testrunner.getTestClass().getName());

			result.setTestcase(testref);

			int tries = 0;
			while(result.getId() == null && ++tries <= 3)
			{
				try
				{
					result = resultApi.addResult(result);
				} catch(ClientResponseFailure error)
				{
					error.getResponse().releaseConnection();
					logger.warn("Recieved error while trying to create result, assuming I need to create the test case first.", error);
					// probably (hopefully) due to not finding the testcase.  We'll fix that
					Testcase test = new Testcase();
					test.setName(testref.getName());
					test.setAutomated(true);
					test.setAutomationId(testref.getAutomationId());
					test.setAutomationKey(testref.getAutomationKey());
					test.setAutomationTool(testref.getAutomationTool());
					test.setProject(project.createReference());
					List<String> tags = new ArrayList<String>();
					if(p_testrunner.getTestClass().isAnnotationPresent(TestGroup.class))
					{
						TestGroup annot = p_testrunner.getTestClass().getAnnotation(TestGroup.class);
						tags.addAll(Arrays.asList(annot.value()));
					}

					test.setTags(tags);
					if(!project.getTags().containsAll(tags))
					{
						List<String> tagsToAdd = new ArrayList<String>(tags);
						tagsToAdd.removeAll(project.getTags());
						try
						{
							project.setTags(projectApi.addTags(project.getId(), tagsToAdd));
						} catch(ClientResponseFailure e)
						{
							logger.error("Error adding tags to project.", e);
						}
					}

					test.setComponent(compref);
					logger.info("Creating Testcase in Slick v2 with name {} and automationId {}.", test.getName(), test.getAutomationId());
					try
					{
						test = testcaseApi.addNewTestcase(test);
					} catch(ClientResponseFailure e)
					{
						error.getResponse().releaseConnection();
						logger.error("Error creating testcase.", e);
					}
					testref = test.createReference();
					result.setTestcase(testref);
					testIsNew = true;

				}
			}
			if(tries >= 3)
			{
				logAppender.setResult(null);
				result = null;
				logger.error("Unable to create result for test with class name {}.", p_testrunner.getTestClass().getName());
			} else
			{
				logAppender.setResult(result);
			}
		}
	}

	@Override
	public void onShutdown(TCRunContext p_context)
	{
		if(report)
		{
			this.logAppender.setStop(true);
		}
	}
}
