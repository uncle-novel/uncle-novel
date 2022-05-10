BEGIN
TRANSACTION;
-- 有声小说
drop table if exists `audio_book`;
create table `audio_book`
(
    `currentProgress`     DOUBLE PRECISION,
    `broadcast`           VARCHAR,
    `currentChapterName`  VARCHAR,
    `id`                  VARCHAR PRIMARY KEY,
    `url`                 VARCHAR,
    `name`                VARCHAR,
    `author`              VARCHAR,
    `cover`               VARCHAR,
    `order`               INTEGER,
    `currentChapterIndex` INTEGER,
    `group`               VARCHAR,
    `currentPage`         INTEGER,
    `charset`             VARCHAR,
    `local`               BOOLEAN,
    `txtTocRule`          VARCHAR,
    `update`              BOOLEAN
);

-- 文本小说
drop table if exists `book`;
create table `book`
(
    `id`                  VARCHAR PRIMARY KEY,
    `url`                 VARCHAR,
    `name`                VARCHAR,
    `author`              VARCHAR,
    `cover`               VARCHAR,
    `order`               INTEGER,
    `currentChapterIndex` INTEGER,
    `group`               VARCHAR,
    `currentPage`         INTEGER,
    `charset`             VARCHAR,
    `local`               BOOLEAN,
    `txtTocRule`          VARCHAR,
    `update`              BOOLEAN
);

-- 下载历史
drop table if exists `download_history`;
create table `download_history`
(
    `id`   INTEGER PRIMARY KEY AUTOINCREMENT,
    `name` VARCHAR,
    `path` VARCHAR,
    `date` VARCHAR,
    `type` VARCHAR
);

-- 搜索引擎
drop table if exists `search_engine`;
create table `search_engine`
(
    `id`         INTEGER PRIMARY KEY AUTOINCREMENT,
    `enabled`    BOOLEAN,
    `name`       VARCHAR,
    `url`        VARCHAR,
    `stylesheet` VARCHAR
);
INSERT INTO main.search_engine (id, enabled, name, url, stylesheet) VALUES (1, 1, '百度', 'https://www.baidu.com/s?wd=title: (阅读 "{{keyword}}" (最新章节) -(官方网站))', 'css/home/webview/baidu.css');
INSERT INTO main.search_engine (id, enabled, name, url, stylesheet) VALUES (2, 1, '谷歌', 'https://www.google.com.hk/search?q={{keyword}} 小说最新章节', 'css/home/webview/google.css');
INSERT INTO main.search_engine (id, enabled, name, url, stylesheet) VALUES (3, 1, '必应', 'https://cn.bing.com/search?q={{keyword}} 小说最新章节', 'css/home/webview/bing.css');


