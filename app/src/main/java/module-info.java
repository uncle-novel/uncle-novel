/**
 * @author blog.unclezs.com
 * @date 2021/04/03 17:44
 */
open module com.unclezs.novel.app.main {
  // app framework
  requires com.unclezs.novel.app.framework;
  // analyzer
  requires novel.analyzer;
  requires com.google.gson;

  requires javafx.web;
  requires org.slf4j;
  requires logback.classic;
  requires logback.core;

  requires hutool.core;
  requires static lombok;
  requires freemarker;
  requires jkeymaster;

  // richtext
  requires org.fxmisc.richtext;
  requires flowless;

  exports com.unclezs.novel.app.main;
}
