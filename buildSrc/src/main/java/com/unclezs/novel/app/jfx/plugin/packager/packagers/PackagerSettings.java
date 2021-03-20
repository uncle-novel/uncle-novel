package com.unclezs.novel.app.jfx.plugin.packager.packagers;

import com.unclezs.novel.app.jfx.plugin.packager.model.LinuxConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.MacConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.Manifest;
import com.unclezs.novel.app.jfx.plugin.packager.utils.Platform;
import com.unclezs.novel.app.jfx.plugin.packager.model.WindowsConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Common packagers' settings
 */
public class PackagerSettings {

	protected File outputDirectory;
	protected File licenseFile;
	protected File iconFile;
	protected Boolean generateInstaller;
	protected String mainClass;
	protected String name;
	protected String displayName;
	protected String version;
	protected String description;
	protected String url;
	protected Boolean administratorRequired;
	protected String organizationName;
	protected String organizationUrl;
	protected String organizationEmail;
	protected Boolean bundleJre;
	protected Boolean customizedJre;
	protected File jrePath;
	protected File jdkPath;
	protected List<File> additionalResources;
	protected List<String> modules;
	protected List<String> additionalModules;
	protected Platform platform;
	protected String envPath;
	protected List<String> vmArgs;
	protected File runnableJar;
	protected Boolean copyDependencies;
	protected String jreDirectoryName;
	protected WindowsConfig winConfig;
	protected LinuxConfig linuxConfig;
	protected MacConfig macConfig;
	protected Boolean createTarball;
	protected Boolean createZipball;
	protected Map<String, String> extra;
	protected boolean useResourcesAsWorkingDir;
	protected File assetsDir;
	protected String classpath;
	protected String jreMinVersion;
	protected Manifest manifest;
	protected List<File> additionalModulePaths;

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public File getLicenseFile() {
		return licenseFile;
	}

	public File getIconFile() {
		return iconFile;
	}

	public Boolean getGenerateInstaller() {
		return generateInstaller;
	}

