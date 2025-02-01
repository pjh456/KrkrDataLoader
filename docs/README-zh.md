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

### 首次实现

使用 Python + PySimpleGUI 实现，完成了所有目标。

但由于未能实现配置的自动化和可视化，并不满足于 Python 的低效运行，因此进行重构。

### 第零次重构

基于 Python 版本设计思路，转向更为面向对象的设计方式。

使用 C++ 实现，完成了数据的存储。

然而 JSON 文件的解析难度较大，外部库的导入也难以支持 Unicode，因此进行重构。

该版本尚未发布，未来如果找到合适的 JSON 库，可能会考虑重构回 C++ 版本来优化性能并发布。

### 第一次重构

基于 Python 版本设计思路，采用 C++ 版本的面向对象思想。

引入了更为解耦合的数据存储方式，简化了实现的同时提高了可读性。

使用 Gson 库进行 JSON 文件解析，解决了 C++ 版本中的遗留问题。

使用 Java + JavaFX 实现，完成了剧情加载、配置文件和音频播放的功能。

但由于 JavaFX 存在较多漏洞，以及管理的不方便和 FXML 编辑的难度，因此再次重构。

### 第二次重构

复用了 JavaFX 版本的核心数据存储类，并大幅进行了重构。

将 Java 作为后端而非整体的运行方式，并使用 Spring Boot 作为服务器 API 设置。

为简化可视化页面的设计流程，提高可视化部分的可定制性，引入了 Electron 作为前端框架。

本次重构即为当前版本，旨在提供更规范和更高效的支持。

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

### 3.1 源码类继承架构

```
ParentChild
├── KrkrData
│   ├── KrkrVoice
│   ├── KrkrDialogue
│   ├── KrkrScene
│   └── KrkrScenes
└── JsonPath (in json package)
```

### 3.2 `core` 核心类说明

所有字段均拥有 getter，部分拥有 setter。

每个方法的所有参数版本均会被列出以简化理解。

#### 3.2.1 `ParentChild`

`ParentChild` 是所有需要存储父子双向关系类的基类。

##### 构造函数

###### `ParentChild(String name, ParentChild parent)`：构建一个带父节点的 `ParentChild` 实例。

###### `ParentChild(String name)`：构建一个不带父节点的 `ParentChild` 实例。

###### `ParentChild(ParentChild parent)`：构建一个带父节点的默认名称 `ParentChild` 实例。

###### `ParentChild()`：构建一个不带父节点的默认名称 `ParentChild` 实例。

##### 字段

- `parent`：父节点，类型为 `ParentChild`，默认为 `null`。
- `childrenMap`：子节点集合，类型为 `Map<String, ParentChild>`，默认为 `new LinkedHashMap<>()`。
- `name`：节点名称，类型为 `String`，默认为 `default`。

##### 方法

###### `void addChild(String name, ParentChild child)`：添加子节点到集合指定键值。

所有的添加子节点方法均为双向关系，即设置 `child` 为子节点的同时，也调用 `child.setParent(this)`

###### `void addChild(ParentChild child)`：添加子节点到集合。

此时子节点对应键值为 `child.getName()`。

###### `void addAllChildren(Map<String, ParentChild> childrenMap)`：添加 `Map<String, ParentChild>` 中所有子节点键值对到集合。

###### `void addAllChildren(List<ParentChild> children)`：添加 `List<ParentChild>` 中所有子节点到集合。

###### `void addAllChildren(ParentChild... children)`：添加所有子节点到集合。

###### `List<ParentChild> listChildren()`：获取所有子节点列表。

###### `List<String> listChildrenName()`：获取所有子节点名称列表。

###### `ParentChild getChild(String name)`：通过指定键值获取指定子节点。

###### `ParentChild getChild(int Index) throws IndexOutOfBoundsException`：通过指定索引获取指定子节点。

当索引越界时，抛出 `IndexOutOfBoundsException`。

###### `List<ParentChild> listAbsolutePath()`：获取绝对路径。

###### `List<String> listAbsolutePathName()`：获取绝对路径名称。

##### 异常

- `IndexOutOfBoundsException`：当获取子节点，索引越界时抛出。

#### 3.2.2 `KrkrData extends ParentChild`

