package org.tcrun.plugins.htmlreport;

/**
 *
 * @author jcorbett
 */
public class JsonResult
{
	private String id;
	private String name;
	private String result;
	private String log;
        private String[] screenshots;
        private String[] htmlSourceFiles;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getLog()
	{
		return log;
	}

	public void setLog(String log)
	{
		this.log = log;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

        public String[] getHtmlSourceFiles()
        {

                return htmlSourceFiles;
        }

        public void setHtmlSourceFiles(String[] htmlSourceFiles)
        {
                this.htmlSourceFiles = htmlSourceFiles;
        }

        public String[] getScreenshots()
        {
                return screenshots;
        }

        public void setScreenshots(String[] screenshots)
        {
                this.screenshots = screenshots;
        }
}
