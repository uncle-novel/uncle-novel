package com.unclezs.novel.app.jfx.plugin.packager.task;

import cn.hutool.core.bean.BeanUtil;
import com.unclezs.novel.app.jfx.plugin.packager.packager.Packager;
import com.unclezs.novel.app.jfx.plugin.packager.packager.PackagerFactory;


/**
 * Gradle的默认打包任务
 *
 * @author blog.unclezs.com
 * @since 2021/03/23 19:10
 */
public class DefaultPackageTask extends AbstractPackageTask {

  @Override
  protected Packager createPackager() throws Exception {
    PackagePluginExtension extension = getProject().getExtensions().getByType(PackagePluginExtension.class);
    Packager packager = PackagerFactory.createPackager(extension.getPlatform());
    BeanUtil.copyProperties(extension, packager);
    return packager;
  }
}
