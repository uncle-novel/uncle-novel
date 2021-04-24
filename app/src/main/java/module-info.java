/**
 * @author blog.unclezs.com
 * @date 2021/04/03 17:44
 */
open module com.unclezs.novel.app.main {
  // openjfx
  requires javafx.web;

  // app framework
  requires com.unclezs.novel.app.framework;
  // analyzer
  requires novel.analyzer;
  requires com.google.gson;

  // hutool
  requires hutool.core;

  requires org.slf4j;
  requires logback.classic;
  requires logback.core;

  requires static lombok;
  requires freemarker;
  requires jkeymaster;

  exports com.unclezs.novel.app.main;
}
