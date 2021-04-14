/**
 * @author blog.unclezs.com
 * @date 2021/04/03 17:27
 */
open module com.unclezs.novel.app.framework {
  // openjfx
  requires transitive javafx.graphics;
  requires transitive javafx.controls;
  requires transitive javafx.fxml;
  requires transitive com.jfoenix;

  requires static lombok;

  requires jkeymaster;
  requires transitive java.desktop;
  requires org.slf4j;
  requires hutool.core;

  // components
  exports com.unclezs.novel.app.framework.components;
  exports com.unclezs.novel.app.framework.components.icon;
  exports com.unclezs.novel.app.framework.components.sidebar;

  exports com.unclezs.novel.app.framework.util;
  exports com.unclezs.novel.app.framework.appication;
  exports com.unclezs.novel.app.framework.annotation;
  exports com.unclezs.novel.app.framework.view;
  exports com.unclezs.novel.app.framework.hotkey;
  exports com.unclezs.novel.app.framework.i18n;
  exports com.unclezs.novel.app.framework.factory;
}
