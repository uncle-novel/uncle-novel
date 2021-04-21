package com.unclezs.novel.app.framework.bundle;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * 数据包
 *
 * @author blog.unclezs.com
 * @date 2021/03/06 17:06
 */
@Getter
@SuppressWarnings("unchecked")
public class BaseBundle {

  /**
   * 数据
   */
  protected Map<String, Object> data;


  /**
   * 添加数据
   *
   * @param key   键
   * @param value 值
   * @param <T>   类型
   * @return 值
   */
  public <T extends BaseBundle> T put(String key, Object value) {
    if (data == null) {
      data = new HashMap<>(16);
    }
    data.put(key, value);
    return (T) this;
  }

  /**
   * 获取数据
   *
   * @param key 数据key
   * @param <T> 类型
   * @return 数据
   */
  public <T> T get(String key) {
    if (data == null) {
      return null;
    }
    return (T) data.get(key);
  }
}