`KrkrData` 是所有剧情文本数据的基类，所有剧情文本的嵌套架构都继承自 `KrkrData`。

##### 构造函数

###### `KrkrData(String name)`：使用指定的名称创建一个新的 `KrkrData` 实例。

##### 字段

- 继承自 `ParentChild` 类，因此继承了 `parent`，`childrenMap` 和 `name` 字段。
- `data`：剧情文本数据，类型为 `JsonElement`，默认为 `null`
- `is_init`：是否初始化，类型为 `boolean`，默认为 `false`

##### 方法

###### `void initialize() throws Throwable`：初始化（为延迟初始化而创建）。

##### 异常

- `Throwable`：当初始化时异常抛出。

#### 3.2.3 `KrkrVoice extends KrkrData`

`KrkrVoice` 类表示 `KrkrDialogue` 中的语音数据。

它包含了语音的文件路径和该语音的名称。

##### 构造函数：

###### `KrkrVoice(String name, String path)`：使用指定的名称和路径创建一个新的 `KrkrVoice` 实例。

##### 字段

- 继承自 `ParentChild` 类，因此继承了 `parent`，`childrenMap` 和 `name` 字段。
- 继承自 `KrkrData` 类，因此继承了 `data` 和 `is_init` 字段。
- `path`：类型为 `String`，表示语音文件的路径。

##### 方法

###### `void play()`：如果存在语音对象，播放该语音。

已废弃，不再支持。

###### `void stop()`： 如果存在语音对象，停止该语音。

已废弃，不再支持。

#### 3.2.4 `KrkrDialogue extends KrkrData`

`KrkrDialogue` 类表示 `KrkrScene` 中的单条对话数据。

它包含了对话的发言者、内容和可选的语音对象。

##### 构造函数：

###### `KrkrDialogue(String name)`：使用指定的名称创建一个新的 `KrkrDialogue` 实例。

此构造函数**不包含**对话数据。

###### `KrkrDialogue(String name, JsonElement data) throws Throwable`：使用指定的名称和 JSON 数据创建一个新的

`KrkrDialogue` 实例。

该构造函数从 `data` 中提取发言者、内容和语音信息，并根据配置加载对应数据。

如果数据无效或缺少必要的字段，将抛出异常。

##### 字段

- 继承自 `ParentChild` 类，因此继承了 `parent`，`childrenMap` 和 `name` 字段。
- 继承自 `KrkrData` 类，因此继承了 `data` 和 `is_init` 字段。
- `speaker`：类型为 `String`，表示对话的发言者。默认值为从 `GlobalSetting` 中获取的当前设置的发言者值。
- `content`：类型为 `String`，表示对话的内容。默认值为从 `GlobalSetting` 中获取的当前设置的内容值。
- `voice`：类型为 `KrkrVoice`，表示对话对应的语音对象，默认为 `null`。

##### 方法

###### `void play()`：如果存在语音对象，播放该语音。

已废弃，不再支持。

###### `void stop()`：如果存在语音对象，停止该语音。

已废弃，不再支持。

##### 异常

###### `Throwable`：在 构造函数中，如果 JSON 数据不包含所需字段或数据格式不正确，将抛出异常。

#### 3.2.5 `KrkrScene extends KrkrData`

`KrkrScene` 类表示 `KrkrScene` 中的一整个场景。

它包括其中的所有 `KrkrDialogue` 对话，负责管理该场景的对话内容。

##### 构造函数：

###### `KrkrScene(JsonElement data, boolean init_now) throws Throwable`：通过提供的 `JsonElement` 数据创建一个新的

`KrkrScene`

实例，并根据 `init_now` 标志决定是否立即初始化该场景。

###### `KrkrScene(JsonElement data) throws Throwable`：通过提供的 `JsonElement` 数据创建一个新的 `KrkrScene` 实例，并立即初始化。

##### 字段：

- 继承自 `ParentChild` 类，因此继承了 `parent`，`childrenMap` 和 `name` 字段。
- 继承自 `KrkrData` 类，因此继承了 `data` 和 `is_init` 字段。

##### 方法：

###### `void initialize() throws Throwable`：初始化场景数据。

该方法从 JSON 数据中提取 `dialogues` 字段，并为每个对话创建一个 `KrkrDialogue` 实例，最后将这些对话添加为子节点。

