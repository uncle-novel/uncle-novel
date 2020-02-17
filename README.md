# Uncle小说 V3.54
一个PC端（win、mac、linux）文本小说下载、阅读；有声小说下载

由javaFX+Mybatis+SqLite+Maven搭建环境编写，fxlauncher实现自动更新

[安卓版地址](https://github.com/unclezs/NovelDownloader)

## 更新说明/下载地址
[查看更新介绍](https://unclezs.gitee.io/service/%E6%9B%B4%E6%96%B0%E8%AF%B4%E6%98%8E.html "更新说明")

[Uncle小说下载地址](https://www.lanzous.com/b517134)
## 功能简介：
### 一、.TXT小说下载
1.1 下载任意小说网站的免费小说，打包成TXT格式，方式：通过小说目录链接解析下载

1.2 自定义线程及延迟下载，防止被封IP

1.3 支持动态网页抓取，动态网页需要等待较长时间，即使显示请求超时也请耐心等待，基于HTMLUnit实现

1.4 支持自定义章节及正文内容范围匹配，让抓取更加精确

1.5 支持自定义Cookies模拟登陆。可以自定义User-Agent(可用于伪装成手机)

1.6 支持去广告，一行一条，让阅读在无广告

1.7 章节过滤、乱序重排、多种正文规则，繁体转简体、NRC字体转中文

1.8 支持导出为txt，epub,mobi格式

**友情提示，有时候没有匹配到或者乱序了，可以关闭章节过滤和乱序重排说不定会有奇效哈**

### 二、TXT小说阅读器

2.1 首先得支持了章节记忆，精确到行，毕竟我也是书虫知道想要啥

2.2 三种字体 宋体、雅黑、楷体选择，常用背景颜色更换

2.3 可以调节页距，不是窗口大小！，当然窗口也是可以调整的

2.4 语音朗读，用的jacob做的，说实话这玩意真不好用

2.5 窗口大小记忆，记录下你最想要的尺寸。。

2.6 支持本地小说导入阅读，可以拖入导入

2.7 下滑底部顶部，左右键切换章节

2.8 点击左右区域翻页阅读

**个人建议，F11全屏模式更有阅读体验**

### 三、有声小说下载

3.1 支持7个源，可以切换，想用哪些用哪些

3.2 支持检测源是否失效，防止浪费时间下载

3.3 可以分块下载，也就是你选几个下几个，也算分块了把哈哈

3.4 多线程下载，必须有的，线程多了失败一大片，悠着点


**提示下，想完美，就单线程下载咯，慢点问题不大**

### 四、有声小说在线听书

4.1 既然在线看了也得能在线听才对的

4.2 记录上次听到的位置，精确到秒

4.3 剩下的一些常规的听书功能，自动下一章之类的

4.4 播放失败可以重新试试，也可以换源

### 五、操作技巧

1.列表选中支持shitf操作

2.大多数地方斗都有右键菜单，记住在列表里面不要点到字了，不然回触发不了事件
## 使用帮助

### 下载文本小说正确步骤：
1.搜索小说

2.解析目录

3.选出想下载的章节（可以shift）

4.点击章节目录查看正文内容,点到章节文字是选中，空白处是查看内容，可以右键

5.过滤除不要的内容（加范围，去广告），范围最好书网页源码的内容

6.点击加入书架或者下载

7.可以到下载管理进行查看进度

8.发现下载失败数量比较多到则增加每个线程下载章节数量。

**可以直接拿个目录链接进行上诉操作，如果是动态网页记得开启动态网页支持，切换规则不用重新解析，只有和章节有关的需要重新解析**


### 下载有声小说正确姿势：
1.搜索有声小说

2.随机检测几个失效了没有，一般失效了一个就是全部失效了，可以自己手动验证，比如复制音频链接到浏览器打开看看

3.添加书架获取直接选择你想要的开始下载

4.发现失败较多，增加每个线程的下载的章节数量，增加延迟


## 预览图
#### 1.小说搜索页

![Image text](https://github.com/unclezs/NovelHarvester/raw/master/screenshot/%E5%B0%8F%E8%AF%B4%E6%90%9C%E7%B4%A2.png)

#### 2.解析小说
![Image text](https://github.com/unclezs/NovelHarvester/raw/master/screenshot/%E8%A7%A3%E6%9E%90%E5%B0%8F%E8%AF%B4.png)

#### 3.解析设置
![Image text](https://github.com/unclezs/NovelHarvester/raw/master/screenshot/%E8%A7%A3%E6%9E%90%E8%AE%BE%E7%BD%AE.png)

#### 4.文本书架
![Image text](https://github.com/unclezs/NovelHarvester/raw/master/screenshot/%E6%96%87%E6%9C%AC%E4%B9%A6%E6%9E%B6.png)

#### 5.阅读器
![Image text](https://github.com/unclezs/NovelHarvester/raw/master/screenshot/%E9%98%85%E8%AF%BB%E5%99%A8.png)

#### 6.有声搜索
![Image text](https://github.com/unclezs/NovelHarvester/raw/master/screenshot/%E6%9C%89%E5%A3%B0%E6%90%9C%E7%B4%A2.png)

#### 7.有声书架
![Image text](https://github.com/unclezs/NovelHarvester/raw/master/screenshot/%E6%9C%89%E5%A3%B0%E4%B9%A6%E6%9E%B6.png)

#### 8.正在下载
![Image text](https://github.com/unclezs/NovelHarvester/raw/master/screenshot/%E6%AD%A3%E5%9C%A8%E4%B8%8B%E8%BD%BD.png)

#### 9.下载完成
![Image text](https://github.com/unclezs/NovelHarvester/raw/master/screenshot/%E4%B8%8B%E8%BD%BD%E5%AE%8C%E6%88%90.png)

# 动力动力
<img src="https://github.com/unclezs/NovelHarvester/raw/master/screenshot/zfb.jpg" alt="Sample"  width="400" height="526">  <img src="https://github.com/unclezs/NovelHarvester/raw/master/screenshot/wx.png" alt="Sample"  width="400" height="526">
