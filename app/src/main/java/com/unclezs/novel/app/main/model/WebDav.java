package com.unclezs.novel.app.main.model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.request.okhttp.OkHttpClient;
import com.unclezs.novel.analyzer.util.StringUtils;
import java.io.File;
import java.io.IOException;
import lombok.Setter;
import lombok.ToString;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;

/**
 * WebDav工具
 *
 * @author blog.unclezs.com
 * @date 2021/5/10 21:22
 */
@ToString
public class WebDav {

  public static final String PROPS = "<a:propfind xmlns:a=\"DAV:\">\n"
    + "                    <a:prop>\n"
    + "                        <a:displayname/>\n"
    + "                        <a:resourcetype/>\n"
    + "                        <a:getcontentlength/>\n"
    + "                        <a:creationdate/>\n"
    + "                        <a:getlastmodified/>\n"
    + "                    </a:prop>\n"
    + "                </a:propfind>";
  public static final String DIR = "uncle-novel";
  @Setter
  private String username;
  @Setter
  private String password;
  private String url;
  private final String name;

  public WebDav(String dir) {
    this.name = dir;
  }

  public void setUrl(String url) {
    this.url = url.endsWith(StringUtils.SLASH) ? url + name : url + StringUtils.SLASH + name;
  }

  public boolean exist() {
    try {
      RequestParams params = createParams("PROPFIND");
      return Http.validate(params);
    } catch (IOException e) {
      return false;
    }
  }

  public void mkdir() {
    RequestParams params = createParams("MKCOL");
    content(params);
  }

  /**
   * 上传文件
   *
   * @param file 文件
   * @return true 成功
   * @throws IORuntimeException 失败
   */
  public boolean upload(File file) {
    Request request = new Builder()
      .url(url)
      .addHeader("Authorization", Credentials.basic(username, password))
      .put(RequestBody.create(null, file)).build();
    OkHttpClient client = (OkHttpClient) Http.getStaticHttpClient();
    try {
      return client.getStaticHttpClient().newCall(request).execute().isSuccessful();
    } catch (IOException e) {
      throw new IORuntimeException("文件上传失败", e);
    }
  }

  public void download(File file) {
    RequestParams params = RequestParams.create(url);
    params.addHeader("Authorization", Credentials.basic(username, password));
    params.addHeader("Depth", "1");
    try {
      byte[] bytes = Http.bytes(params);
      FileUtil.writeBytes(bytes, file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public WebDav child(String name) {
    WebDav webDav = new WebDav(name);
    webDav.setUsername(username);
    webDav.setPassword(password);
    webDav.setUrl(url);
    return webDav;
  }

  private RequestParams createParams(String method) {
    RequestParams params = RequestParams.create(url);
    params.addHeader("Authorization", Credentials.basic(username, password));
    params.addHeader("Depth", "1");
    params.setMethod(method);
    params.setMediaType("text/plain");
    params.setBody(PROPS);
    return params;
  }

  /**
   * 正文
   *
   * @param params 参数
   * @return 返回内容
   */
  private String content(RequestParams params) {
    try {
      return Http.content(params);
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }
}
