# KrkrDataLoader

## 1.项目概述

### 1.1 项目背景：

该项目旨在为使用 kirikiri 引擎开发的游戏提供一个数据加载工具，能够简化剧情文本、语音、立绘等数据的解析、加载和浏览过程。

该项目采用 Java + Electron 开发，提供了高效、灵活的数据加载方式，并接入 Spring Boot 框架，以支持 RESTful API 的调用。

### 1.2 核心功能：
- [ ] 解包游戏数据，并导出为解密后的资源文件。
- [x] 允许自定义配置剧情文本解析规则，便捷地解析和利用剧情文本数据。
- [x] 简易地浏览、编辑、保存剧情文本数据。
- [x] 支持 Restful API，可直接部署在服务器上进行远程操作。
- [ ] 游戏音频和立绘的加载、导出与还原（复现剧情演绎过程）。
- [ ] 长音频的合成拼接，支持台词同步。
- [ ] 游戏场景的 Web 化复现。

## 2.安装与配置

**开发环境为 Windows，关于 Linux 和 Mac 的调试也许会在更之后进行发布。**

###### ~~其实 Electron 加上 Java，哪里都能跑的吧？~~

### 2.1 开发依赖：
- JDK 17 或更高版本
- Gradle 8.8
- Spring Boot 2.7.15 或更高版本
- Git

