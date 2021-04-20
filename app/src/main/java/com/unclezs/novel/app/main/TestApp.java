package com.unclezs.novel.app.main;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;

/**
 * @author blog.unclezs.com
 * @since 2021/02/25 13:50
 */
public class TestApp {

  public static void main(String[] args) throws Exception {
    String res = "com/sun/javafx/scene/control/skin/modena/modena.css";
    Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    System.out.println(gson.toJson(new AnalyzerRule()));
  }
}