-- 文本小说规则
drop table if exists `txt_toc_rule`;
create table `txt_toc_rule`
(
    `id`      INTEGER PRIMARY KEY AUTOINCREMENT,
    `name`    VARCHAR,
    `rule`    VARCHAR,
    `order`   INTEGER
);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-18, '标题 特殊符号 序号', '^.{1,20}[(（][\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,8}[)）][ 　	]{0,4}$', 90);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-17, '双标题(后向)', '(?m)(?<=[ \t　]{0,4}第[\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,8}章.{0,30}$[\s　]{0,8})第[\d零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,8}章.{0,30}$', 85);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-16, '双标题(前向)', '(?m)(?<=[ \t　]{0,4})第[\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,8}章.{0,30}$(?=[\s　]{0,8}第[\d零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,8}章)', 80);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-15, '顶格标题', '^\S.{1,20}$', 75);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-14, '章/卷 序号 标题', '^[ \t　]{0,4}(?:(?:内容|文章)?简介|文案|前言|序章|序言|卷首语|扉页|楔子|正文(?!完|结)|终章|后记|尾声|番外|[卷章][\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,8})[ 　]{0,4}.{0,30}$', 70);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-13, '特殊符号 标题(单个)', '(?<=[\s　]{0,4})(?:[☆★✦✧].{1,30}|(?:内容|文章)?简介|文案|前言|序章|楔子|正文(?!完|结)|终章|后记|尾声|番外)[ 　]{0,4}$', 65);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-12, '特殊符号 标题(成对)', '(?<=[\s　]{0,4})(?:[\[〈「『〖〔《（【\(].{1,30}[\)】）》〕〗』」〉\]]?|(?:内容|文章)?简介|文案|前言|序章|楔子|正文(?!完|结)|终章|后记|尾声|番外)[ 　]{0,4}$', 60);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-11, '特殊符号 序号 标题', '(?<=[\s　])[【〔〖「『〈［\[](?:第|[Cc]hapter)[\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10}[章节].{0,20}$', 55);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-10, 'Chapter(去简介)', '^[ 　\t]{0,4}(?:[Cc]hapter|[Ss]ection|[Pp]art|ＰＡＲＴ|[Nn][Oo]\.|[Ee]pisode)\s{0,4}\d{1,4}.{0,30}$', 50);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-9, 'Chapter/Section/Part/Episode 序号 标题', '^[ 　\t]{0,4}(?:[Cc]hapter|[Ss]ection|[Pp]art|ＰＡＲＴ|[Nn][oO]\.|[Ee]pisode|(?:内容|文章)?简介|文案|前言|序章|楔子|正文(?!完|结)|终章|后记|尾声|番外)\s{0,4}\d{1,4}.{0,30}$', 45);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-8, '正文 标题/序号', '^[ 　\t]{0,4}正文[ 　]{1,4}.{0,20}$', 40);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-7, '大写数字 分隔符 标题名称', '^[ 　\t]{0,4}(?:序章|序言|卷首语|扉页|楔子|正文(?!完|结)|终章|后记|尾声|番外|[〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,8})[ 、_—\-].{1,30}$', 35);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-6, '数字 分隔符 标题名称', '^[ 　\t]{0,4}\d{1,5}[：:,.， 、_—\-].{1,30}$', 30);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-5, '数字(纯数字标题)', '(?<=[　\s])\d+\.?[ 　\t]{0,4}$', 25);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-4, '目录(古典、轻小说备用)', '^[ 　\t]{0,4}(?:序章|楔子|正文(?!完|结)|终章|后记|尾声|番外|第?\s{0,4}[\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]+?\s{0,4}(?:章|节(?!课)|卷|集(?![合和])|部(?![分赛游])|回(?![合来事去])|场(?![和合比电是])|话|篇(?!张))).{0,30}$', 20);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-3, '目录(匹配简介)', '(?<=[　\s])(?:(?:内容|文章)?简介|文案|前言|序章|序言|卷首语|扉页|楔子|正文(?!完|结)|终章|后记|尾声|番外|第?\s{0,4}[\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]+?\s{0,4}(?:章|节(?!课)|卷|集(?![合和])|部(?![分赛游])|回(?![合来事去])|场(?![和合比电是])|篇(?!张))).{0,30}$', 15);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-2, '目录', '^[ 　\t]{0,4}(?:序章|序言|卷首语|扉页|楔子|正文(?!完|结)|终章|后记|尾声|番外|第?\s{0,4}[\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]+?\s{0,4}(?:章|节(?!课)|卷|集(?![合和])|部(?![分赛游])|篇(?!张))).{0,30}$', 10);
INSERT INTO main.txt_toc_rule (id, `name`, rule, `order`) VALUES (-1, '目录(去空白)', '(?<=[　\s])(?:序章|序言|卷首语|扉页|楔子|正文(?!完|结)|终章|后记|尾声|番外|第?\s{0,4}[\d〇零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]+?\s{0,4}(?:章|节(?!课)|卷|集(?![合和])|部(?![分赛游])|篇(?!张))).{0,30}$', 5);
COMMIT;