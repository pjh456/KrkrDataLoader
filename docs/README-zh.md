# KrkrDataLoader

以 Electron + Java Restful API 组合实现。

设计思路与部分代码继承自 Python 版本与 JavaFX 版本。

## 文件架构
### 一、数据存储类
根据层次的不同，数据被划分为了 ``KrkrFolder``,``KrkrScenes``,``KrkrScene``,``KrkrDialogue``,``Voice`` 等类，自顶而下地嵌套构筑，以简化访问过程。

具体而言，程序包含如下嵌套：

``KrkrFolder -> KrkrScenes -> KrkrScene -> KrkrDialogue -> Voice -> KrkrData``

所有类均继承自父类 ``KrkrData``，包含了属性 ``child_map`` 和 ``name``，分别标识子路径集合和对应标识符。

#### KrkrData
表示从 Json 文件中解析出来的一个路径对应的数据。

``KrkrData.getChild(String name)`` 返回值为 ``KrkrData`` 类型，用于获取一个路径的对应标识符子路径；

``KrkrData.setChild(KrkrData child)`` 无返回值，用于加入一个 ``KrkrData`` 对象作为其子路径；

``KrkrData.listChildrent()`` 返回值为 ``List<KrkrData>`` 类型，表示一个有序子路径列表；

``KrkrData.size()`` 返回值为 ``int`` 类型，表示子路径集合的大小。

#### Voice
表示一条声音文件，可以用于播放。

**该类不提供对外接口支持，只作为只读类存在。**

``Voice.play()`` 无返回值，表示播放一条音频；

``Voice.stop()`` 无返回值，表示停止一条音频。

#### KrkrDialogue
表示一条对话。

**该类不提供对外接口支持，只作为只读类存在。**

``KrkrDialogue.speaker`` 为 ``String`` 类型，表示对话的说话人，根据是否是独白可能为 ``null``；

``KrkrDialogue.content`` 为 ``String`` 类型，表示对话的内容，保证不为 ``null``；

``KrkrDialogue.voice`` 为 ``Voice`` 类型，表示对话的音频，根据是否存在可能为 ``null``；

#### KrkrScene
表示一个情景，包含多段对话。

**该类不提供对外接口支持，只作为只读类存在。**

#### KrkrScenes
表示一个情景组，即一个剧本 ``.scn`` 文件。

**该类提供了对外接口支持**

``KrkrScenes(file_path)`` 初始化一个新的剧本文件，需要处理来自 ``Config.getSingleConfig("scenes_name").getValueAsJsonPrimitive(data)`` 的 ``Throwable`` 异常。


### 二、配置器类
为了实现路径读取的自动化，配置路径文件使用 ``Json`` 文件进行存储。

配置器包含了 ``Config``,``SingleConfig`` 两个类用于读取路径文件，以及 ``Settings`` 用于存储默认参数。

#### SingleConfig
表示一个相对路径的字段。

**该类不提供对外接口支持，只作为只读类存在。**

``SingleConfig(JsonElement)`` 读取一个只包含 ``String`` 和 ``Integer`` 的 ``List``，分别表示相对路径中的字典和列表项。

``SingleConfig.add_fields(List<Object> fields)`` 原计划是想对外提供接口支持，以支持动态添加路径。但目前还在测试阶段，因此不建议使用。

如此实现之后，只需要调用 ``SingleConfig.getValueAsJsonPrimitive(data)``,``SingleConfig.getValueAsJsonArray(data)``,``SingleConfig.getValueAsJsonObject(data)`` 即可将上一层级传入的 ``JsonElement`` 数据进行解析，并获取其中相对路径的值。

#### Config
``SingleConfig`` 的集合，使用 ``ListMap`` 进行存储。

#### Settings
表示默认参数或者系统设置。


## API Reference

运行 Main.java 后访问 http://localhost:8080/swagger-ui.html 阅读 API 文档。

# FAQ

## 为什么后端使用 Java Restful API？
### 一、Java 提供了更高的定制性
在 Python 的实现中，我大量调用了各方的库来为我的代码提供支持。

然而，这些代码的质量良莠不齐，有些甚至需要我自己去修 Bug，这使得维护的成本逐渐提高，以至于到如今已经难以接受。

进一步而言，很多库的实现并没有考虑到扩展性和健壮性的问题。这导致了维护代码的风险进一步加大，并且对于性能的开销过多。

因此，不管是为了更小的性能消耗、更少的空间占用还是更快的处理速度，重写一份 Java 版本所能产生的收益看起来是十分诱人的。

### 二、Java 在性能和实现之间取得了平衡
在这个版本发布之前，我已经采用 C++ 重构了一遍。然而，C++ 对各种第三方库的调用不太友好。

同时，在解析 Json 文件时，我所采用的 JsonCpp 库对 Unicode 字符集的不支持，浪费了我大量时间在处理字符上，
尽管重写一切、让所有东西都被自己充分了解是可以做到的，但是这样会大大拖累开发效率，并造成了代码的臃肿。

