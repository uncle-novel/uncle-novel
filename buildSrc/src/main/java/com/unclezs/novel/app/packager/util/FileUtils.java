package com.unclezs.novel.app.packager.util;

import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.copyFileToDirectory;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件工具
 *
 * @author blog.unclezs.com
 * @date 2021/4/6 16:41
 */
@UtilityClass
public class FileUtils {

  public static void copyFileToFile(File source, File dest) throws Exception {
    Logger.info("Copying file [" + source + "] to folder [" + dest + "]");
    try {
      copyFile(source, dest);
    } catch (IOException e) {
      throw new Exception(e.getMessage(), e);
    }
  }

  public static void copyFileToFolder(File source, File destFolder) throws Exception {
    Logger.info("Copying file [" + source + "] to folder [" + destFolder + "]");
    if (new File(destFolder, source.getName()).exists()) {
      return;
    }
    try {
      copyFileToDirectory(source, destFolder);
    } catch (IOException e) {
      throw new Exception(e.getMessage(), e);
    }
  }

  public static void concat(File dest, File... sources) throws Exception {
    Logger.info(
      "Concatenating files [" + StringUtils.join(sources, ",") + "] into file [" + dest + "]");
    try {
      FileOutputStream fos = new FileOutputStream(dest);
      for (File source : sources) {
        FileInputStream fis = new FileInputStream(source);
        IOUtils.copy(fis, fos);
        fis.close();
      }
      fos.flush();
      fos.close();
    } catch (IOException e) {
      throw new Exception("Error concatenating streams", e);
    }
  }

  private static void copyStreamToFile(InputStream is, File dest) throws Exception {
    try {
      copyInputStreamToFile(is, dest);
    } catch (IOException ex) {
      throw new Exception("Could not copy input stream to " + dest, ex);
    }
  }

  public static void copyResourceToFile(String resource, File dest, boolean unixStyleNewLines)
    throws Exception {
    copyResourceToFile(resource, dest);
    if (unixStyleNewLines) {
      try {
        processFileContent(dest, c -> c.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n"));
      } catch (IOException e) {
        throw new Exception(e.getMessage(), e);
      }
    }
  }

  public static void processFileContent(File dest, Function<String, String> function)
    throws IOException {
    String content = org.apache.commons.io.FileUtils.readFileToString(dest, StandardCharsets.UTF_8);
    content = function.apply(content);
    org.apache.commons.io.FileUtils.writeStringToFile(dest, content, StandardCharsets.UTF_8);
  }

  public static void copyResourceToFile(String resource, File dest) throws Exception {
    Logger.info("Copying resource [" + resource + "] to file [" + dest + "]");
    copyStreamToFile(FileUtils.class.getResourceAsStream(resource), dest);
  }


  public static void createSymlink(File link, File target) throws Exception {
    Logger.info("Creating symbolic link [" + link + "] to [" + target + "]");
    try {
      Files.createSymbolicLink(link.toPath(), target.toPath());
    } catch (IOException e) {
      throw new Exception("Could not create symlink " + link + " to " + target, e);
    }
  }
}
