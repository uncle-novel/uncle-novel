package com.unclezs.novel.app.packager.model;

import java.io.File;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * linux 配置
 *
 * @author blog.unclezs.com
 * @date 2021/4/2 1:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LinuxConfig extends PlatformConfig implements Serializable {

  private static final long serialVersionUID = -1238166997019141904L;
  private boolean generateDeb = true;
  private boolean generateRpm = true;
  private File pngFile;
  private File xpmFile;
}
