<p align="center">
    <a href="https://github.com/unclezs/NovelHarverster/actions/workflows/gradle.yml">
    <img src="https://img.shields.io/github/workflow/status/unclezs/uncle-novel/Java%20CI%20with%20Gradle" alt="gradle build"/>
    </a>
    <a href="https://travis-ci.com/unclezs/uncle-novel">
    <img src="https://img.shields.io/travis/com/unclezs/uncle-novel/main?logo=travis" alt="Travis Build"/>
    </a>
    <a href="https://github.com/unclezs/jfx-launcher/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/unclezs/uncle-novel?color=%2340C0D0&label=License" alt="GitHub license"/>
    </a>
	<img src="https://img.shields.io/github/downloads/unclezs/uncle-novel/total" alt=""/>
	<img src="https://img.shields.io/badge/openjdk-11-green" alt=""/>
	<img src="https://img.shields.io/badge/platform-win linux mac-green" alt=""/>
</p>

# Uncle 小说

一个运行在 PC 端的应用，提供了全网小说的转码阅读功能。其核心为目录解析功能，辅以书源模式进行（全网小说）小说的内容获取。

本项目仅供学习交流，请勿用于商业用途，软件内产生的数据请关闭软件后立即删除！！

## 主要功能

- 搜书文本小说
- 搜书有声小说
- 全网搜书
- 文本小说书架
- 文本小说阅读器
- 有声小说书架
- 解析下载
- 下载管理
- 书源管理
- 软件设置
- 全局热键
- 主题定制
- 国际化支持
- 备份与恢复（WebDav）

## 预览

### 书架

书架提供了书籍的分组管理功能，同时支持按照分组批量更新书籍，也可以导入本地 TXT 小说到书架。

<img width="600" src="https://github.com/unclezs/uncle-novel/raw/main/app/packager/screenshot/home.png"/>

### 阅读器

<img width="600" src="https://github.com/unclezs/uncle-novel/raw/main/app/packager/screenshot/read.png"/>
<img width="600" src="https://github.com/unclezs/uncle-novel/raw/main/app/packager/screenshot/read1.png"/>

### 软件设置

<img width="600" src="https://github.com/unclezs/uncle-novel/raw/main/app/packager/screenshot/setting.png"/>


## 开发

### 本地点火

准备环境:

- `jdk` 至少需要 JDK11 版本。
- `npm install -g sass` 安装 sass，本项目使用 sass 将 scss 编译为 css。
- `sqlite3`，非必须，如果想重建 sqlite 数据库则需要安装。

然后直接运行 :app:runApp 任务即可。

```shell
./graldew :app:runApp
```

### 打包

```shell
# window 64 位包
./gradlew :app:packageWin64

# window 32 位包
./gradlew :app:packageWin32

# mac 包，如果开发机不是 aarch64(m1) 的则有可能不能在 aarch64 的 mac 上运行
./gradlew :app:packageMac

# linux 包，未测试..应该是不完善的
./gradlew :app:packageLinux
```

## 支持开发者~

<img src="https://cdn.unclezs.com/20210105090216.jpeg" alt=""/>

