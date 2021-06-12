package com.unclezs.novel.app.main.util;

import cn.hutool.core.util.StrUtil;
import com.jfoenix.utils.JFXUtilities;
import com.unclezs.novel.app.framework.components.ModalBox;
import com.unclezs.novel.app.framework.executor.Executor;
import com.unclezs.novel.app.main.manager.SettingManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;

/**
 * @author blog.unclezs.com
 * @date 2021/6/12 21:04
 */
@UtilityClass
public class UpdateUtils {

  /**
   * jfx-launcher更新参数
   */
  public static final String CHANGE_LOG_KEY = "changeLog";
  public static final String VERSION_KEY = "version";
  public static final String HAS_NEW = "hasNew";


  /**
   * 检测更新，有新版本则显示更新内容
   *
   * @param stage 舞台
   */
  @SuppressWarnings("unchecked")
  public void checkForUpdate(Stage stage) {
    Executor.run(() -> {
      Object userData = stage.getUserData();
      if (userData == null) {
        return;
      }
      Map<String, Object> data = (Map<String, Object>) userData;
      Boolean hasNew = (Boolean) data.getOrDefault(HAS_NEW, false);
      // 更新日志
      List<String> changeLog = (List<String>) data.get(CHANGE_LOG_KEY);
      StringBuilder whatNew = new StringBuilder();
      for (String newItem : changeLog) {
        whatNew.append(newItem).append(StrUtil.LF);
      }
      // 版本信息
      String version = (String) data.get(VERSION_KEY);
      SettingManager.manager().setVersion(version);
      if (!Boolean.TRUE.equals(hasNew)) {
        return;
      }
      Executor.run(() -> JFXUtilities.runInFX(() -> ModalBox.none().cancel("了解了").message(whatNew.toString()).title("更新内容 - V".concat(version)).show()), 1000);
    });
  }

  /**
   * 模拟更新
   *
   * @param stage 舞台
   */
  public void mockUpdate(Stage stage) {
    Map<String, Object> map = new HashMap<>(5);
    map.put(CHANGE_LOG_KEY, List.of("我是谁呢", "你又是谁呢"));
    map.put(VERSION_KEY, "5.5.54");
    map.put(HAS_NEW, true);
    stage.setUserData(map);
  }
}
