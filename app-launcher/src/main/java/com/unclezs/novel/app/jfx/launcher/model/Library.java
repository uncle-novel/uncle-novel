package com.unclezs.novel.app.jfx.launcher.model;

import com.unclezs.novel.app.jfx.launcher.enums.Os;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import lombok.Data;

/**
 * @author blog.unclezs.com
 * @since 2021/03/23 13:46
 */
@Data
public class Library {

  private String path;
  private String size;
  private Os os;

  public URL toUrl(Path libDir) {
    try {
      return libDir.resolve(path).toFile().toURI().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean currentPlatform() {
    return os == null || os == Os.CURRENT;
  }
}
