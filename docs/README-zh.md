# KrkrDataLoader

一个尚未完善的项目，旨在方便地处理从 Krkr 引擎提取的剧情文件。

未来可能着重于进行可视化页面的制作与美化，和更多的简易自动匹配文本方法。

## 主要功能

- ### 将 ``.ks.scn`` 格式文件批量处理为 ``.ks.json`` 格式文件

- ### 通过将 ``.ks.json`` 格式文件（或文件夹）转换为对象结构，提供了导出剧情文本等功能


- ### 播放剧情音频，提供了便利的“听剧情”接口

- ### 立绘合成与导出

- ###### 未来还会接入 ``ffmpeg`` 的合成功能，以便导出由剧情合成的长音频。

## 安装方法

下载整个文件夹，按照使用说明引用 [src.tools](../src/tools) 中的库。

## API 文档

### 1. **剧本解包**

[DecompileTool.py](../src/tools/DecompileTool.py) 中的 ``Decompiler`` 类用于自动化解包

您可以像这样实例化和调用 ``Decompiler``：
~~~python
from DecompileTool import Decompiler
d = Decompiler()
~~~

``Decompiler`` 具有可选的 ``path`` 参数，用于指定 [FreeMoteToolkit](../src/FreeMoteToolkit) 的路径；如果省略，它将默认为项目路径（可正常运行）。

你可以使用 ``Decompiler.decompile(path)`` 对一个路径为 ``path`` 的 ``.ks.scn`` 格式文件进行解包：
~~~python
from DecompileTool import Decompiler
d = Decompiler()
d.decompile("D:\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.scn")
~~~

同理，你可以使用 ``Decompiler.decompile_all(path)`` 对一个路径为 ``path`` 的文件夹内所有的 ``.ks.scn`` 格式文件进行解包：
~~~python
from DecompileTool import Decompiler
d = Decompiler()
d.decompile_all("D:\\senrenbanka\\outPath\\data.xp3\\scn")
~~~

为避免不必要的麻烦，在指定的文件夹中解包的文件 **并不包含** 其子文件夹的文件。

详细示例，请参见 [DecompileExample.py](../examples/DecompileExample.py)。

### 2. **剧情读取**

[DataLoader.py](../src/tools/DataLoader.py) 中的 ``Select``、``Scene`` 和 ``Scenes``类用于数据的格式化加载。

``ScnFolder`` 类用于批量处理整个文件夹，实现了大规模的文件数据格式化管理。

``Config`` 类用于配置全局变量。

注：以下的结构在源码中是从里到外的。

#### ``Select``

代表选择中的特定选项。

具有 ``Select.text`` ``Select.location`` 和 ``Select.target`` 属性，分别指示选项的文本、所在文件和它引导的后续剧情。

#### ``SceneText``

代表剧情里的一条文本。

调用属性 ``SceneText.owner`` 来获取其所属剧情片段。

调用属性 ``SceneText.data`` 来获取其全部内容，其返回值为一个 ``list``。

提供了属性 ``SceneText.speaker`` 和``SceneText.content``，分别表示文本说话人和内容。

提供了属性 ``SceneText.sound`` 来获取所有对应播放的 ``SoundData`` 音频文件。（详见 **音频管理** 一章）


#### ``Scene``

剧情片段被分离，以实现选择和场景过渡之间的切换。

通过 ``Scene.isselect`` 属性区分选择支的存在，选择支的具体内容可以由 ``Scene.selects`` 进行调用。

提供了一个属性 ``Scene.fixname`` 作为每个场景的唯一标识符。

提供了 ``Scene.target`` 属性来获取下一个或多个场景。返回一个由 ``Scene`` 类对象组成的 ``list``。

提供了一个属性 ``Scene.title`` 来标识每个文件对应的剧情标题（目前已不维护）。

提供了属性 ``Scene.texts`` 来获取剧情片段中的所有文本。返回一个由特殊格式的 ``dict`` 组成的 ``list``。

使用 ``Scene.exposeText(output_file,watch_output)`` 导出某一个场景的剧情文本。``output_file`` 为导出文件 **（非路径，以 ``_io.TextIOWrapper``，即 ``open()`` 返回格式为指定格式）**，如果省略，则会在 src/outputs/文件名 下生成一个与该场景同名、以 ``.txt`` 结尾的文本文件；``watch_output`` 为测试选项，默认为 ``False``，若参数为 ``True`` 则会在命令行同步输出导出文件的内容以供测试。若该场景无文本，则会在命令行输出提示，同时也会保留一个只含文件标识符和 ``target`` 的文件。

>如果希望自定义文本的过滤器，请修改属性 ``Config.filter``，其默认值为一个包含若干正则表达式的列表，用于去除被过滤的部分。

#### ``Scenes``

表示整个 ``.ks.json`` 格式文件。为了高效管理各个片段，我将每个片段嵌套在类结构中。

