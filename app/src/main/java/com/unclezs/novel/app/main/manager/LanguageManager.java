package com.unclezs.novel.app.main.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 支持的语言
 *
 * @author blog.unclezs.com
 * @since 2021/4/28 11:58
 */
@UtilityClass
public class LanguageManager {

  private static final Map<String, String> LANG = new HashMap<>(16);

  static {
    LANG.put("简体中文", "zh_CN");
    LANG.put("繁体中文", "zh_TW");
    LANG.put("English", "en");
  }

  public static ObservableList<String> names() {
    return FXCollections.observableArrayList(LANG.keySet());
  }


  /**
   * 根据locale获取对应的名字
   *
   * @param locale Locale
   * @return 如：简体中文
   */
  public static String name(Locale locale) {
    String localeString = String.format("%s_%s", locale.getLanguage(), locale.getCountry());
    for (Entry<String, String> entry : LANG.entrySet()) {
      if (entry.getValue().equals(localeString)) {
        return entry.getKey();
      }
    }
    return null;
  }

  /**
   * 根据语言名字获取Locale
   *
   * @param name 名字如：English
   * @return Locale
   */
  public static Locale locale(String name) {
    String locale = LANG.get(name);
    if (locale == null) {
      return Locale.getDefault();
    }
    String[] localeSplit = locale.split("_");
    if (localeSplit.length > 1) {
      return new Locale(localeSplit[0], localeSplit[1]);
    }
    return new Locale(localeSplit[0]);
  }
}
