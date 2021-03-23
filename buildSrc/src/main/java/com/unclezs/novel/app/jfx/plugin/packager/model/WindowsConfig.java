package com.unclezs.novel.app.jfx.plugin.packager.model;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.utils.ObjectUtils;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * JavaPackager Windows specific configuration
 */
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
	private boolean wrapJar = true;
	private LinkedHashMap<String, String> setupLanguages = new LinkedHashMap<>();
	private SetupMode setupMode = SetupMode.installForAllUsers;
	private WindowsSigning signing;
	private Registry registry = new Registry();

	public File getIcoFile() {
		return icoFile;
	}

	public void setIcoFile(File icoFile) {
		this.icoFile = icoFile;
	}

	public HeaderType getHeaderType() {
		return headerType;
	}

	public void setHeaderType(HeaderType headerType) {
		this.headerType = headerType;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	public String getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}

	public String getTrademarks() {
		return trademarks;
	}

	public void setTrademarks(String trademarks) {
		this.trademarks = trademarks;
	}

	public String getTxtFileVersion() {
		return txtFileVersion;
	}

	public void setTxtFileVersion(String txtFileVersion) {
		this.txtFileVersion = txtFileVersion;
	}

	public String getTxtProductVersion() {
		return txtProductVersion;
	}

	public void setTxtProductVersion(String txtProductVersion) {
		this.txtProductVersion = txtProductVersion;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public boolean isDisableDirPage() {
		return disableDirPage;
	}

	public void setDisableDirPage(boolean disableDirPage) {
		this.disableDirPage = disableDirPage;
	}

	public boolean isDisableProgramGroupPage() {
		return disableProgramGroupPage;
	}

	public void setDisableProgramGroupPage(boolean disableProgramGroupPage) {
		this.disableProgramGroupPage = disableProgramGroupPage;
	}

	public boolean isDisableFinishedPage() {
		return disableFinishedPage;
	}

	public void setDisableFinishedPage(boolean disableFinishedPage) {
		this.disableFinishedPage = disableFinishedPage;
	}

	public boolean isCreateDesktopIconTask() {
		return createDesktopIconTask;
	}

	public void setCreateDesktopIconTask(boolean createDesktopIconTask) {
		this.createDesktopIconTask = createDesktopIconTask;
	}

	public boolean isGenerateSetup() {
		return generateSetup;
	}

	public void setGenerateSetup(boolean generateSetup) {
		this.generateSetup = generateSetup;
	}

	public boolean isGenerateMsi() {
		return generateMsi;
	}

	public void setGenerateMsi(boolean generateMsi) {
		this.generateMsi = generateMsi;
	}

	public boolean isGenerateMsm() {
		return generateMsm;
	}

	public void setGenerateMsm(boolean generateMsm) {
		this.generateMsm = generateMsm;
	}

	public String getMsiUpgradeCode() {
		return msiUpgradeCode;
	}

	public void setMsiUpgradeCode(String msiUpgradeCode) {
		this.msiUpgradeCode = msiUpgradeCode;
	}

	public boolean isWrapJar() {
		return wrapJar;
	}

	public void setWrapJar(boolean wrapJar) {
		this.wrapJar = wrapJar;
	}

	public LinkedHashMap<String, String> getSetupLanguages() {
		return setupLanguages;
	}

	public void setSetupLanguages(LinkedHashMap<String, String> setupLanguages) {
		this.setupLanguages = setupLanguages;
	}

	public SetupMode getSetupMode() {
		return setupMode;
	}

	public void setSetupMode(SetupMode setupMode) {
		this.setupMode = setupMode;
	}

	public WindowsSigning getSigning() {
		return signing;
	}

	public void setSigning(WindowsSigning signing) {
		this.signing = signing;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Override
	public String toString() {
		return "WindowsConfig [icoFile=" + icoFile + ", headerType=" + headerType + ", companyName=" + companyName
				+ ", copyright=" + copyright + ", fileDescription=" + fileDescription + ", fileVersion=" + fileVersion
				+ ", internalName=" + internalName + ", language=" + language + ", originalFilename=" + originalFilename
				+ ", productName=" + productName + ", productVersion=" + productVersion + ", trademarks=" + trademarks
				+ ", txtFileVersion=" + txtFileVersion + ", txtProductVersion=" + txtProductVersion
				+ ", disableDirPage=" + disableDirPage + ", disableProgramGroupPage=" + disableProgramGroupPage
				+ ", disableFinishedPage=" + disableFinishedPage + ", createDesktopIconTask=" + createDesktopIconTask
				+ ", generateSetup=" + generateSetup + ", generateMsi=" + generateMsi + ", generateMsm=" + generateMsm
				+ ", msiUpgradeCode=" + msiUpgradeCode + ", wrapJar=" + wrapJar + ", setupLanguages=" + setupLanguages
				+ ", setupMode=" + setupMode + ", signing=" + signing + ", registry=" + registry + "]";
	}

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
