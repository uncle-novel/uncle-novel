package com.unclezs.novel.app.jfx.plugin.packager.model;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.util.ObjectUtils;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * JavaPackager Windows specific configuration
 */
@Data
public class WindowsConfig implements Serializable {
    private static final long serialVersionUID = 2106752412224694318L;
    private File icoFile;
    private HeaderType headerType;
    private String companyName;
    private String copyright;
    private String fileDescription;
    private String fileVersion;
    private String internalName;
    private String language;
    private String originalFilename;
    private String productName;
    private String productVersion;
    private String trademarks;
    private String txtFileVersion;
    private String txtProductVersion;
    private boolean disableDirPage = true;
    private boolean disableProgramGroupPage = true;
    private boolean disableFinishedPage = true;
    private boolean createDesktopIconTask = false;
    private boolean generateSetup = false;
    private boolean generateMsi = false;
    private boolean generateMsm = false;
    private String msiUpgradeCode;
    /**
     * 是否把 runnable jar 打包进exe
     */
    private boolean wrapJar = true;
    private LinkedHashMap<String, String> setupLanguages = new LinkedHashMap<>();
    private SetupMode setupMode = SetupMode.installForAllUsers;
    private WindowsSigning signing;
    private Registry registry = new Registry();


    /**
     * Tests Windows specific config and set defaults if not specified
     *
     * @param packager Packager
     */
    public void setDefaults(Packager packager) {
        this.setHeaderType(ObjectUtils.defaultIfNull(this.getHeaderType(), HeaderType.gui));
        this.setFileVersion(defaultIfBlank(this.getFileVersion(), "1.0.0.0"));
        this.setTxtFileVersion(defaultIfBlank(this.getTxtFileVersion(), "" + packager.getVersion()));
        this.setProductVersion(defaultIfBlank(this.getProductVersion(), "1.0.0.0"));
        this.setTxtProductVersion(defaultIfBlank(this.getTxtProductVersion(), "" + packager.getVersion()));
        this.setCompanyName(defaultIfBlank(this.getCompanyName(), packager.getOrganizationName()));
        this.setCopyright(defaultIfBlank(this.getCopyright(), packager.getOrganizationName()));
        this.setFileDescription(defaultIfBlank(this.getFileDescription(), packager.getDescription()));
        this.setProductName(defaultIfBlank(this.getProductName(), packager.getName()));
        this.setInternalName(defaultIfBlank(this.getInternalName(), packager.getName()));
        this.setOriginalFilename(defaultIfBlank(this.getOriginalFilename(), packager.getName() + ".exe"));
        this.setMsiUpgradeCode(defaultIfBlank(this.getMsiUpgradeCode(), UUID.randomUUID().toString()));
    }

}
