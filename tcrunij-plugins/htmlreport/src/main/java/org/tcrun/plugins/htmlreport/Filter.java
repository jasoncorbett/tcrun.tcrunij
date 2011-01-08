package org.tcrun.plugins.htmlreport;

import java.io.FileFilter;
import java.io.File;

public class Filter implements FileFilter
{
   String fileExtension;

   Filter(String fileExtension)
   {
       this.fileExtension = fileExtension;
   }

   public boolean accept(File file)
   {
      return file.getName().endsWith(fileExtension);
   }
}
