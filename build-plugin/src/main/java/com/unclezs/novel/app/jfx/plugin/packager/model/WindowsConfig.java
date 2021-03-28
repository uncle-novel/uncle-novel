package com.unclezs.novel.app.jfx.plugin.packager.model;

import cn.hutool.core.util.ObjectUtil;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.UUID;
import lombok.Data;

/**
 * Windows特定配置
 *
 * @author blog.unclezs.com
 * @date 2021/3/29 0:32
 */
@Data
public class WindowsConfig implements Serializable {

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

  /**
   * 设置默认值
   *
   * @param packager Packager
   */
  public void setDefaults(Packager packager) {
    this.setTxtProductVersion(ObjectUtil.defaultIfNull(this.getTxtProductVersion(), packager.getVersion()));
    this.setCompanyName(ObjectUtil.defaultIfNull(this.getCompanyName(), packager.getOrganizationName()));
    this.setCopyright(ObjectUtil.defaultIfNull(this.getCopyright(), packager.getOrganizationName()));
    this.setFileDescription(ObjectUtil.defaultIfNull(this.getFileDescription(), packager.getDescription()));
    this.setProductName(ObjectUtil.defaultIfNull(this.getProductName(), packager.getName()));
    this.setInternalName(ObjectUtil.defaultIfNull(this.getInternalName(), packager.getName()));
    this.setOriginalFilename(ObjectUtil.defaultIfNull(this.getOriginalFilename(), packager.getName().concat(".exe")));
  }

}
