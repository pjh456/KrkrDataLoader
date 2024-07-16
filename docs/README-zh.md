# KrkrDataLoader
一个尚未完成的项目，用于简便地处理Krkr引擎解包出来的剧情文件

未来也许会有一些功能上的扩展，都是我现在已经实现，但是接口太丑陋的东西。

## 主要功能：

### 将 ``.ks.scn`` 格式文件批量处理为 ``.ks.json`` 格式文件

用到了解包工具 [FreeMoteToolkit](../src/FreeMoteToolkit)，加了点 ``subprocess`` 的部分用于自动化处理文件，而不必一个个手动拖拽解包。

###### 实际上已经有一个写好的版本了，但是当时没想过开源，所以就写得很难维护。实际上都是些简单的东西，只不过把重复劳动的过程简化了而已。等我啥时候把它改得易维护之后就端上来。

### 将 ``.ks.json`` 格式文件的内容转化为对象，以实现对其内容的高效处理


其他功能火热实现中。

###### 已经在写了，只不过功能上还有什么可以简化的部分我还在想，所以可能可用版本的推出会稍微晚点。

## 安装方法

将整个文件夹下载下来，引用 [src.tools](../src/tools) 里的库按照使用方法进行调用即可

都是些没啥技术含量的东西，只不过省去了读文档结构和手动写解包脚本的步骤罢了。

## 使用方法

### 配置文件

在旧版本中用于寻找 [FreeMoteToolkit](../src/FreeMoteToolkit) 的路径。

不过现在已经可以基于文件架构进行自动配置了，所以暂时废弃。

###### 未来实现音频处理的时候可能会附带ffmpeg，因此可能会用到。

### 剧本解包

在 [DecompileTool.py](../src/tools/DecompileTool.py) 中提供了一个类 ``Decompiler`` 用于自动化解包。

你可以使用以下方法对 ``Decompiler`` 进行调用：
~~~py
from tools.DecompileTool import Decompiler
d = Decompiler()
~~~
其中 ``Decompiler`` 的初始化包括一个参数 ``path``，用于获取 [FreeMoteToolkit](../src/FreeMoteToolkit) 的路径，如果不填则为默认项目路径（可正常使用）。

你可以使用 ``Decompiler.decompile(path)`` 对一个路径为 ``path`` 的 ``.ks.scn`` 格式文件进行解包：
~~~py
from tools.DecompileTool import Decompiler
d = Decompiler()
d.decompile("D:\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.scn")
~~~

同理，你可以使用 ``Decompiler.decompile_all(path)`` 对一个路径为 ``path`` 的文件夹内所有的 ``.ks.scn`` 格式文件进行解包：
~~~py
from tools.DecompileTool import Decompiler
d = Decompiler()
d.decompile_all("D:\\senrenbanka\\outPath\\data.xp3\\scn")
~~~

详细的使用例放在 [DecompileExample.py](../examples/DecompileExample.py) 中了。

### 剧情读取

在 [ScnLoader.py](../src/tools/ScnLoader.py) 中提供了两个类 ``Scene`` 和 ``Scenes``用于自动化数据获取。

#### ``Scene``

每一幕的剧情在源文件中被分隔开来，以便于实现选择支的切换和场景的跳转。

包含了剧情和选择支两种类型的部分，以 ``Scene.selection`` 来区分。``Scene.selection=True`` 则为选择支，``Scene.selection=False`` 则为剧情。

提供了一个接口 ``Scene.goto()`` 来获取下一个场景，若该场景为选择支，则其不包含

#### ``Scenes``

一整个 ``.ks.json`` 格式文件。为了方便各幕之间的管理，我将每一幕单独丢进类里套娃实现了。

详细的使用例放在 [ScnLoaderexample.py](../examples/ScnLoaderExample.py) 中了。

## 开源动机(?)

解包 Krkr2 引擎游戏的剧情、拿音频去练模型的时候遇到了些麻烦，浪费了许多时间在写这些神秘脚本上。

抱着前人种树后人乘凉的想法先把这个实现了，万一哪天被人刨坟也欢迎 pull。

Ciallo～(∠・ω< )⌒☆

## 未来可能实现

### 长音频剧本合成
把一整个文件的剧情合成为一个长音频（改进后也许含背景音乐？），用于当广播剧听之类的，省去了许多社死的可能性(?)。

这个是我现在正在实现的，不过对于哑巴角色和旁白的语音还在考虑要不要用 TTS 实现，开销稍微有点大。

### 对于新引擎加密的解密

新作用的加密方式不同，导致 Krkrextract 没办法正常解包，必须得靠 KrkrzExtract/KrkrDump/KrkrPatch 等工具读取的同时解包文件。

这样子不仅麻烦，文件还没办法像之前一样被分好类。而后两个工具更是无法解包音频文件。

正在学习逆向工程，万一哪天开窍了就上手把解包工具升级一下。

Comming not soon......
