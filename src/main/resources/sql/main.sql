/*
 Navicat Premium Data Transfer

 Source Server         : Uncle小说下载软件
 Source Server Type    : SQLite
 Source Server Version : 3021000
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3021000
 File Encoding         : 65001

 Date: 12/08/2020 13:59:58
*/

PRAGMA
foreign_keys = false;

-- ----------------------------
-- Table structure for audio_book
-- ----------------------------
DROP TABLE IF EXISTS "audio_book";
CREATE TABLE "audio_book"
(
    "id"                 text NOT NULL,
    "author"             TEXT,
    "speak"              TEXT,
    "title"              TEXT,
    "cover"              TEXT,
    "url"                TEXT,
    "last_chapter_index" INTEGER,
    "last_time"          text,
    "last_chapter_name"  TEXT,
    "update_time"        TEXT
);

-- ----------------------------
-- Records of audio_book
-- ----------------------------
INSERT INTO "audio_book"
VALUES ('76746ff2ffdc4a1b91ebcef5307f1574', '辰东', '昊儒', '完美世界（上部）',
        'D:/java/NovelHarvester/images/完美世界（上部）_1590559688940.png', 'https://www.lrts.me/book/5594', 0,
        0.00302662379771657, 1, '2020-07-15 15:23:22');

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS "book";
CREATE TABLE "book"
(
    "id"              text NOT NULL,
    "name"            TEXT,
    "path"            TEXT,
    "cover"           TEXT,
    "chapter_index"   integer,
    "web"             real,
    "location"        TEXT,
    "reading_chapter" TEXT,
    "chapter_path"    TEXT,
    "auto_update"     real,
    PRIMARY KEY ("id")
);

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO "book"
VALUES ('c5c47322064d4145b547c55eb94d24dc', '完美世界', 'http://www.bxwx8.la/b/70/70093/',
        'D:/java/NovelHarvester/images/完美世界_1590931158181.png', 211, 1.0, 0.52736214945583, '211,1',
        'D:/java/NovelHarvester/cache/c5c47322064d4145b547c55eb94d24dc/book.json', 0.0);

-- ----------------------------
-- Table structure for download_record
-- ----------------------------
DROP TABLE IF EXISTS "download_record";
CREATE TABLE "download_record"
(
    "id"       text NOT NULL,
    "title"    TEXT,
    "type"     TEXT,
    "path"     TEXT,
    "cover"    TEXT,
    "size"     REAL,
    "datetime" TEXT
);

-- ----------------------------
-- Table structure for search_audio_rule
-- ----------------------------
DROP TABLE IF EXISTS "search_audio_rule";
CREATE TABLE "search_audio_rule"
(
    "site"         TEXT NOT NULL,
    "name"         TEXT,
    "search_url"   TEXT,
    "search_key"   TEXT,
    "method"       TEXT,
    "search_list"  TEXT,
    "cover"        TEXT,
    "catalog_list" TEXT,
    "catalog_name" TEXT,
    "catalog_url"  TEXT,
    "speak"        TEXT,
    "title"        TEXT,
    "author"       TEXT,
    "url"          TEXT,
    "client"       real,
    "strict"       real,
    "next_page"    TEXT,
    "weight"       integer,
    "enabled"      real,
    "charset"      TEXT,
    PRIMARY KEY ("site")
);

-- ----------------------------
-- Records of search_audio_rule
-- ----------------------------
INSERT INTO "search_audio_rule"
VALUES (' ', '静听网', 'http://m.audio699.com/search', 'keyword', 'GET', '//*[@id="wrapper"]/section/div[1]/a',
        'a/dl/dt/img/@src', '//*[@id="wrapper"]/section/div[4]/div/a', 'a/text()', 'a/@href',
        'a/dl/dd/p[2]/text()||播音：', 'a/dl/dd/h3/text()', 'a/dl/dd/p[1]/text()||作者：', 'a/@href', 1.0, 0.0, NULL, 1, 1.0,
        'utf-8');
INSERT INTO "search_audio_rule"
VALUES ('https://ting22.com/', '22听书网', 'https://ting22.com/search.php?page=1', 'q', 'GET',
        '//*[@id="body"]//div[@class="result"]', 'div/div[@class="image_pic"]/a/img/@src', '//*[@id="vlink"]/li/a',
        'a/text()', 'a/@href', 'div/div[2]/div[1]/span[2]/text()||播音：', '//h3[@class="title"]/a/allText()',
        'div/div[2]/div[1]/span[1]/text()||作者：', 'div/div[@class="image_pic"]/a/@href', 0.0, 0.0,
        '//*[@id="page"]/a[4]/@href', 2, 1.0, 'utf-8');
