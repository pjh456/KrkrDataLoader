# KrkrDataLoader 开发者文档

## 1.项目概述

### 1.1 项目背景

KrkrDataLoader 是一个处理剧情文本数据并支持图像/音频导入处理的工具。

项目包括前端与后端两部分，前端通过 Electron 渲染界面，后端使用 Java 和 Spring Boot 提供 API 支持。

该工具可以从不同格式的文件中提取数据，进行格式化并返回到前端进行展示。

### 1.2 目标

- [ ] 解包游戏数据，并导出为解密后的资源文件。
- [x] 支持自定义配置文件和命令行工具的扩展。
- [x] 提供一个可视化工具，允许用户上传和管理剧本数据。
- [x] 提供 API 支持，允许其他系统通过 RESTful API 访问和操作数据。
- [ ] 游戏音频和立绘的加载、导出与还原（复现剧情演绎过程）。
- [ ] 长音频的合成拼接，支持台词同步。
- [ ] 游戏场景的 Web 化复现。

## 1.3 项目历史

| 逻辑端               | 渲染端         | 重构原因                     | 重构结果                               |
|-------------------|-------------|--------------------------|------------------------------------|
| Python            | PySimpleGUI | 实现 KrkrDataLoader 全部功能   | 完成了游戏解包、剧情读取、音频播放和立绘浏览的功能          |
| C++               | 无           | 提供剧情文本解析的配置自动化           | 逻辑端数据存储部分完成，由于 JSON 库和文本编码问题暂时停止维护 |
| Java              | JavaFx      | 继承 C++ 版本的面向对象设计，并提供完整实现 | 实现了剧情文本的加载、配置                      |
| Java + SpringBoot | Electron    | 提供更优雅易用的界面设计，并支持更多功能     | 正在进行中，当前除继承上一版本功能外，已完成了配置文件的自动化指令  |

## 2.项目架构

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

## 3. 源码类说明

所有属性均拥有 getter，部分拥有 setter。

除非有特殊说明，否则 getter 和 setter 不在方法中注明。

每个方法的所有参数版本均会被列出以简化理解。

### 源码类继承架构

```
ParentChild
├── KrkrData
│   ├── KrkrVoice
│   ├── KrkrDialogue
│   ├── KrkrScene
│   └── KrkrScenes
└── JsonPath (in json package)
```

### `core` 核心类说明

#### 1. `ParentChild`

`ParentChild` 是所有需要存储父子双向关系类的基类。

##### 构造函数

| 构造函数                                           | 参数                                  | 描述                               |
|------------------------------------------------|-------------------------------------|----------------------------------|
| `ParentChild(String name, ParentChild parent)` | `String name`, `ParentChild parent` | 构建一个带父节点的 `ParentChild` 实例。      |
| `ParentChild(String name)`                     | `String name`                       | 构建一个不带父节点的 `ParentChild` 实例。     |
| `ParentChild(ParentChild parent)`              | `ParentChild parent`                | 构建一个带父节点的默认名称 `ParentChild` 实例。  |
| `ParentChild()`                                | 无                                   | 构建一个不带父节点的默认名称 `ParentChild` 实例。 |

##### 属性

| 字段名           | 含义    | 类型                         | 默认值                     |
|---------------|-------|----------------------------|-------------------------|
| `parent`      | 父节点   | `ParentChild`              | `null`                  |
| `childrenMap` | 子节点集合 | `Map<String, ParentChild>` | `new LinkedHashMap<>()` |
| `name`        | 节点名称  | `String`                   | `"default"`             |

##### 方法