初始化完成后，设置 `is_init` 为 `true`，并将 `data` 设置为 `null`。

###### `List<String> listDialogues()`：获取当前场景中的所有对话内容。

返回一个 `List<String>`，每个元素是场景中的一条对话的格式化输出。

##### 异常：

###### `Throwable`：在 `initialize()` 方法中，如果 JSON 数据不包含所需字段或数据格式不正确，将抛出异常。

#### 3.2.6 `KrkrScenes extends KrkrData`

`KrkrScenes` 类表示一个完整的剧情场景集合。

它负责管理多个 `KrkrScene` 剧情场景实例。

##### 构造函数：

###### `KrkrScenes(JsonElement data, boolean init_now) throws Throwable`：通过提供的 JSON 数据创建一个新的

`KrkrScenes` 实例，是否初始化由 `init_now` 参数控制。

###### `KrkrScenes(String path, boolean init_now) throws Throwable`：通过提供的本地文件路径加载 JSON 数据并创建一个新的

`KrkrScenes` 实例，是否初始化由 `init_now` 参数控制。

###### `KrkrScenes(File file, boolean init_now) throws Throwable`：通过提供的文件对象加载 JSON 数据并创建一个新的

`KrkrScenes` 实例，是否初始化由 `init_now` 参数控制。

###### `KrkrScenes(MultipartFile file, boolean init_now) throws Throwable`：通过提供的

`MultipartFile` 对象加载 JSON 数据并创建一个新的 `KrkrScenes` 实例，是否初始化由 `init_now` 参数控制。

###### `KrkrScenes(String path) throws Throwable`：通过提供的本地文件路径加载 JSON 数据并创建一个新的

`KrkrScenes` 实例，并立即初始化。

###### `KrkrScenes(File file) throws Throwable`：通过提供的文件对象加载 JSON 数据并创建一个新的 `KrkrScenes` 实例，并立即初始化。

###### `KrkrScenes(MultipartFile file) throws Throwable`：通过提供的 `MultipartFile` 对象加载 JSON 数据并创建一个新的

`KrkrScenes` 实例，并立即初始化。

##### 字段：

- 继承自 `ParentChild` 类，因此继承了 `parent`，`childrenMap` 和 `name` 字段。
- 继承自 `KrkrData` 类，因此继承了 `data` 和 `is_init` 字段。

##### 方法：

###### `void initialize() throws Throwable`：初始化场景集合数据。

该方法从 JSON 数据中提取 `scenes` 字段，为每个场景创建一个新的 `KrkrScene` 实例，并将其添加为子节点。

然后，它为每个 `KrkrScene` 创建一个新线程进行初始化，所有线程执行完毕后，将 `data` 设置为 `null`，并将 `is_init` 设置为
`true`。

#### 3.2.7 核心工具类 `KrkrUtils`

`KrkrUtils` 是一个工具类，主要提供与 JSON 文件相关的加载、解析功能。

它支持从多种来源加载 JSON 文件，包括 `MultipartFile`、`File` 和路径，并提供对文件夹中 JSON 文件的批量加载功能。

该类还包含对文件类型和目录的验证方法，以确保正确处理文件和路径。

##### 方法说明

###### `JsonObject loadJsonFile(MultipartFile file)`：从 `MultipartFile` 类型的文件中加载并解析 JSON 数据。

当文件解析过程中发生错误时，抛出 `IOException`。

###### `JsonObject loadJsonFile(File file)`：从指定的 `File` 类型文件中加载并解析 JSON 数据。

当文件解析过程中发生错误时，抛出 `IOException`。

###### `JsonObject loadJsonFile(String path)`: 从指定的本地文件路径加载并解析 JSON 文件。

如果路径不是一个有效的文件，抛出 `FileNotFoundException`。
如果文件类型不是 `.json`，抛出 `InvalidTypeException`。
当文件解析过程中发生错误时，抛出 `IOException`。

######

`List<JsonObject> loadJsonFolder(String path)`: 加载指定本地文件夹路径下所有符合条件的 JSON 剧情文本文件（`.ks.json`
扩展名）。

如果指定路径不是一个文件夹，抛出 `FileNotFoundException`。
当遍历文件夹或读取文件时发生错误，抛出 `IOException`。

