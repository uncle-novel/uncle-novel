package com.unclezs.novel.app.main.model;

import cn.hutool.core.net.URLEncoder;
import com.unclezs.novel.analyzer.request.RequestParams;
import java.nio.charset.StandardCharsets;
import lombok.Data;

/**
 * TTS配置
 *
 * @author blog.unclezs.com
 * @date 2021/5/9 9:57
 */
@Data
public class TTSConfig {

  public static final URLEncoder ENCODER = URLEncoder.createDefault();
  private RequestParams params;
  private String name;

  public RequestParams getFormattedParams(String text) {
    RequestParams requestParams = this.params.copy();
    text = ENCODER.encode(ENCODER.encode(text, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    requestParams.setBody(this.params.getBody().replace("{{text}}", text));
    requestParams.setUrl(this.params.getUrl().replace("{{text}}", text));
    return requestParams;
  }
}