| 方法名                                                                | 参数                                     | 返回值                 | 描述                                                 |
|--------------------------------------------------------------------|----------------------------------------|---------------------|----------------------------------------------------|
| `void addChild(String name, ParentChild child)`                    | `String name`, `ParentChild child`     | `void`              | 添加子节点到集合指定键值。添加时同时设置子节点的父节点。                       |
| `void addChild(ParentChild child)`                                 | `ParentChild child`                    | `void`              | 添加子节点到集合，子节点键值为 `child.getName()`。                 |
| `void addAllChildren(Map<String, ParentChild> childrenMap)`        | `Map<String, ParentChild> childrenMap` | `void`              | 添加 `Map<String, ParentChild>` 中所有子节点键值对到集合。        |
| `void addAllChildren(List<ParentChild> children)`                  | `List<ParentChild> children`           | `void`              | 添加 `List<ParentChild>` 中所有子节点到集合。                  |
| `void addAllChildren(ParentChild... children)`                     | `ParentChild... children`              | `void`              | 添加所有子节点到集合。                                        |
| `List<ParentChild> listChildren()`                                 | 无                                      | `List<ParentChild>` | 获取所有子节点列表。                                         |
| `List<String> listChildrenName()`                                  | 无                                      | `List<String>`      | 获取所有子节点名称列表。                                       |
| `ParentChild getChild(String name)`                                | `String name`                          | `ParentChild`       | 通过指定键值获取指定子节点。                                     |
| `ParentChild getChild(int index) throws IndexOutOfBoundsException` | `int index`                            | `ParentChild`       | 通过指定索引获取指定子节点，索引越界时抛出 `IndexOutOfBoundsException`。 |
| `List<ParentChild> listAbsolutePath()`                             | 无                                      | `List<ParentChild>` | 获取绝对路径。                                            |
| `List<String> listAbsolutePathName()`                              | 无                                      | `List<String>`      | 获取绝对路径名称。                                          |
| `void close() throws Exception`                                    | 无                                      | `void`              | 关闭所有子节点。实现 `AutoCloseable` 接口，确保资源正确释放。            |

##### 异常

| 异常                          | 触发原因                |
|-----------------------------|---------------------|
| `IndexOutOfBoundsException` | 当获取子节点时，索引越界时抛出该异常。 |

---

#### 2. `KrkrData extends ParentChild`

`KrkrData` 是所有剧情文本数据的基类，所有剧情文本的嵌套架构都继承自 `KrkrData`。

##### 构造函数

| 构造函数                    | 参数            | 描述                           |
|-------------------------|---------------|------------------------------|
| `KrkrData(String name)` | `String name` | 使用指定的名称创建一个新的 `KrkrData` 实例。 |

##### 字段

| 字段名           | 含义      | 类型                         | 默认值                     |
|---------------|---------|----------------------------|-------------------------|
| `parent`      | 父节点     | `ParentChild`              | `null`                  |
| `childrenMap` | 子节点集合   | `Map<String, ParentChild>` | `new LinkedHashMap<>()` |
| `name`        | 节点名称    | `String`                   | `"default"`             |
| `data`        | 剧情文本数据  | `JsonElement`              | `null`                  |
| `is_init`     | 是否初始化标识 | `boolean`                  | `false`                 |

##### 方法

| 方法名                 | 参数 | 返回值    | 描述                |
|---------------------|----|--------|-------------------|
| `void initialize()` | 无  | `void` | 初始化剧情数据，为延迟初始化创建。 |

##### 异常

| 异常          | 触发原因            |
|-------------|-----------------|
| `Throwable` | 初始化时出现异常时抛出该异常。 |

---

#### 3. `KrkrVoice extends KrkrData`

`KrkrVoice` 类表示 `KrkrDialogue` 中的语音数据。

它包含了语音的文件路径和该语音的名称。

##### 构造函数：

| 构造函数                                  | 参数                         | 描述                          |
|---------------------------------------|----------------------------|-----------------------------|
| `KrkrVoice(String name, String path)` | `String name, String path` | 使用指定名称和路径创建 `KrkrVoice` 实例。 |

##### 字段

| 字段名           | 含义      | 类型                         | 默认值                     |
|---------------|---------|----------------------------|-------------------------|
| `parent`      | 父节点     | `ParentChild`              | `null`                  |
| `childrenMap` | 子节点集合   | `Map<String, ParentChild>` | `new LinkedHashMap<>()` |
| `name`        | 节点名称    | `String`                   | `"default"`             |
| `data`        | 剧情文本数据  | `JsonElement`              | `null`                  |
| `is_init`     | 是否初始化标识 | `boolean`                  | `false`                 |
| `path`        | 语音文件路径  | `String`                   | `null`                  |

##### 方法

已废弃，不再支持。

| 方法名           | 参数 | 返回值    | 描述                           |
|---------------|----|--------|------------------------------|
| `void play()` | 无  | `void` | 如果语音对象存在，播放该语音。**已废弃，不再支持。** |
| `void stop()` | 无  | `void` | 如果语音对象存在，停止该语音。**已废弃，不再支持。** |

##### 异常

无

---

#### 4. `KrkrDialogue extends KrkrData`

`KrkrDialogue` 类表示 `KrkrScene` 中的单条对话数据。它包含了对话的发言者、内容和可选的语音对象。

##### 构造函数

