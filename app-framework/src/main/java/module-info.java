/**
 * @author blog.unclezs.com
 * @date 2021/04/03 17:27
 */
open module com.unclezs.novel.app.framework {
  // core
  requires novel.analyzer;
  // openjfx
  requires transitive javafx.graphics;
  requires transitive javafx.controls;
  requires transitive javafx.fxml;
  requires transitive java.desktop;
  requires transitive com.jfoenix;

  requires static lombok;
  requires hutool.cache;
  requires hutool.core;
  requires jkeymaster;
  requires com.google.gson;
  requires org.slf4j;

  exports com.unclezs.novel.app.framework.core;
  exports com.unclezs.novel.app.framework.components;
  exports com.unclezs.novel.app.framework.components.icon;
  exports com.unclezs.novel.app.framework.components.sidebar;
  exports com.unclezs.novel.app.framework.util;
  exports com.unclezs.novel.app.framework.executor;
  exports com.unclezs.novel.app.framework.appication;
  exports com.unclezs.novel.app.framework.annotation;
  exports com.unclezs.novel.app.framework.animation;
  exports com.unclezs.novel.app.framework.collection;
  exports com.unclezs.novel.app.framework.exception;
  exports com.unclezs.novel.app.framework.serialize;
  exports com.unclezs.novel.app.framework.support;
  exports com.unclezs.novel.app.framework.support.fonts;
  exports com.unclezs.novel.app.framework.support.hotkey;
  exports com.unclezs.novel.app.framework.components.cell;
}
