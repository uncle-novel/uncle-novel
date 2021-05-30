package com.unclezs.novel.app.main.test;

import cn.hutool.core.io.FileUtil;
import com.google.gson.reflect.TypeToken;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.main.model.config.TTSConfig;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2021/5/4 22:37
 */
public class BoxTest {

  public static void main(String[] args) throws SQLException, IOException {
    String s = FileUtil.readUtf8String("G:\\coder\\self-coder\\uncle-novel-jfx\\caches\\book\\9d739f8f3424434b89d1b0d05ee204f3\\28");
    System.out.println(s);
    String text = s.substring(0, 10);
    String config = FileUtil.readUtf8String("G:\\coder\\self-coder\\uncle-novel-jfx\\app\\src\\main\\resources\\tts.json");
    List<TTSConfig> ttsConfigs = GsonUtils.me().fromJson(config, new TypeToken<List<TTSConfig>>() {
    }.getType());
    for (TTSConfig ttsConfig : ttsConfigs) {
      FileUtil.writeBytes(Http.bytes(ttsConfig.getFormattedParams(text)), "G:\\coder\\self-coder\\uncle-novel-jfx\\caches\\" + ttsConfig.getName() + ".mp3");
    }
  }

}