| 构造函数                                                           | 参数                                | 描述                                                                |
|----------------------------------------------------------------|-----------------------------------|-------------------------------------------------------------------|
| `KrkrDialogue(String name)`                                    | `String name`                     | 使用指定的名称创建一个新的 `KrkrDialogue` 实例。**不包含对话数据**。                      |
| `KrkrDialogue(String name, JsonElement data) throws Throwable` | `String name`, `JsonElement data` | 使用指定的名称和 JSON 数据创建一个新的 `KrkrDialogue` 实例，从 数据中提取信息，并根据配置加载对应数据路径。 |

##### 字段

| 字段名           | 含义    | 类型                         | 默认值                             |
|---------------|-------|----------------------------|---------------------------------|
| `parent`      | 父节点   | `ParentChild`              | `null`                          |
| `childrenMap` | 子节点集合 | `Map<String, ParentChild>` | `new LinkedHashMap<>()`         |
| `name`        | 节点名称  | `String`                   | `"default"`                     |
| `data`        | 数据    | `JsonElement`              | `null`                          |
| `is_init`     | 初始化标志 | `boolean`                  | `false`                         |
| `speaker`     | 发言者   | `String`                   | 从 `GlobalSetting` 中获取的当前设置的发言者值 |
| `content`     | 内容    | `String`                   | 从 `GlobalSetting` 中获取的当前设置的内容值  |
| `voice`       | 语音对象  | `KrkrVoice`                | `null`                          |

##### 方法

| 方法名           | 参数 | 返回值    | 描述                          |
|---------------|----|--------|-----------------------------|
| `void play()` | 无  | `void` | 如果存在语音对象，播放该语音。**已废弃，不再支持** |
| `void stop()` | 无  | `void` | 如果存在语音对象，停止该语音。**已废弃，不再支持** |

##### 异常

| 异常          | 触发原因                                     |
|-------------|------------------------------------------|
| `Throwable` | 在构造函数中，如果 JSON 数据不包含所需字段或数据格式不正确，将抛出该异常。 |

---

#### 5. `KrkrScene extends KrkrData`

`KrkrScene` 类表示 `KrkrScene` 中的一整个场景。

它包括其中的所有 `KrkrDialogue` 对话，负责管理该场景的对话内容。

##### 构造函数：

| 构造函数                                                             | 参数                                     | 描述                                                                         |
|------------------------------------------------------------------|----------------------------------------|----------------------------------------------------------------------------|
| `KrkrScene(JsonElement data, boolean init_now) throws Throwable` | `JsonElement data`, `boolean init_now` | 通过提供的 `JsonElement` 数据创建一个新的 `KrkrScene` 实例，并根据 `init_now` 标志决定是否立即初始化该场景。 |
| `KrkrScene(JsonElement data) throws Throwable`                   | `JsonElement data`                     | 通过提供的 `JsonElement` 数据创建一个新的 `KrkrScene` 实例，并立即初始化。                        |

##### 字段：

| 字段名           | 含义    | 类型                         | 默认值                     |
|---------------|-------|----------------------------|-------------------------|
| `parent`      | 父节点   | `ParentChild`              | `null`                  |
| `childrenMap` | 子节点集合 | `Map<String, ParentChild>` | `new LinkedHashMap<>()` |
| `name`        | 节点名称  | `String`                   | `"default"`             |
| `data`        | 数据    | `JsonElement`              | `null`                  |
| `is_init`     | 初始化标志 | `boolean`                  | `false`                 |

##### 方法：

| 方法名                                  | 参数 | 返回值            | 描述                                                                                                                                   |
|--------------------------------------|----|----------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `void initialize() throws Throwable` | 无  | `void`         | 初始化场景数据。该方法从 JSON 数据中提取 `dialogues` 字段，并为每个对话创建一个 `KrkrDialogue` 实例，最后将这些对话添加为子节点。初始化完成后，设置 `is_init` 为 `true`，并将 `data` 设置为 `null`。 |
| `List<String> listDialogues()`       | 无  | `List<String>` | 获取当前场景中的所有对话内容。返回一个 `List<String>`，每个元素是场景中的一条对话的格式化输出。                                                                              |

##### 异常：

| 异常          | 触发原因                                                   |
|-------------|--------------------------------------------------------|
| `Throwable` | 在 `initialize()` 方法中，如果 JSON 数据不包含所需字段或数据格式不正确，将抛出该异常。 |

#### 6. `KrkrScenes extends KrkrData`

`KrkrScenes` 类表示一个完整的剧情场景集合。

它负责管理多个 `KrkrScene` 剧情场景实例。

##### 构造函数：