INSERT INTO "search_audio_rule"
VALUES ('https://www.tingchina.com/', '听中国的声音', 'https://www.tingchina.com/search1.asp', 'keyword', 'GET',
        '/html/body/div[2]/div[3]/dl/dd/ul/li/a', NULL,
        '/html/body/div[2]/div[5]/div[1]/div[2]/div[5]/ul/li/div[@class="b2"]/a', 'a/text()', 'a/@abs:href', '',
        'a/text()', '', 'a/@abs:href', 0.0, 0.0, '', 3, 1.0, 'gb2312');
INSERT INTO "search_audio_rule"
VALUES ('https://ting55.com/', '恋听网', 'https://ting55.com/search/', NULL, 'GET',
        '//*[@id="wrapper"]/div[3]/div/div/div/ul/li', 'li/div[@class="img"]/a/img/@abs:src',
        '//*[@id="wrapper"]/div[3]/div/div/div[4]/div/ul/li/a', 'a/text()', 'a/@abs:href',
        'li/div[@class="info"]/p[2]/text()||播音：', 'li/div[@class="info"]/h4/a/allText()',
        'li/div[@class="info"]/p[1]/text()||作者：', 'li/div[@class="info"]/h4/a/@abs:href', 0.0, 0.0, NULL, 4, 1.0,
        'utf-8');
INSERT INTO "search_audio_rule"
VALUES ('http://m.ixinmo.com/', '心魔听书网', 'http://m.ixinmo.com/search.html', 'searchword', 'POST',
        '//div[@class="list-ov-tw"]', '//div[@class="list-ov-tw"]/div[@class="list-ov-t"]/a/img/@src',
        '//*[@id="playlist"]/ul/li', 'a/text()', 'a/@href',
        'div[@class="list-ov-tw"]/div[@class="list-ov-w"]/a/span[3]/text()||演播：',
        'div[@class="list-ov-tw"]/div[@class="list-ov-w"]/a/span[1]/text()',
        'div[@class="list-ov-tw"]/div[@class="list-ov-w"]/a/span[2]/text()||作者：',
        'div[@class="list-ov-tw"]/div[@class="list-ov-w"]/a/@href', 1.0, 0.0, '', 5, 1.0, 'utf-8');
INSERT INTO "search_audio_rule"
VALUES ('http://www.520tingshu.com/', '520听书网', 'http://www.520tingshu.com/search.asp', 'searchword', 'POST',
        '//*[@id="baybox"]/div[1]/ul/li', 'li/dl/dt/a/img/@abs:src', '//*[@id="baybox"]/div/div[2]/font/font/ul/li',
        'li/a/text()', 'li/a/@abs:href', 'li/dl/dd[3]/text()||主演：', 'li/dl/dd[1]/h2/a/text()', NULL,
        'li/dl/dd[1]/h2/a/@abs:href', 0.0, 0.0, '//*[@id="baybox"]/div[1]/div[2]/a[5]/@abs:href', 6, 1.0, 'gb2312');
INSERT INTO "search_audio_rule"
VALUES ('http://www.ting56.com/', '56听书网', 'http://www.ting56.com/search.asp', 'searchword', 'POST',
        '//div[@class="xiaoshuo left"]/ul/li', 'li/a/img/@abs:src', '//*[@id="vlink_1"]/ul/li/a', 'a/text()',
        'a/@abs:href', 'li/dl/dd[2]/text(2)', 'li//dl/dt/a/text()', 'li/dl/dd[2]/text(1)', 'li/a/@abs:href', 0.0, 0.0,
        '//*[@id="pagelink"]/a[4]/@abs:href', 7, 1.0, 'gb2312');