C++ 标准的不统一、GUI的难以制作和对于字符集处理的不友好，使得我最终放弃了它，转而使用 Java。

同时，为了在 GUI 的美观性与足够高的效率上取得平衡，使用 Java Restful API 进行前后端交互是值得的。

### 三、Java 生态相对活跃，且需要考虑的问题相对较少
我采用 Java 重构很大一个原因是它具有良好的跨平台性，这使得之后我不再需要花费大量心思去适配各种操作系统。

同时，Java 自带的 GC 和全指针操作也使得其空间开销上不需要手动实现，降低了代码难度。

从性能上考虑，Gson 库的极高 JSON 解析效率是我想充分利用的。

### 四、我自己正在学习 Java
出于拿 Java 练手与练习全栈开发的打算，我最终选择了转向 Java 的开发。

当然，这并不意味着我会在代码质量上妥协。相反，在这个版本中，我将会消除在 Python 版本中出现的各种已知但未能解决的 Bug！

## 为什么前端使用 Electron？
### 一、GUI 渲染的可控性与易用性
Electron 通过 html+css+javascript 为页面可定制性提供了良好了支持。

曾经我是考虑利用 JavaFX 进行渲染的，然而 FXML 并不能完全满足我的要求，同时性能非常低下。

考虑到 WebView 可能同样会有较低的性能与无法接受的卡顿，我决定选择 JavaScript 原生的内容。

为了兑现我对实现美观 GUI 页面的承诺，我决定放弃一部分的内存与效率，以此简化开发流程。

### 二、JavaScript 生态的成熟
目前的 JavaScript 已经拥有成熟的生态与大量的第三方库，这大大减轻了我的代码负担。

不用从头开始学习 JavaScript 与页面绘制十分诱人。

### 三、我正在学习 JavaScript 与前端开发
学习一门新的语言，最好的方法就是去运用它。

前段时间我对于渲染是十分恐惧的，因为这意味着大部分参数需要手动调整。尽管有各种可视化的解决方案，反复调整设计的部分是十分折磨人的。

因此为了克服对渲染的恐惧，提升我的前端页面绘制能力，我决定开始学习 JavaScript。

在这个版本中，我将会实现 JavaFX 版本中未能实现的遗憾：GUI 的美观化！

## 新版本中，有哪些已知问题会被修复？
### 一、内存泄露和报错
我将会提供更良好的报错处理，包括运行日志和输出等形式。同时，老版本中的内存泄露问题也将会进行修复。

### 二、效率低下
采用 Gson 库进行实现，KrkrDataLoader 将会支持多线程的数据加载，同时提供流式加载以减少内存占用。

换而言之，这个版本中的时空占用都会进一步减少，我们不再需要对着解析时的页面发呆十几秒！

### 三、兼容性差与健壮性弱
目前，Python 版本中，KrkrDataLoader 只支持固定编码的游戏，包括《魔女的夜宴》、《千恋万花》、《星光咖啡馆与死神之蝶》和《Riddle Joker》。

在 Python 版本中，我画过一个自动化匹配字段的饼，如今它已成为现实！

经过算法分析后，你仍然可以选择通过手动指定字段的方式来解析文件，这个过程将会更为人性化和高可定制化，使得 KrkrDataLoader 几乎可以兼容所有的剧情文件！

### 四、页面粗糙
原来采用的 PySimpleGui 页面过于复古，接下来的版本中，Electron 将会被用来渲染页面，使得工具箱的页面更为精致！

同时，这也为将来尚未开始开发的 KrkrWebGal 项目做了个铺垫。（未来将会结合 KrkrDataLoader，进行从数据读取到加载、最后渲染在服务器上的过程！）

### 五、多媒体处理混乱
原有的 TTS 调用了 Microsoft 自带的 TTS API，然而这会导致整个进程卡死，以至于产生很多 BUG。

同时，单线程的音频读取和播放同样也不是我想看到的，为了进行进一步的定制，只能通过 Java 进行完全的重构了。

在该版本完工后，音频的播放和长音频合成、导出，以及剧情的播放演绎功能将会被彻底实现。

由于解析 OGG 格式音频具有一定难度，暂时考虑借助 ffmpeg 等外部工具实现。

### 六、HTTP 访问的支持
在使用 Java Restful API 之后，新的 KrkrDataLoader 将会支持与前端的 HTTP 交互。

这意味着以后不必再直接启动程序以执行，而是可以远程调用！

这也是为 KrkrWebGal 的实现做铺垫的一部分。

### 综上所述，
KrkrDataLoader 的重构将会是一个长期项目。该项目将会长期不稳定更新，原来的 Python 或 JavaFX 版本若有问题仍然可以在 issues 里提出，我仍然会进行回复。

