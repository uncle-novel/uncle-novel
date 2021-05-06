import com.unclezs.novel.app.main.spi.WebEngineHttpClient;

/**
 * @author blog.unclezs.com
 * @date 2021/04/03 17:44
 */
open module com.unclezs.novel.app.main {
  // openjfx
  requires javafx.web;
  requires javafx.media;

  // app framework
  requires com.unclezs.novel.app.framework;
  // analyzer
  requires novel.analyzer;
  requires com.google.gson;

  // hutool
  requires hutool.core;

  requires jsoup;

  requires org.slf4j;
  requires logback.classic;
  requires logback.core;

  requires ormlite.core;
  requires ormlite.jdbc;
  requires sqlite.jdbc;

  requires static lombok;
  requires velocity.engine.core;
  requires jkeymaster;
  provides com.unclezs.novel.analyzer.request.spi.HttpProvider with WebEngineHttpClient;

  exports com.unclezs.novel.app.main;
  exports com.unclezs.novel.app.main.model to ormlite.core;
}

// compiler
// --add-exports=javafx.graphics/com.sun.javafx.css=com.unclezs.novel.app.framework
// --add-exports=javafx.graphics/com.sun.javafx.stage=com.unclezs.novel.app.main,com.jfoenix
// --add-exports=javafx.graphics/com.sun.javafx.scene=com.jfoenix
// --add-exports=javafx.base/com.sun.javafx.binding=com.jfoenix
// --add-exports=javafx.base/com.sun.javafx.event=com.jfoenix
// --add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=com.jfoenix
// --add-exports=javafx.controls/com.sun.javafx.scene.control=com.jfoenix

// runtime
// --add-opens=com.jfoenix/com.jfoenix.controls=com.unclezs.novel.app.framework
