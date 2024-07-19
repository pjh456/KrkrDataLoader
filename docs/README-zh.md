# KrkrDataLoader

一个尚未完成的项目，旨在方便地处理从 Krkr 引擎提取的剧情文件。

未来的扩展可能包括已经实现但目前界面不太友好的附加功能。

## 主要功能

### 将 ``.ks.scn`` 格式文件批量处理为 ``.ks.json`` 格式文件

使用解包工具 [FreeMoteToolkit](../src/FreeMoteToolkit)，辅以 ``subprocess`` 功能，实现文件处理自动化，无需逐一手动拖放解包。

###### 实际上已存在一个可运行的版本，但最初并未计划开源，导致代码难以维护。本质上，它简化了重复性工作。我打算重构代码以提高可维护性，并在准备就绪后发布。

### 通过将 ``.ks.json`` 格式文件转换为对象结构，实现内容的有效处理。


其他功能正在积极开发中。

###### 我正在创建它们，但在考虑进一步简化，这可能推迟更精炼版本的发布。

## 安装方法

下载整个文件夹，按照使用说明引用 [src.tools](../src/tools) 中的库。这些都是简单的实用程序，避免了手动编写解包脚本的繁琐工作。

## 使用方法

### **配置文件**

在早期版本中，用于定位 [FreeMoteToolkit](../src/FreeMoteToolkit) 的路径。

但现在支持基于项目结构的自动配置，使此功能暂时废弃。

###### 未来实现音频处理时，可能会再次涉及 ``ffmpeg``。

### **剧本解包**

[DecompileTool.py](../src/tools/DecompileTool.py) 中的 ``Decompiler`` 类用于自动化解包

您可以像这样实例化和调用 ``Decompiler``：
~~~python
from tools.DecompileTool import Decompiler
d = Decompiler()
~~~

``Decompiler`` 具有可选的 ``path`` 参数，用于指定 [FreeMoteToolkit](../src/FreeMoteToolkit) 的路径；如果省略，它将默认为项目路径（这可以正常工作）。

你可以使用 ``Decompiler.decompile(path)`` 对一个路径为 ``path`` 的 ``.ks.scn`` 格式文件进行解包：
~~~python
from tools.DecompileTool import Decompiler
d = Decompiler()
d.decompile("D:\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.scn")
~~~

同理，你可以使用 ``Decompiler.decompile_all(path)`` 对一个路径为 ``path`` 的文件夹内所有的 ``.ks.scn`` 格式文件进行解包：
~~~python
from tools.DecompileTool import Decompiler
d = Decompiler()
d.decompile_all("D:\\senrenbanka\\outPath\\data.xp3\\scn")
~~~

详细示例，请参见 [DecompileExample.py](../examples/DecompileExample.py)。

### **剧情读取**

[DataLoader.py](../src/tools/DataLoader.py) 中的 ``Select``、``Scene`` 和 ``Scenes``类用于自动化数据检索。

``ScnFolder`` 类用于批量处理整个文件夹，实现了大规模的文件管理。

注：以下的结构在源码中是从里到外的。

#### ``Select``

代表选择中的特定选项。

具有 ``Select.text`` ``Select.target`` 和 ``Select.target`` 属性，分别指示选项的文本、所在文件和它引导的后续剧情。

### ``ScnBase``

``Setting`` 和 ``Setting`` 的基类，包含一些共用属性。

在 2.0.0 版本后，选择支的具体内容可以由 ``ScnBase.selects`` 进行调用，通过 ``ScnBase.isselect`` 属性区分选择支的存在，除含选择支的 ``Setting`` 对象为 ``True``，其他情况均为 ``False``。

提供了一个属性 ``ScnBase.fixname`` 作为每个场景的唯一标识符。

提供了 ``ScnBase.target`` 属性来获取下一个或多个场景。返回一个由 ``Setting`` 或 ``Scene`` 类对象组成的 ``list``。

#### ``Setting``

代表剧情场景片段的背景设置，包括但不限于立绘、音频、图像、选择支等内容。

调用属性 ``Setting.owner`` 来获取其所属剧情片段。

**通过 ``Scenes.settings`` 获取的 ``Setting`` 类该属性必然为 ``None``。**

#### ``SceneText``

代表剧情里的一条文本。

调用属性 ``SceneText.owner`` 来获取其所属剧情片段。