###### `boolean isFile(String path)`: 检查指定路径是否是一个文件。

###### `boolean isFolder(String path)`: 检查指定路径是否是一个文件夹。

###### `JsonObject loadJsonFile(BufferedReader reader)`: 从 `BufferedReader` 中读取 JSON 数据并解析。

当文件解析过程中发生错误时，抛出 `IOException`。

##### 内部实现

`loadJsonFile` 方法通过读取文件内容并使用 `Gson` 库将其解析为 `JsonObject` 类型。对于不同的文件输入类型，提供了不同的加载方式（
`MultipartFile`、`File`、路径）。

`loadJsonFolder` 方法遍历指定目录中的文件，筛选出符合条件（`.ks.json` 扩展名）的文件，并将其内容加载为 `JsonObject`
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

#### 3.3.1 `SingleConfig`

`SingleConfig` 类是中用于解析和匹配剧情文本数据的配置类。

它主要用于自定义配置 JSON 剧情文件的解析规则，以便从结构化的 JSON 数据中提取所需的内容。

##### 构造函数

###### `SingleConfig(String name, List<Object> fieldList) throws NullPointerException, IllegalArgumentException`：使用指定的配置名称和字段列表初始化 `SingleConfig` 实例。

字段列表定义了如何匹配 JSON 数据中的值。

每个字段要么是字符串（对应 `JsonObject` 的键），要么是整数（对应 `JsonArray` 的索引）。

`fieldList` 为 `null` 时，抛出 `NullPointerException`。
`fieldList` 包含非 `String` 或 `Integer` 类型时，抛出 `IllegalArgumentException`。

###### `SingleConfig(String name)`：使用指定的 `name` 初始化 `SingleConfig` 实例，`fieldList` 默认为 `null`。

##### 字段

- `name`：配置名称，用于标识该配置。
- `fieldList`：字段列表，用于解析 JSON 数据。（目前只支持单模式解析）

##### 方法

###### `void addFields(List<Object> fieldList)`：添加一组新的字段列表，用于解析 JSON 数据。

`fieldList` 为 `null` 时，抛出 `NullPointerException`。
`fieldList` 包含非 `String` 或 `Integer` 类型时，抛出 `IllegalArgumentException`。

###### `void List<List<Object>> getFieldsList()`：获取当前配置中所有的字段列表。

###### `void clearFields()`：清空当前所有的字段列表。

###### `static boolean checkFields(List<Object> fieldList)`：检查给定的字段列表是否仅包含 `String` 和 `Integer` 类型的元素。

###### `JsonObject matchValueAsJsonObject(JsonElement data)`：根据配置中的字段列表匹配并返回 `JsonObject` 类型的数据。

###### `JsonArray matchValueAsJsonArray(JsonElement data)`：根据配置中的字段列表匹配并返回 `JsonArray` 类型的数据。

###### `JsonPrimitive matchValueAsJsonPrimitive(JsonElement data)`：根据配置中的字段列表匹配并返回 `JsonPrimitive` 类型的数据。

###### `private JsonElement matchValueAsJsonELement(JsonElement data)`：根据配置中的所有字段列表匹配并返回匹配的数据

当前版本部分支持多模式匹配，如果存在匹配项则返回**第一项先匹配到**的，否则返回 `null`。

`JsonElement` 是 `JsonObject`、`JsonArray` 和 `JsonPrimitive` 的基类，因此该方法不公开。

###### `private JsonElement matchValueFromData(JsonElement data, List<Object> fieldList)`： 根据给定的单组字段列表和 JSON 数据，递归匹配并返回相应的 `JsonElement`。

如果字段类型与数据不匹配时，抛出 `NoSuchFieldException`。

##### 异常

###### ``NullPointerException``：当字段列表为 `null`时，抛出此异常。

###### ``IllegalArgumentException``：当字段列表包含非 `String` 或 `Integer` 类型的元素时，抛出此异常。

###### ```NoSuchFieldException```：当字段类型与数据不匹配时，抛出此异常。

#### 3.3.2 `Configs`

`Configs` 类负责处理配置剧情文件解析规则的加载、使用和保存操作。

它支持从多种来源（如 `JsonObject`、`File`、`MultipartFile` 和本地文件路径）加载配置，并提供相应的方法来验证和保存配置。