您可以使用 ``Scenes[]`` 访问单个 ``Scene`` 实例，且 ``Scenes`` 支持迭代。

使用 ``Scenes.getIndexByName`` 获取对应 ``Scene.name`` 的索引，以及 ``Scene.getNameByIndex`` 根据给定索引检索  ``Scene.name``。

使用 ``Scenes.exposeText(output_path,watch_output)`` 导出整个文件文本。``output_path`` 为导出文件路径，如果省略，则会在 src/outputs/文件名 下生成一个与该文件同名、以 ``.ks.txt`` 结尾的文本文件；``watch_output`` 为测试选项，默认为 ``False``，若参数为 ``True`` 则会在命令行同步输出导出文件的内容以供测试。

>如果希望自定义文本的过滤器，请修改属性 ``Config.filter``，其默认值为一个包含若干正则表达式的列表，用于去除被过滤的部分。

详细示例，请参见 [ScnLoaderexample.py](../examples/ScnLoaderExample.py)。

#### ``Scnfolder``

表示整个通过 ``krkrextract`` 解包出来的 ``data.xp3/scn`` 文件夹。

在初始化阶段把所有 ``.ks.json`` 格式文件作为 ``Scenes`` 格式读取。（不包括子文件夹中的文件）

初始化 ``Scnfolder(path,name)`` 时，``path`` 用于指定文件夹路径，``name`` 用于指定该对象的名字（目前无用）。

与 ``Scenes`` 类似，提供了函数 ``Scnfolder.getIndexByName`` 与 ``Scnfolder.getNameByIndex``，以及 ``Scnfloder[]`` 的调用方法。

### ``Config``

全局变量的设置。

``Config.encoding`` 指定了文件读取的编码形式。

``Config.audio_suffix`` 指定了音频文件的后缀名。

``Config.defualt_name`` 指定了默认的对象名。

``Config.defualt_location`` 指定了默认的对象位置名。

``Config.defualt_speaker`` 指定了文本的默认说话人。

``Config.defualt_content`` 指定了文本的默认内容。

``Config.filter`` 指定了输出文本时的过滤器，为一个由正则表达式组成的``list``，用于去除需要过滤掉的部分。

``Config.hide_tqdm`` 指定了是否显示读取文件的进度条。

``Config.debug`` 指定了是否开启调试模式。

``Config.version`` 指定了加载文档的解释器，目前稳定的有 ``senrenbanka`` 和 ``sanoba witch`` 以及 ``cafe stella`` 三个版本，默认为 ``senrenbanka``。



> ``Config.version`` 的设置主要是因为不同krkr2引擎的版本不同，从而导致了选择支、跳转等定义的命名方式不同。在接下来的版本更新中，将不断新增其他游戏的适配，并实现游戏数据通配。

### 3. **音频管理**

[DataLoader.py](../src/tools/DataLoader.py) 中的 ``SoundData`` 和 ``SoundManager``类用于批量音频处理。

#### ``SoundData``

表示一条音频数据。

调用 ``SoundData.name`` 以获取其说话人名，调用 ``SoundData.voice`` 以获得其文件名。

调用 ``SoundData.owner`` 以获取其所属 ``SceneText`` 文本。

初始化时 ``SoundData(owner={'speaker':'Unknown','content':"Unknown"},data=dict())``，``owner`` 默认值为 ``{'speaker':'Unknown','content':"Unknown"}``，以防止播放时的报错，**但从 ``SceneText`` 中获取的 ``SoundData.owner`` 必然为 ``SceneText`` 格式**；``data`` 指定了该音频的文件名，默认为 ``{'voice':'defualt'}``。

#### ``SoundManager``

表示音乐播放器，播放由 ``pygame.mixer`` 实现。

调用 ``SoundManager.playsound(sound,wait_done,tick,print_content)`` 以播放一条 ``SoundData`` 格式的 ``sound`` 音频，其中 ``wait_done`` 表示是否为阻塞式播放， ``tick`` 为检测播放是否结束的间隔时间，``print_content`` 为是否把对应的 ``SceneText`` 台词输出。

调用 ``SoundManager.playsounds(sound_list,wait_done,tick,interval,print_content,using_tts)`` 以遍历 sound_list 列表并播放多条音频。音频可为 ``str``（文件目录），``SoundData``(单条音频)，``SceneText``（包含文本），其中 ``interval`` 指定了两条语音播放之间的间隔，而当 ``print_content=True`` 时，无声的 ``SceneText`` 也会显示出其台词内容，并同样共享 ``interval``；若 ``using_tts=True``，则会使用内置的 ``pyttsx3`` 语音引擎对无声台词进行语音合成并播放。

调用 ``SoundManager.playScene(self,scene,wait_done,tick,interval,print_content,using_tts)`` 以遍历 ``Scene`` 格式的 ``scene`` 剧情片段，其他参数如上所述。

