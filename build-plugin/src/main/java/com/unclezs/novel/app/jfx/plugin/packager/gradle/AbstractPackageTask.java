package com.unclezs.novel.app.jfx.plugin.packager.gradle;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

/**
 * Gradle的抽象包装任务
 *
 * @author https://github.com/fvarrui/JavaPackager
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
public abstract class AbstractPackageTask extends DefaultTask {

  @Getter
  @OutputFiles
  private final List<File> outputFiles = new ArrayList<>();

  public AbstractPackageTask() {
    super();
    setGroup(PackagePlugin.GROUP_NAME);
    setDescription("将应用程序打包为本地Windows，Mac OS X或GNULinux可执行文件，并创建安装程序");
    getOutputs().upToDateWhen(o -> false);
  }

  @TaskAction
  public void doPackage() throws Exception {
    Packager packager = createPackager();
    // 生成应用程序、安装程序和捆绑包
    File app = packager.createApp();
    List<File> installers = packager.generateInstallers();
    List<File> bundles = packager.createBundles();
    // 将生成的文件设置为输出
    outputFiles.add(app);
    outputFiles.addAll(installers);
    outputFiles.addAll(bundles);
  }

  /**
   * Creates a platform specific packager
   *
   * @return Packager
   * @throws Exception Throw if something went wrong
   */
  protected abstract Packager createPackager() throws Exception;

}