INSERT INTO "search_audio_rule"
VALUES ('http://www.ting89.com/', '幻听网', 'http://www.ting89.com/search.asp', 'searchword', 'POST',
        '//*[@id="channelright"]/div[2]/div[3]/ul/li', 'li/a/img/@src', '/html/body/div[5]/div[1]/div[2]/div/ul/li/a',
        'a/text()', 'a/@abs:href', 'li/p[4]/text()||播音：', 'li/p[1]/a/b/text()', 'li/p[3]/text()||作者：',
        'li/p[1]/a/@abs:href', 0.0, 0.0, '//*[@id="channelright"]/div[2]/div[4]/table/tbody/tr/td/ul/li[4]/a/@abs:href',
        8, 1.0, 'gb2312');
INSERT INTO "search_audio_rule"
VALUES ('https://www.ysts8.net/', '有声听书吧', 'https://www.ysts8.net/Ys_so.asp?stype=1', 'keyword', 'GET',
        '/html/body/div[5]/div[2]/ul/li/a', NULL, '/html/body/div[5]/div[2]/div[1]/ul[1]/li/a', 'a/text()',
        'a/@abs:href', 'a/span/text()||／.+', 'a/text()', '', 'a/@abs:href', 0.0, 0.0, NULL, 9, 1.0, 'gb2312');
INSERT INTO "search_audio_rule"
VALUES ('https://www.lrts.me/', '懒人听书', 'https://www.lrts.me/search/book/', NULL, 'GET',
        '/html/body/div[1]/div[1]/div[2]/ul/li', 'li/div[1]/a/img/@src', NULL, NULL, NULL,
        'li/div[2]/div[2]/a[2]/text()', 'li/div[2]/a[2]/allText()', 'li/div[2]/div[2]/a[1]/text()',
        'li/div[2]/a[2]/@abs:href', 0.0, 0.0, NULL, 10, 1.0, 'utf-8');

-- ----------------------------
-- Table structure for search_text_rule
-- ----------------------------
DROP TABLE IF EXISTS "search_text_rule";
CREATE TABLE "search_text_rule"
(
    "site"        TEXT NOT NULL,
    "name"        TEXT,
    "title"       TEXT,
    "cover"       TEXT,
    "author"      TEXT,
    "strict"      real,
    "url"         TEXT,
    "search_key"  TEXT,
    "result_list" TEXT,
    "method"      TEXT,
    "search_link" TEXT,
    "weight"      integer,
    "enabled"     real,
    "charset"     TEXT,
    "next_page"   TEXT,
    PRIMARY KEY ("site")
);

-- ----------------------------
-- Records of search_text_rule
-- ----------------------------
INSERT INTO "search_text_rule"
VALUES ('bxwx8.la', '笔下文学', '/td[1]/a/text()', NULL, '/td[3]/text()', 1.0, '/td[2]/a/@href||[\d.]+html', 'q',
        '//table[@class="grid"]//tr[@align!="center"]', 'GET', 'http://www.bxwx8.la/cse/search', 1, 1.0, 'utf-8', '');
INSERT INTO "search_text_rule"
VALUES ('x23qb.com', '铅笔小说', 'dl/dd[1]/h3/a/text()', 'dl/dt/a/img/@_src', 'dl/dd[2]/span[1]/text()', 0.0,
        'dl/dd[1]/h3/a/@href', 'searchkey', '//*[@id="sitebox"]/dl', 'POST', 'https://www.x23qb.com/search.php', 1, 1.0,
        'gbk', '//*[@id="pagelink"]/a[@class="next"]/@abs:href');
INSERT INTO "search_text_rule"
VALUES ('shencou.com', '神凑轻小说', 'tr/td[1]/a/text()', '', 'tr/td[3]/text()', 0.0, 'tr/td[2]/a/@href', 'searchkey',
        '//*[@id="content"]/table/tbody/tr[@align!="center"]', 'POST',
        'http://www.shencou.com/modules/article/search.php', 2, 1.0, 'gb2312', '');

-- ----------------------------
-- Table structure for sqlite_sequence
-- ----------------------------
DROP TABLE IF EXISTS "sqlite_sequence";
CREATE TABLE "sqlite_sequence"
(
    "name",
    "seq"
);

-- ----------------------------
-- Records of sqlite_sequence
-- ----------------------------
INSERT INTO "sqlite_sequence"
VALUES ('audio_book', 43);
INSERT INTO "sqlite_sequence"
VALUES ('download_record', 37);

-- ----------------------------
-- Auto increment value for audio_book
-- ----------------------------

-- ----------------------------
-- Auto increment value for download_record
-- ----------------------------

PRAGMA
foreign_keys = true;