| 构造函数                                                                | 参数                                       | 描述                                                                                |
|---------------------------------------------------------------------|------------------------------------------|-----------------------------------------------------------------------------------|
| `KrkrScenes(JsonElement data, boolean init_now) throws Throwable`   | `JsonElement data`, `boolean init_now`   | 通过提供的 JSON 数据创建一个新的 `KrkrScenes` 实例，是否初始化由 `init_now` 参数控制。                       |
| `KrkrScenes(String path, boolean init_now) throws Throwable`        | `String path`, `boolean init_now`        | 通过提供的本地文件路径加载 JSON 数据并创建一个新的 `KrkrScenes` 实例，是否初始化由 `init_now` 参数控制。              |
| `KrkrScenes(File file, boolean init_now) throws Throwable`          | `File file`, `boolean init_now`          | 通过提供的文件对象加载 JSON 数据并创建一个新的 `KrkrScenes` 实例，是否初始化由 `init_now` 参数控制。                |
| `KrkrScenes(MultipartFile file, boolean init_now) throws Throwable` | `MultipartFile file`, `boolean init_now` | 通过提供的 `MultipartFile` 对象加载 JSON 数据并创建一个新的 `KrkrScenes` 实例，是否初始化由 `init_now` 参数控制。 |
| `KrkrScenes(String path) throws Throwable`                          | `String path`                            | 通过提供的本地文件路径加载 JSON 数据并创建一个新的 `KrkrScenes` 实例，并立即初始化。                              |
| `KrkrScenes(File file) throws Throwable`                            | `File file`                              | 通过提供的文件对象加载 JSON 数据并创建一个新的 `KrkrScenes` 实例，并立即初始化。                                |
| `KrkrScenes(MultipartFile file) throws Throwable`                   | `MultipartFile file`                     | 通过提供的 `MultipartFile` 对象加载 JSON 数据并创建一个新的 `KrkrScenes` 实例，并立即初始化。                 |

##### 字段：

| 字段名           | 含义    | 类型                         | 默认值                     |
|---------------|-------|----------------------------|-------------------------|
| `parent`      | 父节点   | `ParentChild`              | `null`                  |
| `childrenMap` | 子节点集合 | `Map<String, ParentChild>` | `new LinkedHashMap<>()` |
| `name`        | 节点名称  | `String`                   | `"default"`             |
| `data`        | 数据    | `JsonElement`              | `null`                  |
| `is_init`     | 初始化标志 | `boolean`                  | `false`                 |

##### 方法：

| 方法名                                  | 参数 | 返回值    | 描述                                                                                                                                                                 |
|--------------------------------------|----|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `void initialize() throws Throwable` | 无  | `void` | 初始化场景集合数据。该方法从 JSON 数据中提取 `scenes` 字段，为每个场景创建一个新的 `KrkrScene` 实例，并将其添加为子节点。然后，它为每个 `KrkrScene` 创建一个新线程进行初始化，所有线程执行完毕后，将 `data` 设置为 `null`，并将 `is_init` 设置为 `true`。 |

##### 异常

| 异常          | 触发原因                                                   |
|-------------|--------------------------------------------------------|
| `Throwable` | 在 `initialize()` 方法中，如果 JSON 数据不包含所需字段或数据格式不正确，将抛出该异常。 |

#### 7. 核心工具类 `KrkrUtils`

`KrkrUtils` 是一个工具类，主要提供与 JSON 文件相关的加载、解析功能。

它支持从多种来源加载 JSON 文件，包括 `MultipartFile`、`File` 和路径，并提供对文件夹中 JSON 文件的批量加载功能。

该类还包含对文件类型和目录的验证方法，以确保正确处理文件和路径。

##### 方法说明

| 方法名                                              | 参数                      | 返回值                | 描述                                                                                                                                          |
|--------------------------------------------------|-------------------------|--------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| `JsonObject loadJsonFile(MultipartFile file)`    | `MultipartFile file`    | `JsonObject`       | 从 `MultipartFile` 类型的文件中加载并解析 JSON 数据。当文件解析过程中发生错误时，抛出 `IOException`。                                                                       |
| `JsonObject loadJsonFile(File file)`             | `File file`             | `JsonObject`       | 从指定的 `File` 类型文件中加载并解析 JSON 数据。当文件解析过程中发生错误时，抛出 `IOException`。                                                                              |
| `JsonObject loadJsonFile(String path)`           | `String path`           | `JsonObject`       | 从指定的本地文件路径加载并解析 JSON 文件。如果路径不是一个有效的文件，抛出 `FileNotFoundException`。如果文件类型不是 `.json`，抛出 `InvalidTypeException`。当文件解析过程中发生错误时，抛出 `IOException`。 |
| `List<JsonObject> loadJsonFolder(String path)`   | `String path`           | `List<JsonObject>` | 加载指定本地文件夹路径下所有符合条件的 JSON 剧情文本文件（`.ks.json` 扩展名）。如果指定路径不是一个文件夹，抛出 `FileNotFoundException`。当遍历文件夹或读取文件时发生错误，抛出 `IOException`。                 |
| `boolean isFile(String path)`                    | `String path`           | `boolean`          | 检查指定路径是否是一个文件。                                                                                                                              |
| `boolean isFolder(String path)`                  | `String path`           | `boolean`          | 检查指定路径是否是一个文件夹。                                                                                                                             |
| `JsonObject loadJsonFile(BufferedReader reader)` | `BufferedReader reader` | `JsonObject`       | 从 `BufferedReader` 中读取 JSON 数据并解析。当文件解析过程中发生错误时，抛出 `IOException`。                                                                           |