### 2.2 发行版安装步骤：
# 稳定版本暂未发行！
- 从 [发行页](https://github.com/pjh456/KrkrDataLoader/releases) 下载最新发行版的 KrkrDataLoader.zip 压缩包。
- 解压压缩包，并运行 KrkrDataLoader.exe 文件，即可启动项目

### 2.3 部署源码步骤：

#### 2.3.1 克隆项目仓库
```shell
git clone https://github.com/pjh456/KrkrDataLoader.git
cd KrkrDataLoader
```

#### 2.3.2 安装依赖并构建项目
由于 KrkrDataLoader 是前后端分离进行开发的，因此在根目录下还需要打开对应的 Java 后端项目。
```shell
cd KrkrDataLoader
./gradlew clean build
```

#### 2.3.3 运行项目
```shell
cd build/classes/java/main
java Main
```

## 3.功能模块

### 3.1 数据解密

#### 功能概述

**暂未实现，未来将接入外部项目实现，并逐步替换为自制版本。**

- 对游戏数据进行解密，并导出为解密后的资源文件。
- 将剧情文本导出为 JSON 文件。
- 将游戏音频导出为 WAV、OGG 或 MP3 文件。
- 将立绘导出为 PNG、JPG 或 BMP 文件。

### 3.2 自定义配置 JSON 剧情文件解析规则

#### 功能概述
- 提供了命令行和 RESTful API 版本的读取和指定 JSON 路径的方式，以支持解析规则的自定义。
- 提供了 JSON 文件版本的配置文件指定方式，以指定自定义配置规则文件路径。
- 提供了 RESTful API 版本的上传配置文件方式，以支持远程加载的配置文件。
- 提供了命令行和 RESTful API 版本的配置文件保存方式，以支持配置文件的保存与获取。

#### 使用方法
通过配置 [``configs.json``](KrkrDataLoader/src/main/resources/configs.json) 来设置规则。

其中每个键值代表的都是其对应上一层级的，在 JSON 文件中的**相对路径**。

**每个键值都应该对应一个JsonArray。用数字来表示在[]中的位置，用字符串来表示在{}中的位置。**

键值名称是指定的，其含义如下：
- scenes_name：场景集合名称路径（该场景文件对应的名称）。
- scene：所有场景所处的 JsonArray 路径（场景的集合由若干个场景组成，位于同一个 JsonArray 之中）。
- scene_label：场景名称路径（场景所对应的名称）。
- dialogues：一个场景中，所有对话所处的 JsonArray 路径（多条对话位于同一个 JsonArray 之中）。
- speaker：一条对话的发言人路径（可以为空，即主角内心独白）。
- content：一条对话的内容路径。
- voice：一条对话的语音路径（可以为空）。

而键值对应嵌套结构如下（已经省略了中间路径和无关键值）：
```
Scenes -> scenes_name
└── SceneArray -> scene
    ├── Scene1 -> scene_label
    │   ├── DialogueArray -> dialogues
    │   │   ├── Dialogue1
    │   │   │   ├── speaker -> speaker
    │   │   │   ├── content -> content
    │   │   │   └── voice -> voice
    │   │   ├── Dialogue2
    │   │   │   └── ...
    │   │   ├── Dialogue3
    │   │   │   └── ...
    │   │   └── ...
    ├── Scene2 -> scene_label
    │   └── ...
    ├── Scene3 -> scene_label
    │   └── ...
    └── ...
    
```

### 3.3 浏览、编辑、保存剧情文本数据：

#### 功能概述
- 通过 Electron 前端页面选取并上传剧情文本文件到 Java 后端进行解析。
- 获取处理后的剧情文本数据，并显示在前端的 Monaco Editor 上。
- 通过树状结构展示剧情文本架构，并在选中对应部分时显示相应内容（支持多个剧情文本文件同时存在）。
- 通过 Monaco Editor 对文本内容进行修改，并将修改好的内容保存到指定位置。

具体的操作方式可以直接打开可执行文件（发行版）或 [``menu.html``](KrkrDataLoaderGUI/pages/menu.html)（源码）了解。

注意：直接浏览 HTML 时，上述功能中与服务器交互的功能均不可用，需要同时运行 [后端项目](KrkrDataLoader)。

### 3.4 支持 Restful API，可直接部署在服务器上进行远程操作。

#### 功能概述
- 通过访问 API 实现其他所有支持的功能。

运行 [后端项目](KrkrDataLoader) 后访问 http://localhost:8080/swagger-ui.html 即可获知 API 文档。

### 3.5 游戏音频和立绘的加载、导出与还原（复现剧情演绎过程）。

#### 功能概述

**暂未实现**

### 3.6 长音频的合成拼接，支持台词同步。

#### 功能概述

**暂未实现**

### 3.7 游戏场景的 Web 化复现。

**暂未实现**

#### 功能概述


## 4.使用示例

### 4.1 手动配置解析规则示例

#### 4.1.1 根据解密的剧情文件内容编写规则文件

有 JSON 剧情文本文件如下：
```json
{
  "name": "Whole Scene File",
  "scenes": [
    {
      "label": "Scene1",
      "texts": [
        [
          {
            "speaker": "Player",
            "content": "Hello, World!"
          },
          {
            "voice": "voice1.wav"
          },
          [
            "Something else"
          ]
        ],
        [
          {
            "speaker": "NPC",
            "content": "Hello, Player!"
          },
          {
            "voice": "voice2.wav"
          },
          [
            "Something else"
          ]
        ]
      ]
    },
    {
      "label": "Scene2",
      "texts": [
        [
          {
            "speaker": "Player",
            "content": "How are you?"
          },
          {
            "voice": "voice3.wav"
          },
          [
            "Something else"
          ]
        ],
        [
          {
            "speaker": "NPC",
            "content": "I'm fine, thank you."
          },
          {
            "voice": "voice4.wav"
          },
          [
            "Something else"
          ]
        ]
      ]
    }
  ]
}
```

其解析规则为：
```json
{
  "scenes_name": [
    "name"
  ],
  "scene": [
    "scenes"
  ],
  "scene_label": [
    "label"
  ],
  "dialogues": [
    "texts"
  ],
  "speaker": [
    0,"speaker"
  ],
  "content": [
    0,"content"
  ],
  "voice": [
    1,"voice"
  ]
}
```

#### 4.1.2 修改项目设置，指定配置文件路径

在 [后端项目根目录](KrkrDataLoader) 以内任意位置（使用源码运行时）或使用相对于 KrkrDataLoader.exe 的路径（使用发行版时）新建一个配置文件。

设置 [Settings.json](KrkrDataLoader/src/main/resources/settings.json) 中的 ``config_path`` 字段为新的配置文件路径（推荐使用相对路径）：
```json
{
  "config_path": "KrkrDataLoader/src/main/resources/configs.json",
  "something": "else"
}
```

#### 4.1.3 重新运行项目

重新运行后即可更新为新的配置文件。

## 5.开发者文档

详见 [中文开发者文档](docs/README-zh.md) 及 [英文开发者文档](docs/README-en.md)。