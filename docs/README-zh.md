# KrkrDataLoader 开发者文档

## 1.源码架构

```
KrkrDataLoader
├── KrkrDataLoaderGUI       # 前端 Electron 项目
│   │   │   ├── speaker -> speaker
└── KrkrDataLoader          # 后端 Java 项目
    └── src                     # 源码部分
        ├── main                    # 发行部分
        │   ├── java                    # 项目源码
        │   │   └── KrkrDataLoader          # JAR 包
        │   │       ├── Main.java               # 项目入口
        │   │       ├── core                    # 剧情文本数据处理
        │   │       │   ├── ParentChild.java        # 所有需要存储父子双向关系类的基类
        │   │       │   ├── KrkrData.java           # 剧情文本数据基类，继承自 ParentChild
        │   │       │   ├── KrkrVoice.java          # 剧情对话音频类，继承自 KrkrData，已废弃
        │   │       │   ├── KrkrDialogue.java       # 剧情对话数据类，继承自 KrkrData，包含一条 KrkrVoice
        │   │       │   ├── KrkrScene.java          # 剧情场景类，继承自 KrkrData，包含若干 KrkrDialogue
        │   │       │   ├── KrkrScenes.java         # 剧情场景集合类，继承自 KrkrData，包含若干 KrkrScene
        │   │       │   └── KrkrUtils.java          # 数据加载工具类
        │   │       ├── config                  # 配置文件加载
        │   │       │   ├── SingleConfig.java       # 配置数据处理路径类
        │   │       │   ├── Configs.java            # 配置处理集合类，包含若干 SingleConfig
        │   │       │   └── GlobalConfig.java       # 全局配置，可以获取和加载 Configs 作为全局使用配置
        │   │       ├── setting                 # 设置状态加载
        │   │       │   ├── SingleSetting.java      # 设置状态存储类
        │   │       │   ├── Settings.java           # 设置状态集合类，包含若干 SingleSetting
        │   │       │   └── GlobalSettings.java     # 全局配置，可以获取和加载 Settings 作为全局使用配置
        │   │       ├── json                    # JSON 文件处理
        │   │       │   ├── JsonPath.java           # JSON 文件中的元素路径
        │   │       │   └── JsonFile.java           # JSON 文件，由 JsonPath 嵌套而成
        │   │       ├── command             # 命令行工具
        │   │       │   └── ConfigCommander.java    # 配置路径命令化处理类，可以根据指令加载路径并设置配置文件
        │   │       └── network             # Spring Boot 项目（服务器 API 设置）
        │   │           ├── controller      # Spring Boot 控制器
        │   │           │   ├── KrkrSceneController.java    # 场景文件处理控制器
        │   │           │   └── KrkrConfigController.java   # 配置文件处理控制器
        │   │           ├── service         # Spring Boot 服务
        │   │           │   ├── KrkrSceneFileService.java   # 场景文件上传服务
        │   │           │   ├── KrkrSceneInfoService.java   # 场景文件信息服务
        │   │           │   ├── KrkrSceneTextService.java   # 场景文本获取服务
        │   │           │   └── KrkrConfigFileService.java  # 配置文件上传服务
        │   │           ├── response        # 响应体
        │   │           │   ├── KrkrResponse.java           # 响应体
        │   │           │   ├── KrkrResponseBuilder.java    # 响应体建造者
        │   │           │   └── KrkrResponseFactory.java    # 响应体工厂
        │   │           ├── RangeCallBack.java           # 范围回调接口，用于处理范围请求 
        │   │           └── KrkrDocConfig.java           # SpringDoc 配置
        │   └── resources           # 配置文件及部分废弃文件
        │   
        └── test                    # 测试部分
            ├── java                # 单元测试源码（暂时未空）
            └── resources           # 测试样例数据及部分废弃文件
```

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