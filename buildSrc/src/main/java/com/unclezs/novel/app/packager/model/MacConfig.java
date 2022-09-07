package com.unclezs.novel.app.packager.model;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import com.unclezs.novel.app.packager.packager.AbstractPackager;
import java.io.File;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * mac配置
 *
 * @author blog.unclezs.com
 * @since 2021/4/2 1:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MacConfig extends PlatformConfig implements Serializable {

  private static final long serialVersionUID = -2268944961932941577L;
  /**
   * 自定义PATH，可以用于指定相对于APP路径的PATH
   */
  protected String envPath;
  private File backgroundImage;
  private Integer windowWidth;
  private Integer windowHeight;
  private Integer windowX;
  private Integer windowY;
  private Integer iconSize;
  private Integer textSize;
  private Integer iconX;
  private Integer iconY;
  private Integer appsLinkIconX;
  private Integer appsLinkIconY;
  private File volumeIcon;
  private String volumeName;
  private boolean generateDmg = true;
  private boolean generatePkg = true;
  /**
   * 自定义文件夹名字 /Content/Resources/{customAppFolder}/
   * <p>
   * {customAppFolder} >
   * <p>
   * - lib
   * <p>
   * - run.jar
   */
  private String customAppFolder = "";
  private String appId;
  private String developerId = "-";
  private File entitlements;
  /**
   * 启动脚本名称
   */
  private String startScriptName = "universalJavaApplicationStub";

  /**
   * Tests Mac OS X specific config and set defaults if not specified
   *
   * @param packager Packager
   */
  public void setDefaults(AbstractPackager packager) {
    this.setWindowX(defaultIfNull(this.getWindowX(), 10));
    this.setWindowY(defaultIfNull(this.getWindowY(), 60));
    this.setWindowWidth(defaultIfNull(this.getWindowWidth(), 540));
    this.setWindowHeight(defaultIfNull(this.getWindowHeight(), 360));
    this.setIconSize(defaultIfNull(this.getIconSize(), 128));
    this.setTextSize(defaultIfNull(this.getTextSize(), 16));
    this.setIconX(defaultIfNull(this.getIconX(), 52));
    this.setIconY(defaultIfNull(this.getIconY(), 116));
    this.setAppsLinkIconX(defaultIfNull(this.getAppsLinkIconX(), 360));
    this.setAppsLinkIconY(defaultIfNull(this.getAppsLinkIconY(), 116));
    this.setAppId(defaultIfNull(this.getAppId(), packager.getMainClass()));
  }
}
