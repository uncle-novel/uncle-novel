package com.unclezs.novel.app.main.model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.request.okhttp.OkHttpClient;
import com.unclezs.novel.analyzer.util.StringUtils;
import java.io.File;
import java.io.IOException;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * WebDav工具
 *
 * @author blog.unclezs.com
 * @date 2021/5/10 21:22
 */
@Slf4j
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
  public static final String AUTHORIZATION = "Authorization";
  private String name;
  private String username;
  private String password;
  private String url;
  private WebDav parent;

  private WebDav() {
  }

  /**
   * 创建
   *
   * @return WebDav
   */
  public static WebDav create(String name) {
    return new WebDav().setName(name);
  }

  public static WebDav createDefault() {
    return new WebDav().setName(DIR);
  }

  /**
   * 设置备份的webdav服务器链接
   *
   * @param url 链接
   * @return this
   */
  public WebDav setUrl(String url) {
    this.url = url.endsWith(StringUtils.SLASH) ? url + name : url + StringUtils.SLASH + name;
    return this;
  }

  /**
   * @param name 文件(夹)名称
   * @return this
   */
  public WebDav setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @param webDav 父级
   * @return this
   */
  public WebDav setParent(WebDav webDav) {
    this.parent = webDav;
    return this;
  }

  /**
   * @param username 用户名
   * @return this
   */
  public WebDav setUsername(String username) {
    this.username = username;
    return this;
  }

  /**
   * @param password 密码
   * @return this
   */
  public WebDav setPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * 子目录或文件
   *
   * @param name 名称
   * @return this
   */
  public WebDav child(String name) {
    return WebDav.create(name)
      .setUsername(username)
      .setPassword(password)
      .setUrl(url)
      .setParent(this);
  }


  /**
   * 判断文件是否存在
   *
   * @return true 存在
   */
  public boolean exist() {
    try {
      RequestParams params = createParams("PROPFIND");
      return Http.validate(params);
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * 创建文件夹
   */
  public void mkdir() {
    RequestParams params = createParams("MKCOL");
    content(params);
  }

  /**
   * 上传文件
   *
   * @param file 文件
   * @throws IORuntimeException 失败
   */
  public void upload(File file) {
    // 父文件夹不存在则创建
    if (parent != null && !parent.exist()) {
      parent.mkdir();
    }
    Request request = new Builder()
      .url(url)
      .addHeader(AUTHORIZATION, Credentials.basic(username, password))
      .put(RequestBody.create(null, file)).build();
    OkHttpClient client = (OkHttpClient) Http.getStaticHttpClient();
    try {
      Response response = client.getStaticHttpClient().newCall(request).execute();
      if (!response.isSuccessful()) {
        ResponseBody body = response.body();
        String result = null;
        if (body != null) {
          result = body.string();
        }
        log.error("WebDav文件上传失败:{}：{}", url, result);
        throw new IORuntimeException("文件上传失败:{}", result);
      }
    } catch (IOException e) {
      throw new IORuntimeException("文件上传失败", e);
    }
  }

  /**
   * 下载文件
   *
   * @param file 下载到的文件夹
   */
  public void download(File file) {
    if (!exist()) {
      return;
    }
    RequestParams params = RequestParams.create(url);
    params.addHeader(AUTHORIZATION, Credentials.basic(username, password));
    params.addHeader("Depth", "1");
    try {
      byte[] bytes = Http.bytes(params);
      FileUtil.writeBytes(bytes, file);
    } catch (IOException e) {
      log.error("文件下载失败：{}", url, e);
    }
  }

  private RequestParams createParams(String method) {
    RequestParams params = RequestParams.create(url);
    params.addHeader(AUTHORIZATION, Credentials.basic(username, password));
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