##### 内部实现

- `loadJsonFile` 方法通过读取文件内容并使用 `Gson` 库将其解析为 `JsonObject` 类型。对于不同的文件输入类型，提供了不同的加载方式（
  `MultipartFile`、`File`、路径）。
- `loadJsonFolder` 方法遍历指定目录中的文件，筛选出符合条件（`.ks.json` 扩展名）的文件，并将其内容加载为 `JsonObject`
  对象，最终返回一个包含所有 `JsonObject` 数据的列表。

##### 使用示例

###### 加载单个 JSON 文件:

```java
MultipartFile multipartFile = null;
File file = null;
String path = "path/to/file";

JsonObject jsonObject1 = KrkrUtils.loadJsonFile(multipartFile);
JsonObject jsonObject2 = KrkrUtils.loadJsonFile(file);
JsonObject jsonObject3 = KrkrUtils.loadJsonFile(path);
```

###### 加载指定路径下的所有 JSON 文件:

```java
String folderPath = "path/to/folder";
List<JsonObject> jsonList = KrkrUtils.loadJsonFolder(folderPath);
```

###### 验证文件是否为文件或文件夹:

```java
boolean isFile = KrkrUtils.isFile("path/to/file");
boolean isFolder = KrkrUtils.isFolder("path/to/folder");
```

##### 注意事项

`loadJsonFolder` 方法仅支持加载扩展名为 `.ks.json` 的文件。如果目录中存在其他格式的文件，它们将被忽略。

文件路径和文件名的大小写在不同操作系统中可能有所不同，需注意路径的准确性。

### 3.3 `config` 配置类说明

所有字段均拥有 getter，部分拥有 setter。

每个方法的所有参数版本均会被列出以简化理解。

#### 1. `SingleConfig`

`SingleConfig` 类是中用于解析和匹配剧情文本数据的配置类。

它主要用于自定义配置 JSON 剧情文件的解析规则，以便从结构化的 JSON 数据中提取所需的内容。

##### 构造函数

| 构造函数                                                | 参数                                      | 描述                                                                                                                                                            |
|-----------------------------------------------------|-----------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `SingleConfig(String name, List<Object> fieldList)` | `String name`, `List<Object> fieldList` | 使用指定的配置名称和字段列表初始化 `SingleConfig` 实例。`fieldList` 为 `null` 时抛出 `NullPointerException`。如果 `fieldList` 包含非 `String` 或 `Integer` 类型时抛出 `IllegalArgumentException`。 |
| `SingleConfig(String name)`                         | `String name`                           | 使用指定的 `name` 初始化 `SingleConfig` 实例，`fieldList` 默认为 `null`。                                                                                                    |

##### 字段

| 字段名          | 含义                | 类型                   | 默认值                 |
|--------------|-------------------|----------------------|---------------------|
| `name`       | 配置名称              | `String`             | -                   |
| `fieldsList` | 字段列表，用于解析 JSON 数据 | `List<List<Object>>` | `new ArrayList<>()` |

##### 方法