调用属性 ``Setting.data`` 来获取其全部内容，其返回值为一个 ``list``。

提供了属性 ``SceneText.speaker`` 和``SceneText.content``，分别表示文本说话人和内容。

提供了属性 ``SceneText.sound`` 来获取所有对应播放的 ``SoundData`` 音频文件。（详见 **音频管理** 一章）


#### ``Scene``

剧情片段被分离，以实现选择和场景过渡之间的切换。

若想获取具体参数内容，请调用 ``Scene.setting`` 属性以获取一个属于其的 ``Setting`` 类。

提供了一个属性 ``Scene.title`` 来标识每个文件对应的剧情标题（目前跨文件对象尚不可用）。

提供了属性 ``Scene.texts`` 来获取剧情片段中的所有文本。返回一个由特殊格式的 ``dict`` 组成的 ``list``。

使用 ``Scene.exposeTextWithFilter(filter,output_file,watch_output)`` 导出某一个场景的剧情文本。其中 ``filter`` 为一个由正则表达式组成的 ``list``，用于过滤一些特殊符号；``output_file`` 为导出文件 **（非路径，以 ``_io.TextIOWrapper``，即 ``open()`` 返回格式为指定格式）**，如果省略，则会在 src/outputs/文件名 下生成一个与该场景同名、以 ``.txt`` 结尾的文本文件；``watch_output`` 为测试选项，默认为 ``False``，若参数为 ``True`` 则会在命令行同步输出导出文件的内容以供测试。若该场景无文本，则会在命令行输出提示，同时也会保留一个只含文件标识符和 ``target`` 的文件。

#### ``Scenes``

表示整个 ``.ks.json`` 格式文件。为了高效管理各个片段，我将每个片段嵌套在类结构中。

您可以使用 ``Scenes[]`` 访问单个 ``Scene`` 实例，且 ``Scenes`` 支持迭代。

提供了 ``Scenes.setting`` 和 ``Scenes.setting_index`` 分别指定了文件内所有 ``Setting`` 对象及其索引。通过 ``Scenes.settings`` 获取的 ``Setting`` 类该属性必然为 ``None``。

使用 ``Scenes.getIndexByName`` 获取对应 ``Scene.name`` 的索引，以及 ``Scene.getNameByIndex`` 根据给定索引检索  ``Scene.name``。

使用 ``Scenes.exposeTextWithFilter(filter,output_path,watch_output)`` 导出整个文件文本。其中 ``filter`` 为一个由正则表达式组成的 ``list``，用于过滤一些特殊符号；``output_path`` 为导出文件路径，如果省略，则会在 src/outputs/文件名 下生成一个与该文件同名、以 ``.ks.txt`` 结尾的文本文件；``watch_output`` 为测试选项，默认为 ``False``，若参数为 ``True`` 则会在命令行同步输出导出文件的内容以供测试。

详细示例，请参见 [ScnLoaderexample.py](../examples/ScnLoaderExample.py)。

#### ``Scnfolder``

表示整个通过 ``krkrextract`` 解包出来的 ``data.xp3/scn`` 文件夹。

在初始化阶段把所有 ``.ks.json`` 格式文件作为 ``Scenes`` 格式读取。（不包括子文件夹中的文件）

初始化 ``Scnfolder(path,name,debug)`` 时，``path`` 用于指定文件夹路径，``name`` 用于指定该对象的名字（目前无用），``debug=True`` 时会在控制台输出读取文件进度。

与 ``Scenes`` 类似，提供了函数 ``Scnfolder.getIndexByName`` 与 ``Scnfolder.getNameByIndex``，以及 ``Scnfloder[]`` 的调用方法。

### **音频管理**

[DataLoader.py](../src/tools/DataLoader.py) 中的 ``SoundData`` 和 ``SoundManager``类用于批量音频处理。

#### ``SoundData``

表示一条音频数据。

初始化时 ``SoundData(owner={'speaker':'Unknown','content':"Unknown"},data=dict(),suffix='.ogg')``，``owner`` 默认值为 ``{'speaker':'Unknown','content':"Unknown"}``，以防止播放时的报错，**但从 ``SceneText`` 中获取的 ``SoundData.owner`` 必然为 ``SceneText`` 格式**；``data`` 指定了该音频在文件中的内容，默认为 ``{'voice':'defualt'}``；``suffix`` 为音频后缀，默认为 ``.ogg``。

