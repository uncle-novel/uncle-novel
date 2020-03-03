package com.unclezs.crawl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unclezs.constrant.Charsets;
import com.unclezs.constrant.Patterns;
import com.unclezs.model.AnalysisConfig;
import com.unclezs.model.AudioBook;
import com.unclezs.model.AudioChapter;
import com.unclezs.utils.Config;
import com.unclezs.utils.HtmlUtil;
import com.unclezs.utils.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *@author unclezs.com
 *@date 2019.06.22 00:16
 */
public class AudioNovelSpider {
    private final static Properties conf = new Properties();
    private boolean isPhone = false;

    static {
        try {//加载配置
            conf.load(AudioNovelSpider.class.getResourceAsStream("/conf/audio.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPhone(boolean phone) {
        this.isPhone = phone;
    }

    /**
     * 根据名字爬取指定网站搜索结果
     *
     * @param key /
     * @param site /
     * @return
     */
    public List<AudioBook> searchBook(String key, String site, String keyType) {
        String keyWord = key;//防止编码后无法识别
        List<AudioBook> list = new ArrayList<>();
        try {
            String tmpImg = "";
            if (site.equals("ysts8") || site.equals("ysxs8") || site.equals("audio699") || site.equals("tingchina")) {//无图处理
                tmpImg = new NovelSpider(new AnalysisConfig()).crawlDescImage(key);
                //有声听书吧关键字URL编码
                key = URLEncoder.encode(key, conf.getProperty(site + "_charset"));
            }
            //抓取解析
            String html;
            if (conf.getProperty(site + "_method").equals("get")) {//get
                html = HtmlUtil.getHtml(conf.getProperty(site + "_searchUrl") + key, conf.getProperty(site + "_charset"));
            } else {//post方式
                String url = conf.getProperty(site + "_searchUrl");
                HttpRequest request = cn.hutool.http.HttpUtil.createPost(url)
                        .header("Referer", url)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36")
                        .form(conf.getProperty(site + "_searchKey"), key);
                html = request.execute().body();
            }
            Document document = Jsoup.parse(html);
            document.setBaseUri(conf.getProperty(site + "_searchUrl"));//设置根地址
            Elements lis = document.select(conf.getProperty(site + "_list"));//结果列表
            for (Element li : lis) {
                //标题
                String title;
                Element elementTitle = li.select(conf.getProperty(site + "_title")).first();
                if (site.equals("ting55") || site.equals("ting22")) {//恋听网标题特殊处理
                    title = elementTitle.text();
                } else {
                    title = elementTitle.ownText();
                }
                //图片
                String img = "";
                //作者
                String author = "";
                if (!site.equals("ysts8") && !site.equals("tingchina")) {
                    img = li.select(conf.getProperty(site + "_img")).first().absUrl("src");
                    author = li.select(conf.getProperty(site + "_author").split(",")[0])//获取选择器
                            .get(Integer.parseInt(conf.getProperty(site + "_author").split(",")[1]))//第几个标签
                            .text();
                }
                //播音
                String speak = "";
                if (!site.equals("tingchina")) {
                    speak = li.select(conf.getProperty(site + "_speak").split(",")[0])
                            .get(Integer.parseInt(conf.getProperty(site + "_speak").split(",")[1]))
                            .text();
                }

                //目录地址
                String homeUrl = li.select(conf.getProperty(site + "_url")).first().absUrl("href");
                //特殊处理
                if (site.equals("ting56")) {
                    author = author.split(" ")[0];
                    speak = speak.split(" ")[1];
                }
                if (site.equals("tingchina")) {//听中国的声音
                    author = "未知";
                    img = tmpImg;
                    speak = "未知";
                }
                if (site.equals("ysts8")) {//有声听书吧特殊处理
                    author = "未知";
                    img = tmpImg;
                    speak = speak.split("／")[0];
                }
                if (site.equals("ysxs8") || site.equals("audio699")) {
                    img = tmpImg;
                }
                //后处理
                if (!author.contains("：")) {
                    author = "作者：" + author;
                }
                if (!speak.contains("：")) {
                    speak = "播音：" + speak;
                }
                //根据搜索类型确定结果
                switch (keyType) {
                    case "书名":
                        //包含搜索关键字才添加,失效不添加
                        if (title.contains(keyWord)) {
                            list.add(new AudioBook(author.trim(), speak.trim(), title.trim(), img, homeUrl.trim()));
                        }
                        break;
                    case "作者":
                        //包含搜索关键字才添加,失效不添加
                        if (author.contains(keyWord)) {
                            list.add(new AudioBook(author.trim(), speak.trim(), title.trim(), img, homeUrl.trim()));
                        }
                        break;
                    case "播音":
                        //包含搜索关键字才添加,失效不添加
                        if (speak.contains(keyWord)) {
                            list.add(new AudioBook(author.trim(), speak.trim(), title.trim(), img, homeUrl.trim()));
                        }
                        break;
                    default:
                        list.add(new AudioBook(author.trim(), speak.trim(), title.trim(), img, homeUrl.trim()));
                }

            }
            return list;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取章节列表
     *
     * @param url 链接
     * @return 列表
     */
    public List<AudioChapter> getChapters(String url) {
        //懒人听书
        if (url.contains("lrts")) {
            return getLRTSChapters(url);
        }
        //其他
        List<AudioChapter> chapters = new ArrayList<>(100);
        String key = getCase(url);
        String charset = conf.getProperty(key + "_charset");
        String html = HtmlUtil.getHtml(url, charset);
        //解析
        Document document = Jsoup.parse(html);
        //根域名
        document.setBaseUri(url);
        //css选择器
        String[] s = conf.getProperty(key).split(",");
        //读取配置
        Elements elements = document.select(s[0]).get(Integer.parseInt(s[1])).select(s[2]);
        for (Element a : elements) {
            chapters.add(new AudioChapter(a.absUrl("href"), a.text()));
        }
        return chapters;
    }

    //获取有声音频真实地址
    public String getSrc(String url) {
        System.out.println("get" + getCase(url).toUpperCase());
        String realUrl = ReflectUtil.invoke(this, "get" + getCase(url).toUpperCase(), url).toString();
        try {
            return com.unclezs.utils.URLEncoder.encode(realUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return realUrl;
    }

    private String getIXINMO(String url) {
        String html = HtmlUtil.getHtml(url, "utf-8");
        return Jsoup.parse(html).select("audio > source").attr("src");
    }

    //根据host不同采用不同的方式爬取
    private String getCase(String url) {
        String host = ReUtil.get("http[s]{0,1}://(.+?)/", url, 1);
        String[] str = host.split("\\.");
        if (str.length == 3) {
            return str[1];
        } else {
            return str[0];
        }
    }

    //520听书网和有声小说吧
    private String get520TINGSHU(String curl) {
        String host = curl.substring(0, curl.indexOf("com") + 4);
        String html = HtmlUtil.getSource(curl, "gb2312", curl, null);
        Pattern pattern = Patterns.TS8_REG;
        Matcher m = pattern.matcher(html);
        if (!m.find()) {
            return "";
        }
        String jsUrl = host + m.group(1);
        String jsRes = HttpUtil.request(jsUrl);
        String json = jsRes.substring(jsRes.indexOf(',') + 1, jsRes.lastIndexOf("]]"));
        JSONArray array = JSON.parseArray(json);
        String[] indexStr;
        if (curl.contains("520tingshu")) {
            indexStr = curl.replace(".html", "").split("-");
        } else {
            indexStr = curl.replace(".html", "").split("_");
        }
        int index = Integer.parseInt(indexStr[indexStr.length - 1]);
        return array.get(index).toString().split("[$]")[1];
    }

    //幻听网
    private String getTING89(String url) {
        String html = HtmlUtil.getSource(url, "gb2312", url, null);
        String realUrl = Jsoup.parse(html).select("iframe").attr("src");
        realUrl = realUrl.substring(realUrl.lastIndexOf("http"));
        return realUrl;
    }

    //有声听书吧
    private String getYSTS8(String curl) {
        //章节源码爬取
        String chtml = HtmlUtil.getSource(curl, Charsets.GBK, curl, null);
        Pattern p = Patterns.YSTS8_REG;
        Matcher m = p.matcher(chtml);
        if (!m.find()) {
            return "";
        }
        //找到真正所在源码网址
        String url = "https://www.ysts8.com" + m.group(1);//真正章节网址
        String prefix = null;
        try {
            prefix = URLDecoder.decode(url.substring(url.indexOf("=") + 1, url.indexOf("&")), Charsets.GBK) + "?";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //匹配出真实音频url
        String html = HtmlUtil.getHtml(url, Charsets.GBK);

        String host = "http://180d.ysts8.com:8000";
        p = Patterns.YSTS8_REG1;
        m = p.matcher(html);
        if (!m.find()) {
            return "";
        }
        //真实URL  m.group(1)加密码
        return host + "/" + prefix + m.group(1);
    }

    //56听书网
    private String getTING56(String url) {
        String html = HtmlUtil.getSource(url, Charsets.GBK, url, null);
        Pattern pattern = Patterns.TING56_REG;
        Matcher m = pattern.matcher(html);
        if (!m.find()) {
            return "";
        }
        String res = getCodeString(m.group(1));
        String realUrl;
        //解密
        String[] datas = res.split("&");//初步数据
        switch (datas[2]) {
            case "tudou":
                String rJson = HttpUtil.request("http://www.ting56.com//player/getcode.php?id=" + datas[0]);
                String code = JSON.parseObject(rJson).get("code").toString();
                if ("".equals(code)) {
                    realUrl = "http://www.tudou.com/programs/view/html5embed.action?code=" + code + "&autoPlay=true&playType=AUTO";
                } else {
                    realUrl = "http://www.tudou.com/v/" + datas[0];
                }
                break;
            case "tc":
                String[] strings = datas[0].split("/");
                String jmUrl = strings[0] + '/' + strings[1] + "/play_" + strings[1] + "_" + strings[2] + ".htm";
                List<NameValuePair> postData = new ArrayList<>();
                postData.add(new BasicNameValuePair("url", jmUrl));
                String resJson = HttpUtil.doPost("http://www.ting56.com/player/tingchina.php", postData);
                realUrl = JSON.parseObject(resJson).getString("url").replace("t44", "t33");
                break;
            default:
                realUrl = datas[0].replace(":82", "");
        }
        return realUrl;
    }

    //恋听网
    private String getTING55(String url) {
        String html = HtmlUtil.getSource(url, "utf-8", url, null);
        Pattern p = Pattern.compile("<meta name=\"_c\" content=\"(.+?)\"");
        Matcher m = p.matcher(html);
        String xt;
        if (m.find()) {
            xt = m.group(1);
            String[] info = url.substring(url.lastIndexOf("/") + 1).split("\\-");
            String body = cn.hutool.http.HttpUtil
                    .createPost("https://ting55.com/glink")
                    .header("xt", xt)
                    .header("Referer", url)
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36")
                    .form("bookId", info[0])
                    .form("isPay", "0")
                    .form("page", info[1])
                    .execute().body();
            JSONObject jsonObject = JSON.parseObject(body);
            String res = jsonObject.getString("url");
            if (StrUtil.isEmpty(res)) {
                res = jsonObject.getString("ourl");
            }
            return res;
        }
        return "";
    }

    //静听网
    private String getAUDIO699(String url) {
        String html = null;
        if (isPhone) {
            url = url.replace("www", "m");
            String ua = "Mozilla/5.0 (Linux; U; Android 9; zh-CN; MI MAX 3 Build/PKQ1.190118.001) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.108 UCBrowser/12.5.0.1030 Mobile Safari/537.36";
            html = HtmlUtil.getSource(url, "utf-8", url, ua);
        } else {
            html = HtmlUtil.getSource(url, "utf-8", url, null);
        }
        Document document = Jsoup.parse(html);
        return document.select("source").attr("src");
    }

    //听中国的声音
    private String getTINGCHINA(String url) {
        String chtml = HtmlUtil.getHtml(url, "gb2312");
        Pattern pattern = Pattern.compile("playurl_flash=\"(.+?)\"");
        Matcher m = pattern.matcher(chtml);
        if (m.find()) {
            String link = null;
            try {
                link = com.unclezs.utils.URLEncoder.encode("http://www.tingchina.com" + m.group(1), "gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String html = HtmlUtil.getSource(link, "gb2312", url, null);
            Pattern p = Pattern.compile("url.3.= \"(.+?)\"");
            Matcher mm = p.matcher(html);
            if (mm.find()) {
                return "http://t44.tingchina.com" + mm.group(1);
            }
        }
        return "";
    }

    //懒人听书
    public String getLRTS(String url) {
        String html = HtmlUtil.getHtml(url, "utf-8");
        Document document = Jsoup.parse(html);
        String realUrl = document.select(".section").first().select("input").first().attr("value");
        if (realUrl.length() < 4)
            realUrl = "http://180d.ysts8.com:8000/";
        return realUrl;
    }

    //22听书网
    public String getTING22(String url) {
        String[] query = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")).split("\\-");
        int chapterNumber = Integer.parseInt(query[1]);
        int page = (int) Math.ceil(chapterNumber * 1.0f / 10);
        int pageIndex = (chapterNumber - 1) % 10;
        //音频地址API
        String api = "https://www.ting22.com/api.php?c=Json&page=" + page + "&pagesize=10&callback=jQuery&id=" + query[0];
        Map<String, String> headers = new HashMap<>(2);
        headers.put(Config.Referer, url);
        headers.put("sign", System.currentTimeMillis() + "");
        //获取URL加密Code
        String rjson = HtmlUtil.getHtmlSource(api, "utf-8", headers).replace("jQuery(", "").replace(");", "");
        String code = JSON.parseObject(rjson).getJSONArray("playlist").getJSONObject(pageIndex).getString("file");
        //解密
        code = getCodeString(code);
        //根据类型获取真实音频地址
        String realUrl = "";
        String[] tempUrl = code.split("\\$");
        //不是默认真实音频地址的，需要另外解析
        if (tempUrl.length == 2) {
            switch (tempUrl[1]) {
                //yousheng/29545/0$tc
                case "tc":
                    String[] data = tempUrl[0].split("/");
                    String tc_api = data[0] + "/" + data[1] + "/play_" + data[1] + "_" + data[2] + ".htm";
                    realUrl = JSON.parseObject(HtmlUtil.getHtml("https://c.ting22.com/tingchina.php?file=" + tc_api, "utf-8")).getString("url");
                    break;
                // 19576598/139592587$xm
                case "xm":
                    realUrl = "http://mobile.ximalaya.com/mobile/redirect/free/play/" + tempUrl[0].split("/")[1] + "/0";
                    break;
                default:
                    break;
            }
        } else {
            realUrl = tempUrl[0];
        }
        return realUrl;
    }

    private List<AudioChapter> getLRTSChapters(String url) {
        List<AudioChapter> chapters = new ArrayList<>(100);
        String bookId = url.substring(url.lastIndexOf("/") + 1);
        String html = HtmlUtil.getHtml(url, "utf-8");
        Pattern pattern = Patterns.LRTS_REG;
        Matcher m = pattern.matcher(html);
        if (m.find()) {
            //章节总数
            int pageNum = Integer.parseInt(m.group(1));
            for (int i = 1; i <= pageNum; i++) {
                chapters.add(new AudioChapter("http://www.lrts.me/ajax/playlist/2/" + bookId + "/" + i, "" + i));
            }
        }
        return chapters;
    }

    /**
     * 将45*48*78类的转字符
     *
     * @param src /
     * @return
     */
    private String getCodeString(String src) {
        String[] u = src.split("\\*");
        StringBuilder res = new StringBuilder();
        for (String s : u) {
            if (!"".equals(s))
                res.append((char) Integer.parseInt(s));
        }
        return res.toString();
    }
}
