package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.PackagerFactory;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;


/**
 * Default packaging task for Gradle
 */
public class DefaultPackageTask extends AbstractPackageTask {

    @Override
    protected Packager createPackager() throws Exception {
        PackagePluginExtension extension = getProject().getExtensions().getByType(PackagePluginExtension.class);
        return (Packager) PackagerFactory.createPackager(extension.getPlatform())
            .additionalModules(extension.getAdditionalModules())
            .additionalResources(extension.getAdditionalResources())
            .administratorRequired(extension.getAdministratorRequired())
            .version(defaultIfNull(extension.getVersion(), getProject().getVersion().toString()))
            .assetsDir(extension.getAssetsDir())
            .bundleJre(extension.getBundleJre())
            .copyDependencies(extension.getCopyDependencies())
            .createTarball(extension.getCreateTarball())
            .createZipball(extension.getCreateZipball())
            .customizedJre(extension.getCustomizedJre())
            .description(extension.getDescription())
            .displayName(extension.getDisplayName())
            .envPath(extension.getEnvPath())
            .extra(extension.getExtra())
            .generateInstaller(extension.getGenerateInstaller())
            .iconFile(extension.getIconFile())
            .jdkPath(extension.getJdkPath())
            .jreDirectoryName(extension.getJreDirectoryName())
            .jreMinVersion(extension.getJreMinVersion())
            .jrePath(extension.getJrePath())
            .licenseFile(extension.getLicenseFile())
            .linuxConfig(extension.getLinuxConfig())
            .macConfig(extension.getMacConfig())
            .mainClass(extension.getMainClass())
            .modules(extension.getModules())
            .name(extension.getName())
            .organizationEmail(extension.getOrganizationEmail())
            .organizationName(extension.getOrganizationName())
            .organizationUrl(extension.getOrganizationUrl())
            .outputDirectory(extension.getOutputDirectory())
            .runnableJar(extension.getRunnableJar())
            .useResourcesAsWorkingDir(extension.isUseResourcesAsWorkingDir())
            .url(extension.getUrl())
            .vmArgs(extension.getVmArgs())
            .winConfig(extension.getWinConfig());

    }

}
