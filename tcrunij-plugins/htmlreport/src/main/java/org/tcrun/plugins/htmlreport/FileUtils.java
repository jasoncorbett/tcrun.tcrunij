package org.tcrun.plugins.htmlreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.lang.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class FileUtils
{
	private static XLogger logger = XLoggerFactory.getXLogger(FileUtils.class);

	public static boolean copyFile(final File toCopy, final File destFile)
	{
		try
		{
			return FileUtils.copyStream(new FileInputStream(toCopy),
			new FileOutputStream(destFile));
		} catch (final FileNotFoundException e)
		{
			logger.error("Error copying file '" + toCopy.getPath() + "' to '" + destFile.getPath() + "': ", e);
		}
		return false;
	}

	private static boolean copyFilesRecusively(final File toCopy,
	final File destDir)
	{
		assert destDir.isDirectory();

		if (!toCopy.isDirectory())
		{
			return FileUtils.copyFile(toCopy, new File(destDir, toCopy.getName()));
		} else
		{
			final File newDestDir = new File(destDir, toCopy.getName());
			if (!newDestDir.exists() && !newDestDir.mkdir())
			{
				return false;
			}
			for (final File child : toCopy.listFiles())
			{
				if (!FileUtils.copyFilesRecusively(child, newDestDir))
				{
					return false;
				}
			}
		}
		return true;
	}

	public static boolean copyJarResourcesRecursively(final File destDir,
	final JarURLConnection jarConnection) throws IOException
	{

		final JarFile jarFile = jarConnection.getJarFile();

		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();)
		{
			final JarEntry entry = e.nextElement();
			if (entry.getName().startsWith(jarConnection.getEntryName()))
			{
				final String filename = StringUtils.removeStart(entry.getName(), //
				jarConnection.getEntryName());

				final File f = new File(destDir, filename);
				if (!entry.isDirectory())
				{
					final InputStream entryInputStream = jarFile.getInputStream(entry);
					if (!FileUtils.copyStream(entryInputStream, f))
					{
						return false;
					}
					entryInputStream.close();
				} else
				{
					if (!FileUtils.ensureDirectoryExists(f))
					{
						throw new IOException("Could not create directory: "
						+ f.getAbsolutePath());
					}
				}
			}
		}
		return true;
	}

	public static boolean copyResourcesRecursively( //
	final URL originUrl, final File destination)
	{
		try
		{
			final URLConnection urlConnection = originUrl.openConnection();
			if (urlConnection instanceof JarURLConnection)
			{
				return FileUtils.copyJarResourcesRecursively(destination,
				(JarURLConnection) urlConnection);
			} else
			{
				return FileUtils.copyFilesRecusively(new File(originUrl.getPath()),
				destination);
			}
		} catch (final IOException e)
		{
			logger.error("Error copying resource from url '" + originUrl.toString() + "' to '" + destination.getPath() + "': ", e);
		}
		return false;
	}

	private static boolean copyStream(final InputStream is, final File f)
	{
		try
		{
			return FileUtils.copyStream(is, new FileOutputStream(f));
		} catch (final FileNotFoundException e)
		{
			logger.error("Error copying input stream to file '" + f.getPath() + "': ", e);
		}
		return false;
	}

	private static boolean copyStream(final InputStream is, final OutputStream os)
	{
		try
		{
			final byte[] buf = new byte[1024];

			int len = 0;
			while ((len = is.read(buf)) > 0)
			{
				os.write(buf, 0, len);
			}
			is.close();
			os.close();
			return true;
		} catch (final IOException e)
		{
			logger.error("Error copying input stream '" + is.toString() + "' to output stream '" + os.toString() + "': ", e);
		}
		return false;
	}

	private static boolean ensureDirectoryExists(final File f)
	{
		return f.exists() || f.mkdir();
	}
        public static String[] getListOfFiles(final File searchDir, final String fileExtension)
        {
            String[] stringFileList;
            File[] fileList = searchDir.listFiles(new Filter(fileExtension));
            if (fileList != null && fileList.length != 0)
            {
                    stringFileList = new String[fileList.length];
                    for (int index = 0; index < fileList.length; index++)
                    {
                        stringFileList[index] = fileList[index].toString();
                    }
            }
            else
            {
                    stringFileList = new String[0];
            }
            return stringFileList;
        }
}