调用 ``SoundData.name`` 以获取其说话人名，调用 ``SoundData.voice`` 以获得其文件名。

调用 ``SoundData.owner`` 以获取其所属 ``SceneText`` 文本。

#### ``SoundManager``

表示音乐播放器，播放由 ``pygame.mixer`` 实现。

调用 ``SoundManager.playsound(sound,wait_done,tick,print_content)`` 以播放一条 ``SoundData`` 格式的 ``sound`` 音频，其中 ``wait_done`` 表示是否为阻塞式播放， ``tick`` 为检测播放是否结束的间隔时间，``print_content`` 为是否把对应的 ``SceneText`` 台词输出。

调用 ``SoundManager.playsounds(sound_list,wait_done,tick,interval,print_content,using_tts)`` 以遍历 sound_list 列表并播放多条音频。音频可为 ``str``（文件目录），``SoundData``(单条音频)，``SceneText``（包含文本），其中 ``interval`` 指定了两条语音播放之间的间隔，而当 ``print_content=True`` 时，无声的 ``SceneText`` 也会显示出其台词内容，并同样共享 ``interval``；若 ``using_tts=True``，则会使用内置的 ``pyttsx3`` 语音引擎对无声台词进行语音合成并播放。

调用 ``SoundManager.playScene(self,scene,wait_done,tick,interval,print_content,using_tts)`` 以遍历 ``Scene`` 格式的 ``scene`` 剧情片段，其他参数如上所述。

调用 ``SoundManager.playScenes(self,scenes,wait_done,tick,interval,print_content,using_tts):`` 以播放一个 ``Scenes`` 格式的 ``scenes`` 剧情文件，其他参数如上所述。

提供了一个属性 ``SoundManager.engine`` 来获取内置的 ``pyttsx3`` 语音引擎。

详细示例，请参见 [SoundManagerExample.py](../examples/SoundManagerExample.py)。

## 开源动机(?)

在解包使用 Krkr2 引擎的游戏剧情和用音频训练模型时遇到一些困难，浪费了大量时间在这些神秘的脚本上。

本着“前人栽树，后人乘凉”的精神，我决定实施这一解决方案。

万一将来有人发现它，欢迎贡献。

Ciallo～(∠・ω< )⌒☆

## 未来可能实现

### 长音频剧本合成
将整个文件的剧本合并为一个长音频文件（未来改进或许包含背景音乐），用于像听广播剧一样收听，避免许多尴尬时刻。 

这正在开发中，但我仍在权衡是否为无声角色和旁白使用文本转语音 (TTS)，这可能比较耗费资源。

### 新引擎加密的解密

引擎采用的新加密方法阻止了 ``Krkrextract`` 的正常解包，需要 ``KrkrzExtract`` ``KrkrDump`` ``KrkrPatch`` 等工具进行同步读取和解包。

这使文件分类和音频解包变得复杂。 我正在学习逆向工程，一旦熟练，我打算升级解包工具。

敬请期待……但不会很快。

## 后记

### 2.1.0

在批量处理文本的时候，我发现解包出来的数据上有格式上的问题。不知道是柚子社自己的问题还是解包工具的问题。

因此接下来对于报错的修复可能需要大量游戏数据作为基础，但目前还没整合，因此说这个版本仍然是不稳定版本。

本项目基于《千恋万花》数据实现，对于其他 krkr 引擎游戏不保证可以正常使用，如果有报错问题请提 issue，如果可以的话把发生错误的文件发过来最好。

对于文档的处理到此差不多算是告一段落了，具体文本类的实现暂时不太有必要，下一步是音频的处理，可能需要 ffmpeg。

### 2.0.0

接下来的实现目标很明显了：跨文件批量处理、具体文本类和音频处理。大部分的雏形已经有了，但是编写接口文档和通过测试需要时间......

从这个版本开始，我可能会暂停英文文档的更新，直到我完成了所有预期代码为止。

### 1.1.0 

在版本 ``1.0.1`` 中，我使用 ``qwen2.5`` 更新了原来比较口语化的 ``README`` 中英文文档。

如果你发现新版本文档中具有解释上的错误，可以查找 ``1.0.0`` 版本文档。

###### 一切责任不在我，锅交给通义千问背。