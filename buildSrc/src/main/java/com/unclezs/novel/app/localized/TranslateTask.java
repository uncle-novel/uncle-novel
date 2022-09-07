package com.unclezs.novel.app.localized;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/**
 * 国际化翻译Task
 *
 * @author blog.unclezs.com
 * @since 2021/4/13 14:00
 * @see <a href="https://fanyi-api.baidu.com/doc/21"></a>
 */
@Setter
@Getter
public class TranslateTask extends DefaultTask {

  public static final int MAX_CONTENT_LENT = 2000;
  public static final String ERROR_CODE = "error_code";
  public static final String ERROR_MSG = "error_msg";
  /**
   * 百度翻译接口地址
   */
  private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";
  @Input
  private String appId;
  @Input
  private String secret;
  @Internal
  private File resourceDir;
  /**
   * 语言
   */
  @Input
  @Optional
  private Map<String, String> language = Map.of("en", "en", "cht", "zh_TW");

  public TranslateTask() {
    File projectDir = getProject().getProjectDir();
    resourceDir = new File(projectDir, "src/main/resources");
  }

  @TaskAction
  public void translate() {
    Map<String, File> bundleMap = getResourceBundleFilesMap();
    for (Entry<String, File> bundle : bundleMap.entrySet()) {
      File bundleFile = bundle.getValue();
      Properties properties = properties(bundleFile);
      getLogger().quiet("开始翻译文件：{}", bundleFile);
      Map<String, Properties> langProps = new HashMap<>(language.size() * 2);
      for (String key : properties.stringPropertyNames()) {
        String value = properties.getProperty(key);
        getLogger().quiet("开始翻译：{}={}", key, value);
        // 翻译各个语种
        for (Entry<String, String> langEntry : language.entrySet()) {
          Properties langProp = langProps.computeIfAbsent(langEntry.getValue(), langStr -> properties(getResourceBundleFile(bundleFile.getParent(), bundle.getKey(), langStr)));
          // 不存在则更新
          if (langProp.getProperty(key) == null) {
            String result = translate(value, "zh", langEntry.getKey());
            getLogger().quiet("{} = {}", langEntry.getValue(), result);
            langProp.put(key, result);
          }
        }
      }
      // 持久化
      for (Entry<String, Properties> entry : langProps.entrySet()) {
        saveProperties(entry.getValue(), getResourceBundleFile(bundleFile.getParent(), bundle.getKey(), entry.getKey()));
      }
    }
  }

  /**
   * 获取国际化资源包 文件Map，只获取 {name}.properties
   * <p>
   * key: 资源包名字 value：国际化资源文件
   *
   * @return 资源包
   */
  private Map<String, File> getResourceBundleFilesMap() {
    ConfigurableFileTree files = getProject().fileTree(resourceDir);
    Map<String, File> bundleMap = new HashMap<>(16);
    files.include("**/*.properties");
    for (File file : files) {
      String fileName = file.getName().replace(".properties", "");
      String[] split = fileName.split("_");
      if (split.length == 1) {
        bundleMap.put(split[0], file);
      }
    }
    return bundleMap;
  }

  /**
   * 获取properties
   *
   * @param file properties文件
   * @return Properties
   */
  private Properties properties(File file) {
    Properties properties = new Properties();
    if (file.exists()) {
      try (BufferedInputStream inputStream = FileUtil.getInputStream(file)) {
        properties.load(inputStream);
      } catch (IOException e) {
        throw new IORuntimeException(e);
      }
    }
    return properties;
  }

  /**
   * 保持properties
   *
   * @param properties /
   * @param file       保存位置
   */
  private void saveProperties(Properties properties, File file) {
    try (BufferedOutputStream outputStream = FileUtil.getOutputStream(file)) {
      properties.store(outputStream, "Generate By Baidu Translate");
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }


  /**
   * 获取语言资源包文件
   *
   * @param parent 父目录
   * @param name   名字
   * @param lang   语言
   * @return 格式化后得语言包文件
   */
  private File getResourceBundleFile(String parent, String name, String lang) {
    return FileUtil.file(parent, String.format("%s_%s.properties", name, lang));
  }

  /**
   * 获得翻译结果
   *
   * @param query 翻译内容
   * @param from  源语言
   * @param to    目标语言
   * @return 翻译结果
   */
  public String translate(String query, String from, String to) {
    Map<String, Object> params = buildParams(query, from, to);
    //当请求翻译内容过长 用post
    String result;
    if (query.length() >= MAX_CONTENT_LENT) {
      result = HttpUtil.post(TRANS_API_HOST, params);
    } else {
      result = HttpUtil.get(TRANS_API_HOST, params);
    }
    JsonElement element = JsonParser.parseString(result);
    if (element.getAsJsonObject().get(ERROR_CODE) != null) {
      getLogger().error("翻译失败，原因：{}", element.getAsJsonObject().get(ERROR_MSG).getAsString());
      return CharSequenceUtil.EMPTY;
    } else {
      return element.getAsJsonObject().getAsJsonArray("trans_result").get(0).getAsJsonObject().get("dst").getAsString();
    }
  }

  private Map<String, Object> buildParams(String query, String from, String to) {
    Map<String, Object> params = new HashMap<>();
    params.put("q", query);
    params.put("from", from);
    params.put("to", to);
    params.put("appid", appId);
    // 随机数
    String salt = String.valueOf(System.currentTimeMillis());
    params.put("salt", salt);
    // 签名
    String src = appId + query + salt + secret;
    params.put("sign", MD5.create().digestHex(src));
    return params;
  }
}
