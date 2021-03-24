package com.unclezs.novel.app.jfx.framework.i18n.bundle;

import com.unclezs.novel.app.jfx.framework.i18n.LanguageUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Xml格式的国际化资源 中文当作key
 *
 * @author blog.unclezs.com
 * @date 2021/03/03 23:47
 */
@Slf4j
@SuppressWarnings("all")
public class XmlResourceBundle extends ResourceBundle {

  Properties props;
  @Getter
  private Enumeration<String> keys;

  public XmlResourceBundle(InputStream inputStream) throws IOException {
    props = new Properties();
    props.loadFromXML(inputStream);
    Set<String> keySet = new HashSet<>();
    for (Object keyObj : props.keySet()) {
      keySet.add(keyObj.toString());
    }
    keys = Collections.enumeration(keySet);
  }

  public XmlResourceBundle() {
    this.props = new Properties();
    this.keys = Collections.emptyEnumeration();
  }

  /**
   * 如果是中文直接返回key
   * <p>
   * 如果没有找到也直接返回key
   *
   * @param key key
   * @return 国际化值
   */
  @Override
  public Object handleGetObject(@NonNull String key) {
    if (LanguageUtils.isChineseSimple()) {
      return key;
    }
    Object result = props.get(key);
    // 找不到 返回key
    if (result == null) {
      log.warn("没有找到 key：{} 的国际化资源", key);
      return key;
    }
    return result;
  }

  /**
   * 永远存在
   *
   * @param key key
   * @return true 存在
   */
  @Override
  public boolean containsKey(String key) {
    return true;
  }

  @Override
  public void setParent(ResourceBundle parent) {
    super.setParent(parent);
  }


}
