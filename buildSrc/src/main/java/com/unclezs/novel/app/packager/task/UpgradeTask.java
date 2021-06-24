package com.unclezs.novel.app.packager.task;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.unclezs.novel.app.packager.LauncherHelper;
import com.unclezs.novel.app.packager.PackagePlugin;
import com.unclezs.novel.app.packager.util.ExecUtils;
import com.unclezs.novel.app.packager.util.FileUtils;
import com.unclezs.novel.app.packager.util.Logger;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/**
 * @author blog.unclezs.com
 * @date 2021/04/02 11:49
 */
@Getter
@Setter
public class UpgradeTask extends DefaultTask {

  public static final String FILE_SCHEME = "file:";
  /**
   * 更新文件本地输出目录
   */
  @Internal
  private File outDir;
  /**
   * 不执行部署
   */
  @Input
  @Optional
  private Boolean dryRun = false;
  /**
   * scp的远程目录地址
   */
  @Input
  private String url;
  @Input
  @Optional
  private Boolean rsync = false;
  @Input
  @Optional
  private List<String> excludes = new ArrayList<>();

  public UpgradeTask() {
    setGroup(PackagePlugin.GROUP_NAME);
    setDescription("更新版本");
  }

  @TaskAction
  public void createManifest() {
    if (outDir == null) {
      outDir = new File(getProject().getBuildDir(), "upgrade");
    }
    LauncherHelper launcherHelper = new LauncherHelper(getProject(), outDir);
    launcherHelper.generate();
    deploy();
  }

  /**
   * 部署到服务器
   */
  public void deploy() {
    if (Boolean.TRUE.equals(dryRun)) {
      return;
    }
    for (String exclude : excludes) {
      FileUtil.del(FileUtil.file(outDir, exclude));
    }
    if (url.startsWith(FILE_SCHEME)) {
      File serverDir = new File(URI.create(url));
      FileUtils.del(serverDir);
      FileUtil.copyContent(outDir, serverDir, true);
    } else {
      String info;
      // rsync 增量传输
      String local = outDir.getAbsolutePath().concat("/*");
      if (Boolean.TRUE.equals(rsync)) {
        if (SystemUtil.getOsInfo().isWindows()) {
          local = String.format("/cygdrive/%s", local.replace(StrUtil.BACKSLASH, StrUtil.SLASH).replace(StrUtil.COLON, CharSequenceUtil.EMPTY));
        }
        info = ExecUtils.exec("rsync", "-avz", "--size-only", "--chmod", "755", local, url);
      } else {
        // scp 全量复制
        info = ExecUtils.exec("scp", "-r", local, url);
      }
      Logger.info(info);
    }
  }
}
