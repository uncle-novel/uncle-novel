package com.unclezs.novel.app.framework.view;

import javafx.event.Event;

/**
 * @author blog.unclezs.com
 * @date 2021/4/4 22:40
 */
public interface View {
  void onCreate();

  void onCreated();

  void publish(Event event);

  void subscribe();
}