调用 ``SoundManager.playScenes(self,scenes,wait_done,tick,interval,print_content,using_tts):`` 以播放一个 ``Scenes`` 格式的 ``scenes`` 剧情文件，其他参数如上所述。

提供了一个属性 ``SoundManager.engine`` 来获取内置的 ``pyttsx3`` 语音引擎。

详细示例，请参见 [SoundManagerExample.py](../examples/SoundManagerExample.py)。

### **立绘合成与导出**

[FgimageLoader.py](../src/tools/FgimageLoader.py) 中的 ``Fgimage`` 和 ``FgimageFolder``类用于批量音频处理。

#### ``Layer``

表示一张图层文件。

目前尚不支持直接自定义，一般由 ``Fgimage`` 自动生成；

因此对其参数也不进行详细介绍，详情请参照源码，属性均按照原有图层规则定义。

#### ``Group``

表示一个图层组。

用于标记图层所在图层组，一般由 ``Fgimage`` 自动生成；

目前尚无意义，未来在制作可视化界面时会用作图层组的分类。

#### ``Fgimage``

表示一份立绘规则文件（及其所调用的所有图层）。

可以通过 ``Fgimage[]`` 调用其中的图层。

提供了函数 ``Fgimage.get_image(layers,show,background)`` 获取一个 ``PIL.Image`` 对象。其中 ``layers`` 为一个由 ``int`` 组成的 ``list``，每个值为立绘名最后一个 ``_`` 与文件后缀（如 ``.png``）之间的下标；``show`` 为是否在返回的时候显示图片；``background`` 为一个四维元组 ``(R,G,B,A)``，用于注明背景颜色。

#### ``FgimageFolder``

表示一个图层文件夹（不包含子文件夹）。

可以通过 ``FgimageFolder[]`` 调用其中的 ``Fgimage``。

## 开源动机(?)

在解包使用 Krkr2 引擎的游戏剧情和用音频训练模型时遇到一些困难，浪费了大量时间在这些神秘的脚本上。

本着“前人栽树，后人乘凉”的精神，我决定实施这一解决方案。

万一将来有人发现它，欢迎贡献。

Ciallo～(∠・ω< )⌒☆

## 未来可能实现

### 长音频剧本合成（正在开发中）

将整个文件的剧本合并为一个长音频文件（未来改进或许包含背景音乐），用于像听广播剧一样收听，避免许多尴尬时刻。 

虽然长音频的合成尚未完成，但是播放已经完全可以实现了。


### 新引擎加密的解密

引擎采用的新加密方法阻止了 ``Krkrextract`` 的正常解包，需要 ``KrkrzExtract`` ``KrkrDump`` ``KrkrPatch`` 等工具进行同步读取和解包。

这使文件分类和音频解包变得复杂。 我正在学习逆向工程，一旦熟练，我打算升级解包工具。

敬请期待……但不会很快。

## 后记

### 4.1.4

目前基础的功能已经完全实现，并且可视化的操作界面也已经开发完成。

然而出于效率等角度的考虑，对比起同类工具 ``Krkrextract`` 等，``KrkrDataLoader`` 的内存仍然过大，并带着许多不便。这是由 Python 语言自身的臃肿性导致的，因此未来可能的重构之中，可能会换语言版本，但是为了方便可能需要的简便 API 的调用，我还是会保留 Python 版本的接口。也许未来不会继续维护 Python 接口了也不一定。

2024.8.17，我上 github 看了一眼，发现居然有人给这个项目 star 了。无论出于什么原因，这个 star 还是挺鼓舞人心的，至少有人发现了这个项目，而也许这个项目会帮助到有需要的人。

未来我也会继续优化的，等啥时候学了前端就拿这个项目来练练手。

顺带一提，3.1.0 的后记中我提到了解包规则的自定义，目前内置的版本已经支持了《星光咖啡馆与死神之蝶》，而未来我想做的是一劳永逸地匹配所有文件，因此我会再次重新制作一个新的规则加载器以自动化规范其格式。

### 3.1.0

自从我发现 ``Krkrextract`` 具有直接导出文本的功能之后，其实我是深受打击的，因此项目的开发进度也就慢了下来。

但是我发现偶尔在B站上还是能看到一些对于这类工具的需要，这促使我继续改进这个项目，以实现一个便于使用的版本。

未来我可能会考虑PyQt的使用，以开发窗口化程序来简化解包和文件读取过程。

而且立绘的信息在我用GRAbro获取了合并规则之后，也变得可读了，因此也许最终会把立绘合成导出也给合并进来。

我还没考虑过CG的合成，这个或许不会很快，因为我还要花实现搞点小玩具（桌宠）玩玩。

《魔女的夜宴》和《千恋万花》的数据尚已通过测试，可以正常使用，其他的不保证，因此将来我会提供一个自定义接口，可以自定义解包规则，以简化这一流程。

综上所述，看到还有这么多功能要写，《天使纷扰》的解包暂停，未来重启时会写在这里的。

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