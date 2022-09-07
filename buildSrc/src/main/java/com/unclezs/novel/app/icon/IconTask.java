package com.unclezs.novel.app.icon;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unclezs.novel.app.packager.util.VelocityUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

/**
 * 字体图标生成
 * <a href="https://www.iconfont.cn/">阿里云图标库</a>
 *
 * @author blog.unclezs.com
 * @since 2021/4/12 23:57
 */
@Getter
@Setter
public class IconTask extends DefaultTask {

  /**
   * 阿里云 Icon json图标链接
   */
  @Input
  private String url;
  /**
   * 字体ttf文件输出路径
   */
  @Input
  private String outFont;
  /**
   * 类文件输出目录，不用带包名
   */
  @Input
  private String out;
  /**
   * 包名
   */
  @Input
  private String packageName;
  /**
   * 类名
   */
  @Input
  private String className;
  @Internal
  private Map<String, String> icons;

  public IconTask() {
    setGroup(BasePlugin.BUILD_GROUP);
    setDescription("通过云端的json图标文件生成properties图标文件");
  }

  @TaskAction
  public void generates() {
    String jsonString = HttpUtil.get(url);
    JsonElement jsonElement = JsonParser.parseString(jsonString);
    JsonArray glyphs = jsonElement.getAsJsonObject().getAsJsonArray("glyphs");
    icons = new HashMap<>(glyphs.size() * 2);
    for (JsonElement element : glyphs) {
      JsonObject glyph = element.getAsJsonObject();
      String unicode = glyph.getAsJsonPrimitive("unicode").getAsString();
      String name = glyph.getAsJsonPrimitive("font_class").getAsString().replace("-", "_");
      icons.put(name, "\\u" + unicode);
      getLogger().quiet("添加图标: name: {} unicode: {}", name, unicode);
    }
    File outFile = FileUtil.file(out, packageName.replace(".", "/"), className.concat(".java"));
    VelocityUtils.render("/icon/IconFont.java.vm", outFile, this);
    // 下载ttf字体文件
    HttpUtil.downloadFile(url.replace("json", "ttf"), outFont);
  }
}
