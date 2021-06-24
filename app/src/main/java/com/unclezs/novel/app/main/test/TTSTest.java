package com.unclezs.novel.app.main.test;

import com.unclezs.novel.analyzer.util.GsonUtils;

/**
 * @author blog.unclezs.com
 * @date 2021/5/9 9:59
 */
public class TTSTest {

  public static void main(String[] args) {
    String src = "function getHtmlParas(str) {\n"
      + "\tvar sid = str.split(\"-\");\n"
      + "\tvar n = sid.length;\n"
      + "\tvar vid = sid[n - 1].split(\".\")[0];\n"
      + "\tvar pid = 0;\n"
      + "\tvid = vid - 1;\n"
      + "\treturn [pid, vid]\n"
      + "}\n"
      + "var params = getHtmlParas(url);\n"
      + "var jsUrl = utils.absUrl(url, result);\n"
      + "var dataJs = utils.get(jsUrl);\n"
      + "dataJs = dataJs.replaceAll(\",urlinfo.+?;\", \";result = VideoListJson;\");\n"
      + "var VideoListJson = eval(dataJs);\n"
      + "result = VideoListJson[params[0]][1][params[1]].split(\"$\")[1];";
    System.out.println(GsonUtils.me().toJson(src));
  }
}
