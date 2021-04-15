/**
 * @author blog.unclezs.com
 * @date 2021/04/03 17:44
 */
open module com.unclezs.novel.app.main {
  // app framework
  requires com.unclezs.novel.app.framework;

  requires org.slf4j;
  requires logback.classic;
  requires logback.core;

  requires hutool.core;
  requires static lombok;
  requires freemarker;
  requires jkeymaster;

  exports com.unclezs.novel.app.main;
}
