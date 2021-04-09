package com.unclezs.novel.app.packager.model;

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
public class WinConfig extends PlatformConfig implements Serializable {

  private static final long serialVersionUID = 2106752412224694318L;
  /**
   * 是否把 runnable jar 打包进exe
   */
  private Boolean wrapJar = true;
  /**
   * version info
   */
  private String internalName;
  private String companyName;
  private String copyright;
  private String fileVersion;
  private String productVersion;
  private String fileDescription;

  /**
   * inno setup参数，需要inno setup v6+
   * <p>
   * 选择自定义安装位置
   */
  private Boolean showSelectInstallDirPage = true;
  /**
   * 显示开始菜单文件夹选择页面
   */
  private Boolean showSelectedProgramGroupPage = false;
  /**
   * 显示安装完成页面
   */
  private Boolean showFinishedPage = true;
  /**
   * 创建桌面图标
   */
  private Boolean createDesktopIconTask = true;
  /**
   * 安装的语言
   * <p>
   * https://jrsoftware.org/files/istrans/
   */
  private LinkedHashMap<String, String> setupLanguages = new LinkedHashMap<>();
  /**
   * 安装类型 installForAllUsers/installForCurrentUser
   */
  private String setupMode = "installForCurrentUser";

  private Boolean generateSetup = true;
  private Boolean generateMsi = false;
  private Boolean generateMsm = false;
  private String msiUpgradeCode = UUID.randomUUID().toString();
  private WindowsSigning signing;
}
