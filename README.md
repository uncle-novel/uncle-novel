<p align="center">
    <a href="https://github.com/unclezs/uncle-novel/actions/workflows/build.yml">
        <img src="https://img.shields.io/github/actions/workflow/status/uncle-novel/uncle-novel/build.yml" alt="gradle build"/>
    </a>
    <a href="https://app.netlify.com/sites/uncle-novel-upgrade/deploys">
        <img src="https://api.netlify.com/api/v1/badges/79920580-ee2f-4334-aac0-d0e9424321c0/deploy-status" alt="Netlify"/>
    </a>
    <a href="https://github.com/unclezs/jfx-launcher/blob/main/LICENSE">
        <img src="https://img.shields.io/github/license/unclezs/uncle-novel?color=%2340C0D0&label=License" alt="GitHub license"/>
    </a>
	<img src="https://img.shields.io/badge/openjdk-17-green" alt=""/>
	<img src="https://img.shields.io/badge/platform-win mac-green" alt=""/>
</p>

# Uncle 小说

一个桌面端应用，支持 MacOS/Windows，提供了全网小说的转码阅读功能。其核心为目录解析功能，辅以书源模式进行（全网小说）小说的内容获取。

注意：本项目仅供学习交流，请勿用于商业用途，软件内产生的数据请关闭软件后立即删除！！

## 跨平台阅读器推荐

推荐一款跨平台纯离线电子书阅读器 [Reeden](https://reeden.app/cn)

支持 `Windows`、`Mac`、`Linux`、`iOS`、`Android` 平台。

注意：[Reeden](https://reeden.app/cn) 是纯粹的阅读器，没有解析、书源，需要自己导入 TXT、EPUB 之类的阅读

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

## 贡献代码

### 本地启动

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

# macos 包，如果要打 arm64 的包需要配置 arm64 的 jdk
./gradlew :app:packageMac
```

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=uncle-novel/uncle-novel&type=Date)

