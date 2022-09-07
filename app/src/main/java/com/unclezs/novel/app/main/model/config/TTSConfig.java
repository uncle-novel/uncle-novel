package com.unclezs.novel.app.main.model.config;

import cn.hutool.core.codec.PercentCodec;
import cn.hutool.core.net.RFC3986;
import cn.hutool.core.net.URLEncoder;
import com.unclezs.novel.analyzer.request.RequestParams;
import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * TTS配置
 *
 * @author blog.unclezs.com
 * @since 2021/5/9 9:57
 */
@Data
public class TTSConfig {
  private RequestParams params;
  private String name;

  public RequestParams getFormattedParams(String text) {
    RequestParams requestParams = this.params.copy();
    text = RFC3986.GEN_DELIMS.encode(RFC3986.GEN_DELIMS.encode(text, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    requestParams.setBody(this.params.getBody().replace("{{text}}", text));
    requestParams.setUrl(this.params.getUrl().replace("{{text}}", text));
    return requestParams;
  }
}
