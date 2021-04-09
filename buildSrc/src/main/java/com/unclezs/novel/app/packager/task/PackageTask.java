package com.unclezs.novel.app.packager.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import com.unclezs.novel.app.packager.Context;
import com.unclezs.novel.app.packager.PackagePlugin;
import com.unclezs.novel.app.packager.model.PackagerExtension;
import com.unclezs.novel.app.packager.model.Platform;
import com.unclezs.novel.app.packager.packager.AbstractPackager;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/**
 * Gradle 打包任务 不进行增量构建
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
@Getter
@Setter
public class PackageTask extends DefaultTask {

  @Internal
  private AbstractPackager packager;
  /**
   * 属性含义 {@link PackagerExtension}
   */
  @Input
  @Optional
  private Platform platform;
  @Input
  @Optional
  protected String mainModule;
  @Input
  @Optional
  protected String mainClass;
  @Input
  @Optional
  private List<File> resources;
  @Input
  @Optional
  private Boolean administratorRequired;
  @Input
  @Optional
  private Boolean createTar;
  @Input
  @Optional
  private Boolean createZip;
  @Input
  @Optional
  private Boolean generateInstaller;
  @InputDirectory
  @Optional
  private File jdkPath;
  @InputDirectory
  @Optional
  private File jrePath;
  @InputDirectory
  @Optional
  private File jfxPath;
  @Input
  @Optional
  private Boolean useResourcesAsWorkingDir;
  @Input
  @Optional
  private List<String> vmArgs;
  @Input
  @Optional
  private Boolean enabledLauncher;
  @Input
  @Optional
  private Boolean x64 = true;

  public PackageTask() {
    super();
    setGroup(PackagePlugin.GROUP_NAME);
    setDescription("将应用程序打包为本地Windows，Mac OS X或GNULinux可执行文件，并创建安装程序");
    getOutputs().upToDateWhen(o -> false);
  }

  /**
   * 初始化打包器
   */
  private void initPackager() {
    PackagerExtension extension = getProject().getExtensions().getByType(PackagerExtension.class);
    packager = ObjectUtil.defaultIfNull(platform, extension.getPlatform()).createPackager(extension);
    // 默认配置的值
    BeanUtil.copyProperties(extension, packager, CopyOptions.create().ignoreNullValue());
    // 任务配置的值 , 忽略父类
    CopyOptions copyOptions = CopyOptions.create().ignoreNullValue()
      .setIgnoreProperties(Arrays.stream(BeanUtil.getPropertyDescriptors(DefaultTask.class)).map(PropertyDescriptor::getName).toArray(String[]::new));
    BeanUtil.copyProperties(this, packager, copyOptions);
    Context.packager = packager;
  }

  @TaskAction
  public void doPackage() throws Exception {
    initPackager();
    // 生成应用==可执行程序
    packager.createApp();
    // 生成安装程序
    packager.generateInstallers();
    // 生成压缩包
    packager.createCompressedPackage();
  }
}
