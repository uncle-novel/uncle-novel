package com.unclezs.novel.app.main.test;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.app.main.model.TTSConfig;

/**
 * @author blog.unclezs.com
 * @date 2021/5/9 9:59
 */
public class TTSTest {

  public static void main(String[] args) {
    TTSConfig config = new TTSConfig();
    RequestParams params = RequestParams.create("http://tts.baidu.com/text2audio");
    params.setBody("tex={{text}}&per=4007&cuid=baidu_speech_demo&idx=1&cod=2&lan=zh&ctp=1&pdt=160&vol=5&aue=3&pit=5&_res_tag_=audio");
    params.setMethod("POST");
    config.setParams(params);
    config.setName("台湾女声");

    System.out.println(GsonUtils.toJson(config));
  }
}