| 方法名                                                                                | 参数                                           | 返回值                  | 描述                                                                                                                                                |
|------------------------------------------------------------------------------------|----------------------------------------------|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `void addFields(List<Object> fieldList)`                                           | `List<Object> fieldList`                     | `void`               | 添加一组新的字段列表，用于解析 JSON 数据。`fieldList` 为 `null` 时抛出 `NullPointerException`。如果 `fieldList` 包含非 `String` 或 `Integer` 类型时抛出 `IllegalArgumentException`。 |
| `List<List<Object>> getFieldsList()`                                               | 无                                            | `List<List<Object>>` | 获取当前配置中所有的字段列表。                                                                                                                                   |
| `void clearFields()`                                                               | 无                                            | `void`               | 清空当前所有的字段列表。                                                                                                                                      |
| `static boolean checkFields(List<Object> fieldList)`                               | `List<Object> fieldList`                     | `boolean`            | 检查给定的字段列表是否仅包含 `String` 和 `Integer` 类型的元素。                                                                                                        |
| `JsonObject matchValueAsJsonObject(JsonElement data)`                              | `JsonElement data`                           | `JsonObject`         | 根据配置中的字段列表匹配并返回 `JsonObject` 类型的数据。                                                                                                               |
| `JsonArray matchValueAsJsonArray(JsonElement data)`                                | `JsonElement data`                           | `JsonArray`          | 根据配置中的字段列表匹配并返回 `JsonArray` 类型的数据。                                                                                                                |
| `JsonPrimitive matchValueAsJsonPrimitive(JsonElement data)`                        | `JsonElement data`                           | `JsonPrimitive`      | 根据配置中的字段列表匹配并返回 `JsonPrimitive` 类型的数据。                                                                                                            |
| `private JsonElement matchValueAsJsonELement(JsonElement data)`                    | `JsonElement data`                           | `JsonElement`        | 根据配置中的所有字段列表匹配并返回匹配的数据。当前版本部分支持多模式匹配，如果存在匹配项则返回**第一项先匹配到的**，否则返回 `null`。                                                                          |
| `private JsonElement matchValueFromData(JsonElement data, List<Object> fieldList)` | `JsonElement data`, `List<Object> fieldList` | `JsonElement`        | 根据给定的单组字段列表和 JSON 数据，递归匹配并返回相应的 `JsonElement`。如果字段类型与数据不匹配时，抛出 `NoSuchFieldException`。                                                            |

##### 异常
| 异常                         | 触发原因                                        |
|----------------------------|---------------------------------------------|
| `NullPointerException`     | 当字段列表为 `null` 时，抛出此异常。                      |
| `IllegalArgumentException` | 当字段列表包含非 `String` 或 `Integer` 类型的元素时，抛出此异常。 |
| `NoSuchFieldException`     | 当字段类型与数据不匹配时，抛出此异常。                         |

---

#### 2. `Configs`

`Configs` 类负责处理配置剧情文件解析规则的加载、使用和保存操作。

它支持从多种来源（如 `JsonObject`、`File`、`MultipartFile` 和本地文件路径）加载配置，并提供相应的方法来验证和保存配置。

该类通过管理多个 `SingleConfig` 对象来处理各种配置项，确保必要的配置项齐全。

##### 构造函数

| 构造函数                                           | 参数                   | 描述                            |
|------------------------------------------------|----------------------|-------------------------------|
| `Configs()`                                    | 无                    | 创建一个空的 `Configs` 对象。          |
| `Configs(JsonObject data)`                     | `JsonObject data`    | 使用提供的 `JsonObject` 数据加载配置。    |
| `Configs(String path) throws Throwable`        | `String path`        | 使用提供的文件路径加载配置。                |
| `Configs(File file) throws Throwable`          | `File file`          | 使用提供的 `File` 对象加载配置。          |
| `Configs(MultipartFile file) throws Throwable` | `MultipartFile file` | 使用提供的 `MultipartFile` 文件加载配置。 |

##### 字段

| 字段名                         | 含义            | 类型                          | 默认值                                                                                                |
|-----------------------------|---------------|-----------------------------|----------------------------------------------------------------------------------------------------|
| `configMap`                 | 配置映射表         | `Map<String, SingleConfig>` | `new HashMap<>()`                                                                                  |
| `(static) necessaryConfigs` | 必需的配置列表，不支持修改 | `List<String>`              | `Arrays.asList("scenes_name", "scene_label", "scene", "dialogues", "speaker", "content", "voice")` |

