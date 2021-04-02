package com.unclezs.novel.app.jfx.packager.model;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Windows特定配置
 *
 * @author blog.unclezs.com
 * @date 2021/3/29 0:32
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WindowsConfig extends PlatformConfig implements Serializable {

  private static final long serialVersionUID = 2106752412224694318L;
  private File icoFile;
  private HeaderType headerType = HeaderType.gui;
  private String companyName;
  private String copyright;
  private String fileVersion = "1.0";
  private String fileDescription = fileVersion;
  private String txtFileVersion = fileVersion;
  private String txtProductVersion = fileVersion;
  private String productVersion = fileVersion;
  private String internalName;
  private String language;
  private String originalFilename;
  private String productName;
  private String trademarks;
  private boolean disableDirPage = true;
  private boolean disableProgramGroupPage = true;
  private boolean disableFinishedPage = true;
  private boolean createDesktopIconTask = false;
  private boolean generateSetup = false;
  private boolean generateMsi = false;
  private boolean generateMsm = false;
  private String msiUpgradeCode = UUID.randomUUID().toString();
  /**
   * 是否把 runnable jar 打包进exe
   */
  private boolean wrapJar = true;
  private LinkedHashMap<String, String> setupLanguages = new LinkedHashMap<>();
  private SetupMode setupMode = SetupMode.installForAllUsers;
  private WindowsSigning signing;
  private Registry registry = new Registry();
}
