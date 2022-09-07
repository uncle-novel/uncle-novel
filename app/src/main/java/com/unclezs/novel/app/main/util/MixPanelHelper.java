package com.unclezs.novel.app.main.util;

import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.app.framework.executor.Executor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSSession;

import java.util.Map;
import java.util.Map.Entry;

/**
 * MixPanel分析工具
 * <p>
 * https://mixpanel.com
 *
 * @author blog.unclezs.com
 * @since 2021/6/19 11:42
 */
@Slf4j
@UtilityClass
public class MixPanelHelper {

  public static final String NT_AUTHORITY = "NT AUTHORITY";
  public static final String TOKEN = System.getProperty("mixPanel.token");
  private static String computerName;
  private static String clientId;
  private static boolean profile = true;

  static {
    getSystemInfo();
  }

  /**
   * 发送事件
   *
   * @param name          事件名称
   * @param eventPropsMap 事件属性
   */
  public static void event(String name, Map<String, Object> eventPropsMap) {
    Executor.run(() -> sendEvent(name, eventPropsMap));
  }

  /**
   * 发送事件
   *
   * @param name 事件名称
   */
  public static void event(String name) {
    event(name, null);
  }


  /**
   * 发送事件
   *
   * @param name          事件名称
   * @param eventPropsMap 事件属性
   */
  public static void sendEvent(String name, Map<String, Object> eventPropsMap) {
    try {
      if (StringUtils.isBlank(TOKEN)) {
        return;
      }
      MixpanelAPI mixpanel = new MixpanelAPI();
      MessageBuilder messageBuilder = new MessageBuilder(TOKEN);
      JSONObject eventProps = null;
      // 封装事件属性
      if (eventPropsMap != null) {
        eventProps = new JSONObject();
        for (Entry<String, Object> entry : eventPropsMap.entrySet()) {
          eventProps.put(entry.getKey(), entry.getValue());
        }
      }
      JSONObject event = messageBuilder.event(clientId, name, eventProps);
      // 如果携带用户信息
      if (profile) {
        // 封装用户信息
        JSONObject props = new JSONObject();
        props.put("$name", computerName);
        props.put("$platform", System.getProperty("os.name", "未知"));
        JSONObject userProfile = messageBuilder.set(clientId, props);
        ClientDelivery delivery = new ClientDelivery();
        delivery.addMessage(userProfile);
        delivery.addMessage(event);
        mixpanel.deliver(delivery, true);
        profile = false;
      } else {
        mixpanel.sendMessage(event);
      }
    } catch (Exception e) {
      log.debug("mixPanel异常", e);
    }
  }

  /**
   * 获取操作系统序列号、名称
   * <p>
   * https://github.com/oshi/oshi
   */
  private static void getSystemInfo() {
    SystemInfo systemInfo = new SystemInfo();
    HardwareAbstractionLayer hardware = systemInfo.getHardware();
    ComputerSystem computerSystem = hardware.getComputerSystem();
    clientId = computerSystem.getSerialNumber();
    for (OSSession session : systemInfo.getOperatingSystem().getSessions()) {
      if (session.getLoginTime() != 0 && !NT_AUTHORITY.equals(session.getHost())) {
        computerName = session.getUserName();
        return;
      }
    }
    computerName = clientId;
  }


}