##### 方法
| 方法名                                                               | 参数                                   | 返回值            | 描述                                                                   |
|-------------------------------------------------------------------|--------------------------------------|----------------|----------------------------------------------------------------------|
| `void loadFromJson(JsonObject data)`                              | `JsonObject data`                    | `void`         | 从传入的 `JsonObject` 数据中加载配置。遍历 JSON 数据中的每个配置项，并解析其内容。                  |
| `void loadFromJson(String path) throws Throwable`                 | `String path`                        | `void`         | 从指定路径加载 JSON 配置文件并解析。                                                |
| `void loadFromJson(File file) throws Throwable`                   | `File file`                          | `void`         | 从指定的 `File` 对象加载 JSON 配置文件并解析。                                       |
| `void loadFromJson(MultipartFile file) throws Throwable`          | `MultipartFile file`                 | `void`         | 从上传的 `MultipartFile` 文件加载 JSON 配置并解析。                                |
| `void save(String path) throws NullPointerException, IOException` | `String path`                        | `void`         | 将当前的配置保存到指定的文件路径。检查必要的配置项是否存在，如果缺少必需的配置项，则抛出 `NullPointerException`。 |
| `static String checkConfigs(Configs configs)`                     | `Configs configs`                    | `String`       | 检查传入的 `Configs` 对象是否包含所有必要的配置项。返回缺失配置项的名称，如果没有缺失则返回 `null`。          |
| `SingleConfig getConfig(String name)`                             | `String name`                        | `SingleConfig` | 根据名称获取配置对象。                                                          |
| `void setConfig(String name, SingleConfig config)`                | `String name`, `SingleConfig config` | `void`         | 设置或更新名为 `name` 的配置对象。                                                |
| `void setConfig(SingleConfig config)`                             | `SingleConfig config`                | `void`         | 根据 `SingleConfig` 对象的名称设置或更新配置对象。                                    |
| `void removeConfig(String name)`                                  | `String name`                        | `void`         | 移除名为 `name` 的配置对象。                                                   |
| `void clear()`                                                    | 无                                    | `void`         | 清空所有配置对象。                                                            |


##### 注意事项

- 确保传入的配置文件格式正确，并符合预期的 JSON 结构。
- 如果缺少必要的配置项，`save()` 方法会抛出 `NullPointerException`，因此在调用该方法之前，建议使用 `checkConfigs()` 进行验证。

---

#### 3. `GlobalConfig`

`GlobalConfig` 类用于全局管理 `Configs` 对象的加载、保存及获取。

它是一个静态类，负责操作当前配置实例。通过该类，可以方便地访问、修改和保存全局配置。

