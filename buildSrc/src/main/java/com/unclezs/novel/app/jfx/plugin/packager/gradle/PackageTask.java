package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.unclezs.novel.app.jfx.plugin.packager.model.LinuxConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.MacConfig;
import com.unclezs.novel.app.jfx.plugin.packager.model.Manifest;
import com.unclezs.novel.app.jfx.plugin.packager.model.WindowsConfig;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.packagers.PackagerFactory;
import com.unclezs.novel.app.jfx.plugin.packager.util.Platform;
import groovy.lang.Closure;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.unclezs.novel.app.jfx.plugin.packager.util.ObjectUtils.defaultIfNull;


/**
 * Packaging task fro Gradle
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
@Setter
@Getter
public class PackageTask extends AbstractPackageTask {
    @Input
    @Optional
    private Platform platform;
    @Input
    @Optional
    private List<String> additionalModules;
    @Input
    @Optional
    private List<File> additionalResources;
    @Input
    @Optional
    private Boolean administratorRequired;
    @InputDirectory
    @Optional
    private File assetsDir;
    @Input
    @Optional
    private Boolean bundleJre;
    @Input
    @Optional
    private Boolean copyDependencies;
    @Input
    @Optional
    private Boolean createTar;
    @Input
    @Optional
    private Boolean createZip;
    @Input
    @Optional
    private Boolean customizedJre;
    @Input
    @Optional
    private String appDescription;
    @Input
    @Optional
    private String displayName;
    @Input
    @Optional
    private String envPath;
    @Input
    @Optional
    private Map<String, String> extra;
    @Input
    @Optional
    private Boolean generateInstaller;
    @InputFile
    @Optional
    private File iconFile;
    @InputDirectory
    @Optional
    private File jdkPath;
    @Input
    @Optional
    private String jreDirectoryName;
    @InputDirectory
    @Optional
    private File jrePath;
    @InputFile
    @Optional
    private File licenseFile;
    @Input
    @Optional
    private LinuxConfig linuxConfig;
    @Input
    @Optional
    private MacConfig macConfig;
    @Input
    @Optional
    private String mainClass;
    @Input
    @Optional
    private List<String> modules;
    @Input
    @Optional
    private String appName;
    @Input
    @Optional
    private String organizationEmail;
    @Input
    @Optional
    private String organizationName;
    @Input
    @Optional
    private String organizationUrl;
    @InputFile
    @Optional
    private File runnableJar;
    @Input
    @Optional
    private Boolean useResourcesAsWorkingDir;
    @Input
    @Optional
    private String url;
    @Input
    @Optional
    private List<String> vmArgs;
    @Input
    @Optional
    private WindowsConfig winConfig;
    @Input
    @Optional
    private String version = getProject().getVersion().toString();
    @OutputDirectory
    @Optional
    private File outputDirectory;
    @Input
    @Optional
    private String classpath;
    @Input
    @Optional
    private String jreMinVersion;
    @Input
    @Optional
    private Manifest manifest;
    @Input
    @Optional
    private List<File> additionalModulePaths;

    @Override
    protected Packager createPackager() throws Exception {
        PackagePluginExtension extension = getProject().getExtensions().getByType(PackagePluginExtension.class);
        Packager packager = PackagerFactory.createPackager(defaultIfNull(platform, extension.getPlatform()));
        BeanUtil.copyProperties(extension, packager, CopyOptions.create().ignoreNullValue());
        BeanUtil.copyProperties(this, packager, CopyOptions.create().ignoreNullValue());
        return packager;
    }

    public Manifest manifest(Closure<Manifest> closure) {
        manifest = new Manifest();
        getProject().configure(manifest, closure);
        return manifest;
    }

    public WindowsConfig winConfig(Closure<WindowsConfig> closure) {
        winConfig = new WindowsConfig();
        getProject().configure(winConfig, closure);
        return winConfig;
    }

    public MacConfig macConfig(Closure<MacConfig> closure) {
        macConfig = new MacConfig();
        getProject().configure(macConfig, closure);
        return macConfig;
    }

    public LinuxConfig linuxConfig(Closure<LinuxConfig> closure) {
        linuxConfig = new LinuxConfig();
        getProject().configure(linuxConfig, closure);
        return linuxConfig;
    }
}
