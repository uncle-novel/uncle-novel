/**
 * phantomjs 解析动态网页
 * 支持传入referer、cookie、useragent
 *
 * 注意：不能使用let声明变量 使用参照 com.unclezs.novel.core.request.PhantomJsClientTest
 * @author blog.unclezs.com
 * @date   2020-12-24
 * @see    https://phantomjs.org/api/
 */
var page = require('webpage').create();
var system = require('system');

// 只传入脚本名称 不传入参数不执行
if (system.args.length === 1) {
  phantom.exit();
}
// 为了提升加载速度，不加载图片
page.settings.loadImages = false;
// 超过10秒放弃加载
page.settings.resourceTimeout = 10000;
// 忽略SSL错误

// 参数 需要按照顺序
var url = system.args[1];
var referer = system.args[2];
var cookie = system.args[3];
var userAgent = system.args[4];
var customHeaders = {
  'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36',
  'Referer': url
};
if (referer) {
  customHeaders['Referer'] = referer;
}
if (cookie) {
  customHeaders['Cookie'] = cookie;
}
if (userAgent) {
  customHeaders['User-Agent'] = userAgent;
}
page.customHeaders = customHeaders;
page.open(url, function (status) {
  if (status === 'success') {
    console.log(page.content);
  }
  phantom.exit();
});
