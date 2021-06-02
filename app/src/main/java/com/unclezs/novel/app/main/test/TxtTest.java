package com.unclezs.novel.app.main.test;

import cn.hutool.core.io.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.main.db.beans.TxtTocRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2021/5/9 20:15
 */
public class TxtTest {

  //默认从文件中获取数据的长度
  private final static int BUFFER_SIZE = 512 * 1024;
  //没有标题的时候，每个章节的最大长度
  private final static int MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024;

  public static void main(String[] args) throws IOException {
    String s = FileUtil.readUtf8String("G:\\coder\\self-coder\\uncle-novel-jfx\\app\\src\\main\\resources\\toc.json");
    List<TxtTocRule> rules = new ArrayList<>();
    List<TxtChapterRuleBean> json = GsonUtils.me().fromJson(s, new TypeToken<List<TxtChapterRuleBean>>() {
    }.getType());
    int order = 0;
    int id = 0;
    for (TxtChapterRuleBean e : json) {
      TxtTocRule rule = new TxtTocRule();
      rule.setRule(e.getRule());
      rule.setName(e.getName());
      order += 5;
      rule.setId(--id);
      rule.setOrder(order);
      rules.add(rule);
    }
    String s1 = new Gson().newBuilder().disableHtmlEscaping().create().toJson(rules);
    FileUtil.writeUtf8String(s1, FileUtil.file("G:\\coder\\self-coder\\uncle-novel-jfx\\app\\src\\main\\resources\\toc1.json"));
  }

}