支持从多种来源（如 JSON 数据、`File``、`MultipartFile`、文件路径）加载配置。

##### 属性
| 属性名              | 含义     | 类型        | 默认值    |
|------------------|--------|-----------|--------|
| `currentConfigs` | 当前配置实例 | `Configs` | `null` |

##### 方法

| 方法名                                                                                    | 参数                   | 返回值       | 描述                                                                    |
|----------------------------------------------------------------------------------------|----------------------|-----------|-----------------------------------------------------------------------|
| `static void loadFromJson(JsonObject data)`                                            | `JsonObject data`    | `void`    | 从传入的 `JsonObject` 数据中加载配置，并设置为当前配置实例。                                 |
| `static void loadFromJson(String path) throws Throwable`                               | `String path`        | `void`    | 从指定路径加载 JSON 配置文件并解析，然后设置为当前配置实例。                                     |
| `static void loadFromJson(File file) throws Throwable`                                 | `File file`          | `void`    | 从指定的 `File` 对象加载 JSON 配置文件并解析，然后设置为当前配置实例。                            |
| `static void loadFromJson(MultipartFile file) throws Throwable`                        | `MultipartFile file` | `void`    | 从上传的 `MultipartFile` 文件加载 JSON 配置并解析，然后设置为当前配置实例。                     |
| `static boolean isInit()`                                                              | 无                    | `boolean` | 检查当前配置是否已初始化。返回 `true` 如果已初始化，否则返回 `false`。                           |
| `static Configs getCurrentConfigs()`                                                   | 无                    | `Configs` | 获取当前配置实例。如果未初始化则返回 `null`。                                            |
| `static void setCurrentConfigs(Configs configs)`                                       | `Configs configs`    | `void`    | 设置当前配置实例。                                                             |
| `static boolean hasCurrentConfigs()`                                                   | 无                    | `boolean` | 检查是否存在当前配置实例。返回 `true` 如果存在，否则返回 `false`。                             |
| `static void saveCurrentConfigs(String path) throws NullPointerException, IOException` | `String path`        | `void`    | 将当前配置实例保存到指定的文件路径。检查必要的配置项是否存在，如果缺少必需的配置项，则抛出 `NullPointerException`。 |

##### 注意事项

- 确保在调用 `saveCurrentConfigs()` 方法之前使用 `hasCurrentConfigs()` 或 `isInit()` 来验证当前配置实例的存在。
- 如果需要更新或修改当前配置，可以使用 `setCurrentConfigs()` 方法来设置新的 `Configs` 实例。

### 3.4 `json` JSON 文件类说明

#### 1. `JsonPath extends ParentChild`

`JsonPath` 用于表示 JSON 文件中的路径。

它展示了路径之间的嵌套层级关系。

##### 构造函数

| 构造函数                                                                           | 参数                                                                         | 描述                                                     |
|--------------------------------------------------------------------------------|----------------------------------------------------------------------------|--------------------------------------------------------|
| `JsonPath(String name, ParentChild parent, JsonElement data, boolean isInRow)` | `String name`, `ParentChild parent`, `JsonElement data`, `boolean isInRow` | 使用指定名称、父节点、JSON数据及是否在行内标志初始化 `JsonPath` 实例，并从JSON数据加载。 |
| `JsonPath(String name, ParentChild parent, JsonElement data)`                  | `String name`, `ParentChild parent`, `JsonElement data`                    | 调用上述构造函数，设置 `isInRow` 为 `false`。                       |
| `JsonPath(String name, JsonElement data)`                                      | `String name`, `JsonElement data`                                          | 调用上述构造函数，`parent` 设置为 `null`。                          |
| `JsonPath(String name)`                                                        | `String name`                                                              | 调用上述构造函数，`parent` 和 `data` 均设置为 `null`。                |

##### 方法

| 方法名                                     | 参数                 | 返回值            | 描述                                                                                             |
|-----------------------------------------|--------------------|----------------|------------------------------------------------------------------------------------------------|
| `void loadFromJson(JsonElement data)`   | `JsonElement data` | `void`         | 从传入的 `JsonElement` 数据中加载并构建子节点。如果数据是 `JsonArray`，则将每个元素作为子节点添加；如果是 `JsonObject`，则将每个值对作为子节点添加。 |
| `Object getObjectName()`                | 无                  | `Object`       | 获取对象的真实名称（整数或字符串）。尝试将名称解析为整数，若失败则返回原始名称。                                                       |
| `List<Object> listAbsolutePathObject()` | 无                  | `List<Object>` | 返回从根到当前节点的绝对路径列表，包含每个节点的对象名称（整数或字符串）。                                                          |

#### 2. `JsonFile`

`JsonFile` 用于表示一个JSON文件。

它提供了加载、遍历和管理JSON数据的方法，并通过`JsonPath`来实现对JSON结构的路径导航。

##### 字段

| 字段名           | 含义     | 类型            | 默认值    |
|---------------|--------|---------------|--------|
| `data`        | JSON数据 | `JsonElement` | `null` |
| `name`        | 文件名称   | `String`      | -      |
| `root`        | 根路径    | `JsonPath`    | `null` |
| `currentPath` | 当前路径   | `JsonPath`    | `null` |
| `configs`     | 配置管理对象 | `Configs`     | 新实例    |

##### 构造函数

| 构造函数                                            | 参数                                | 描述                                            |
|-------------------------------------------------|-----------------------------------|-----------------------------------------------|
| `JsonFile(String name, JsonElement data)`       | `String name`, `JsonElement data` | 使用指定名称和JSON数据初始化 `JsonFile` 实例，设置根路径并初始化当前路径。 |
| `JsonFile(JsonElement data)`                    | `JsonElement data`                | 调用上述构造函数，使用默认名称 `"JsonFile"`。                 |
| `JsonFile(String path) throws Throwable`        | `String path`                     | 从指定路径加载JSON文件并初始化 `JsonFile` 实例。              |
| `JsonFile(File file) throws Throwable`          | `File file`                       | 从指定文件加载JSON数据并初始化 `JsonFile` 实例。              |
| `JsonFile(MultipartFile file) throws Throwable` | `MultipartFile file`              | 从上传的文件加载JSON数据并初始化 `JsonFile` 实例。             |

##### 方法

| 方法名                                         | 参数                     | 返回值        | 描述                                      |
|---------------------------------------------|------------------------|------------|-----------------------------------------|
| `void gotoChild(String name)`               | `String name`          | `void`     | 如果当前路径有名为 `name` 的子节点，则移动到该子节点。         |
| `void gotoChild(int index)`                 | `int index`            | `void`     | 如果当前路径有索引为 `index` 的子节点，则移动到该子节点。       |
| `void gotoParent()`                         | 无                      | `void`     | 如果当前路径有父节点，则移动到父节点。                     |
| `void setCurrentPathAsConfig(String name)`  | `String name`          | `void`     | 将当前路径设置为配置项，并以 `name` 命名。               |
| `void setCurrentPath(JsonPath currentPath)` | `JsonPath currentPath` | `void`     | 设置当前路径。确保必须是当前文件内部的 `JsonPath`，否则会导致错误。 |