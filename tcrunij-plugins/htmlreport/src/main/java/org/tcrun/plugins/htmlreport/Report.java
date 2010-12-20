package org.tcrun.plugins.htmlreport;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jcorbett
 */
public class Report
{
	private String title;
	private String plan;
	private String environment;
	private List<JsonResult> results;

	public Report(String title, String plan, String environment)
	{
		this.title = title;
		this.plan = plan;
		this.environment = environment;
		results = new ArrayList<JsonResult>();
	}

	public Report()
	{
		this.results = new ArrayList<JsonResult>();
	}

	public String getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}

	public String getPlan()
	{
		return plan;
	}

	public void setPlan(String plan)
	{
		this.plan = plan;
	}

	public List<JsonResult> getResults()
	{
		return results;
	}

	public void setResults(List<JsonResult> results)
	{
		this.results = results;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
}