	public String getMainClass() {
		return mainClass;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public Boolean getAdministratorRequired() {
		return administratorRequired;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public String getOrganizationUrl() {
		return organizationUrl;
	}

	public String getOrganizationEmail() {
		return organizationEmail;
	}

	public Boolean getBundleJre() {
		return bundleJre;
	}

	public Boolean getCustomizedJre() {
		return customizedJre;
	}

	public File getJrePath() {
		return jrePath;
	}

	public File getJdkPath() {
		return jdkPath;
	}

	public List<File> getAdditionalResources() {
		return additionalResources;
	}

	public List<String> getModules() {
		return modules;
	}

	public List<String> getAdditionalModules() {
		return additionalModules;
	}

	public Platform getPlatform() {
		return platform;
	}

	public String getEnvPath() {
		return envPath;
	}

	public List<String> getVmArgs() {
		return vmArgs;
	}

	public File getRunnableJar() {
		return runnableJar;
	}

	public Boolean getCopyDependencies() {
		return copyDependencies;
	}

	public String getJreDirectoryName() {
		return jreDirectoryName;
	}

	public WindowsConfig getWinConfig() {
		return winConfig;
	}

	public LinuxConfig getLinuxConfig() {
		return linuxConfig;
	}

	public MacConfig getMacConfig() {
		return macConfig;
	}

	public Boolean getCreateTarball() {
		return createTarball;
	}

	public Boolean getCreateZipball() {
		return createZipball;
	}

	public Map<String, String> getExtra() {
		return extra;
	}

	public boolean isUseResourcesAsWorkingDir() {
		return useResourcesAsWorkingDir;
	}

	public File getAssetsDir() {
		return assetsDir;
	}

	public String getClasspath() {
		return classpath;
	}

	public String getJreMinVersion() {
		return jreMinVersion;
	}

	public Manifest getManifest() {
		return manifest;
	}

	public List<File> getAdditionalModulePaths() {
		return additionalModulePaths;
	}

	// fluent api

	public PackagerSettings outputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
		return this;
	}

	public PackagerSettings licenseFile(File licenseFile) {
		this.licenseFile = licenseFile;
		return this;
	}

	public PackagerSettings iconFile(File iconFile) {
		this.iconFile = iconFile;
		return this;
	}

	public PackagerSettings generateInstaller(Boolean generateInstaller) {
		this.generateInstaller = generateInstaller;
		return this;
	}

	public PackagerSettings mainClass(String mainClass) {
		this.mainClass = mainClass;
		return this;
	}

	public PackagerSettings name(String name) {
		this.name = name;
		return this;
	}

	public PackagerSettings displayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public PackagerSettings version(String version) {
		this.version = version;
		return this;
	}

	public PackagerSettings description(String description) {
		this.description = description;
		return this;
	}

	public PackagerSettings url(String url) {
		this.url = url;
		return this;
	}

	public PackagerSettings administratorRequired(Boolean administratorRequired) {
		this.administratorRequired = administratorRequired;
		return this;
	}

	public PackagerSettings organizationName(String organizationName) {
		this.organizationName = organizationName;
		return this;
	}

	public PackagerSettings organizationUrl(String organizationUrl) {
		this.organizationUrl = organizationUrl;
		return this;
	}

	public PackagerSettings organizationEmail(String organizationEmail) {
		this.organizationEmail = organizationEmail;
		return this;
	}

	public PackagerSettings bundleJre(Boolean bundleJre) {
		this.bundleJre = bundleJre;
		return this;
	}

	public PackagerSettings customizedJre(Boolean customizedJre) {
		this.customizedJre = customizedJre;
		return this;
	}

	public PackagerSettings jrePath(File jrePath) {
		this.jrePath = jrePath;
		return this;
	}

	public PackagerSettings jdkPath(File jdkPath) {
		this.jdkPath = jdkPath;
		return this;
	}

	public PackagerSettings additionalResources(List<File> additionalResources) {
		this.additionalResources = new ArrayList<>(additionalResources);
		return this;
	}

	public PackagerSettings modules(List<String> modules) {
		this.modules = new ArrayList<>(modules);
		return this;
	}

	public PackagerSettings additionalModules(List<String> additionalModules) {
		this.additionalModules = new ArrayList<>(additionalModules);
		return this;
	}

	public PackagerSettings platform(Platform platform) {
		this.platform = platform;
		return this;
	}

	public PackagerSettings envPath(String envPath) {
		this.envPath = envPath;
		return this;
	}

	public PackagerSettings vmArgs(List<String> vmArgs) {
		this.vmArgs = new ArrayList<>(vmArgs);
		return this;
	}

	public PackagerSettings runnableJar(File runnableJar) {
		this.runnableJar = runnableJar;
		return this;
	}

	public PackagerSettings copyDependencies(Boolean copyDependencies) {
		this.copyDependencies = copyDependencies;
		return this;
	}

	public PackagerSettings jreDirectoryName(String jreDirectoryName) {
		this.jreDirectoryName = jreDirectoryName;
		return this;
	}

	public PackagerSettings winConfig(WindowsConfig winConfig) {
		this.winConfig = winConfig;
		return this;
	}

	public PackagerSettings linuxConfig(LinuxConfig linuxConfig) {
		this.linuxConfig = linuxConfig;
		return this;
	}

	public PackagerSettings macConfig(MacConfig macConfig) {
		this.macConfig = macConfig;
		return this;
	}

	public PackagerSettings createTarball(Boolean createTarball) {
		this.createTarball = createTarball;
		return this;
	}

	public PackagerSettings createZipball(Boolean createZipball) {
		this.createZipball = createZipball;
		return this;
	}

	public PackagerSettings extra(Map<String, String> extra) {
		this.extra = extra;
		return this;
	}

	public PackagerSettings useResourcesAsWorkingDir(boolean useResourcesAsWorkingDir) {
		this.useResourcesAsWorkingDir = useResourcesAsWorkingDir;
		return this;
	}

	public PackagerSettings assetsDir(File assetsDir) {
		this.assetsDir = assetsDir;
		return this;
	}

	public PackagerSettings classpath(String classpath) {
		this.classpath = classpath;
		return this;
	}

	public PackagerSettings jreMinVersion(String jreMinVersion) {
		this.jreMinVersion = jreMinVersion;
		return this;
	}

	public PackagerSettings manifest(Manifest manifest) {
		this.manifest = manifest;
		return this;
	}

	public PackagerSettings additionalModulePaths(List<File> additionalModulePaths) {
		this.additionalModulePaths = additionalModulePaths;
		return this;
	}

	@Override
	public String toString() {
		return "PackagerSettings [outputDirectory=" + outputDirectory + ", licenseFile=" + licenseFile + ", iconFile="
				+ iconFile + ", generateInstaller=" + generateInstaller + ", mainClass=" + mainClass + ", name=" + name
				+ ", displayName=" + displayName + ", version=" + version + ", description=" + description + ", url="
				+ url + ", administratorRequired=" + administratorRequired + ", organizationName=" + organizationName
				+ ", organizationUrl=" + organizationUrl + ", organizationEmail=" + organizationEmail + ", bundleJre="
				+ bundleJre + ", customizedJre=" + customizedJre + ", jrePath=" + jrePath + ", jdkPath=" + jdkPath
				+ ", additionalResources=" + additionalResources + ", modules=" + modules + ", additionalModules="
				+ additionalModules + ", platform=" + platform + ", envPath=" + envPath + ", vmArgs=" + vmArgs
				+ ", runnableJar=" + runnableJar + ", copyDependencies=" + copyDependencies + ", jreDirectoryName="
				+ jreDirectoryName + ", winConfig=" + winConfig + ", linuxConfig=" + linuxConfig + ", macConfig="
				+ macConfig + ", createTarball=" + createTarball + ", createZipball=" + createZipball + ", extra="
				+ extra + ", useResourcesAsWorkingDir=" + useResourcesAsWorkingDir + ", assetsDir=" + assetsDir
				+ ", classpath=" + classpath + ", jreMinVersion=" + jreMinVersion + ", manifest=" + manifest
				+ ", additionalModulePaths=" + additionalModulePaths + "]";
	}

}