该类通过管理多个 `SingleConfig` 对象来处理各种配置项，确保必要的配置项齐全。

##### 构造函数

###### `Configs()`：创建一个空的 `Configs` 对象。

###### `Configs(JsonObject data)`：使用提供的 `JsonObject` 数据加载配置。

###### `Configs(String path) throws Throwable`：使用提供的文件路径加载配置。

###### `Configs(File file) throws Throwable`：使用提供的 `File` 对象加载配置。

###### `Configs(MultipartFile file) throws Throwable`：使用提供的 `MultipartFile` 文件加载配置。

##### 字段

- `(static) necessaryConfigs`：该列表包含所有必须的配置项，配置文件中必须包含这些项，否则将抛出错误，类型为 `List<String>`，不支持修改。

tips：每一个必需的配置项都是由配置文件决定好的，如果不了解项目运行逻辑，不建议进行修改。

##### 方法

###### `void loadFromJson(JsonObject data)`：从传入的 JsonObject 数据中加载配置。

该方法会遍历 JSON 数据中的每个配置项，并解析其内容。

每个配置项的值应该是一个 `JsonArray`，数组中的元素会被转换为 `SingleConfig` 的字段。

###### `void loadFromJson(String path) throws Throwable`：从指定路径加载 JSON 配置文件并解析。

###### `void loadFromJson(File file) throws Throwable`：从指定的 `File` 对象加载 JSON 配置文件并解析。

###### `void loadFromJson(MultipartFile file) throws Throwable`：从上传的 `MultipartFile` 文件加载 JSON 配置并解析。

###### `void save(String path) throws NullPointerException, IOException`：将当前的配置保存到指定的文件路径。

保存时，会检查必要的配置项是否存在，如果缺少必需的配置项，则抛出 `NullPointerException`。

文件内容会以格式化的 JSON 形式保存。

###### `static boolean checkConfigs(Configs configs)`：检查传入的 `Configs` 对象是否包含所有必要的配置项。

##### 注意事项
确保传入的配置文件格式正确，并符合预期的 JSON 结构。

如果缺少必要的配置项，`save()` 方法会抛出 `NullPointerException`，因此在调用该方法之前，建议使用 `checkConfigs()` 进行验证。

#### 3.3.3 `GlobalConfig`

`GlobalConfig` 类用于全局管理 `Configs` 对象的加载、保存及获取。

它是一个静态类，负责操作当前配置实例。通过该类，可以方便地访问、修改和保存全局配置。

支持从多种来源（如 JSON 数据、`File``、`MultipartFile`、文件路径）加载配置。

##### 字段
- `currentConfigs`：存储当前的 `Configs` 实例。该属性是静态的，表示全局唯一的配置对象。

##### 方法

###### `static void loadFromJson(JsonObject data)`：从传入的 JsonObject 数据中加载配置，并将其设置为当前配置。

###### `static void loadFromJson(String path) throws Throwable`：从指定的文件路径（相对或绝对）加载配置，并将其设置为当前配置。

###### `static void loadFromJson(File file) throws Throwable`：从指定的 `File` 对象加载配置，并将其设置为当前配置。

###### `static void loadFromJson(MultipartFile file) throws Throwable`：从上传的 `MultipartFile` 文件加载配置，并将其设置为当前配置。

此方法适用于 Web 应用程序中的文件上传场景。

###### `static boolean isInit()`：检查当前配置是否已经初始化。

###### `static boolean hasCurrentConfigs()`：检查当前是否有有效的配置对象。

###### `static void saveCurrentConfigs(String path) throws NullPointerException, IOException`：将当前的配置保存到指定的文件路径。

如果没有当前配置，抛出 `NullPointerException` 异常。

该方法会调用 `Configs` 类的 `save` 方法来完成保存操作。

##### 注意事项

在调用 `saveCurrentConfigs` 方法时，确保当前配置已成功加载，否则会抛出 `NullPointerException` 异常。

配置的加载是全局性的，因此对 `GlobalConfig` 的操作会影响整个应用程序的配置状态。

### 3.4 `json` JSON 文件类说明

所有字段均拥有 getter，部分拥有 setter。

每个方法的所有参数版本均会被列出以简化理解。

#### 3.4.1 `JsonPath`

