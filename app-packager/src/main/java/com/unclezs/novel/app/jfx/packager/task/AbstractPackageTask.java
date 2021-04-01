package com.unclezs.novel.app.jfx.packager.task;

import com.unclezs.novel.app.jfx.packager.PackagePlugin;
import com.unclezs.novel.app.jfx.packager.packager.Packager;
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
@Getter
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
    // 生成应用程序
    File app = packager.createApp();
    if (app == null) {
      return;
    }
    // 生成安装程序
    List<File> installers = packager.generateInstallers();
    // 生成压缩包
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
