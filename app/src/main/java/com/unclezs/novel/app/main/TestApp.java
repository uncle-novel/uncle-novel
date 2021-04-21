package com.unclezs.novel.app.main;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class TestApp {

  public static void main(String[] args) throws Exception {
    String res = "com/sun/javafx/scene/control/skin/modena/modena.css";
    Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
//    System.out.println(gson.toJson(new AnalyzerRule()));
    StringBuilder sb = new StringBuilder();
    sb.append("key: value");
//    sb.append("key1: value1\n");

    System.out.println(sb.toString().matches(".+?: .+"));
  }
}
