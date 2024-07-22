## [3.0.0] - 2024-7-22  ***latest**
* 这个版本是一个稳定版本，并且进行了大的更新
### Fixed
- 修复了读取《魔女的夜宴》会报错的问题，对《魔女的夜宴》进行了适应性调整。
- 修复了 [README-zh.md](README-zh.md) 的文档错误。
### Added
- 增加了《魔女的夜宴》对应的适配参数，使其能够正常被读取。
- 给 ``SoundData``,``SceneText``,``Scene``,``Scenes`` 与 ``Scnfolder`` 增加了初始化参数 ``suffix``，用于指定音频文件格式。
### Changed
- 修改了 [SoundManagerExample.py](../examples/SoundManagerExample.py) 的设置，提供了本地语音播放参数的设置样例。
### Deleted
- 删除了 ``Setting`` 类，以后所有的剧情片段只根据 ``isselect`` 参数及有无文本进行区分，不对设置参数做具体调整，以适应《魔女的夜宴》数据。


## [2.3.1.0721]* - 2024-7-21
* 0d00 0721
### Added
- 新增了 [AfterStory](../src/AfterStory) 文件夹，用于维护在本项目完工后的一些相关小创意。
- 新增了 [ayachinene.py](../src/AfterStory/ayachinene.py)，可调用 ``Onani`` 类用于调用 ``Onani.Onani_generate_number()`` 函数，将数字转化为数学表达式（目前最大是60）。


## [2.3.1]* - 2024-7-20
* 该版本是一个文档补充与代码修复版本。
### Fixed
- 修复了 ``SoundManager.playsound()`` 在 ``print_content=False`` 时仍会输出台词的问题。
### Added
- 为所有函数添加了文档注释。


## [2.3.0]* - 2024-7-19
* 该版本不是一个稳定的版本，稳定性的修复可能会无限期延期。
### Added
- 为播放无声角色台词提供了 ``pyttsx3`` 的本地 TTS 服务，详情参阅说明手册。
### Changed
- 更新了 [README-zh.md](README-zh.md)，增加了对 TTS 的说明。
- 更新了 [README.md](../README.md)，修改了新的版本前瞻。


## [2.2.2]* - 2024-7-19
* 该版本不是一个稳定的版本，稳定性的修复可能会无限期延期。
### Changed
- 为 ``Scenes`` 的读取增加了多个 ``tqdm`` 进度条，以提供更好的使用体验。


## [2.2.1]* - 2024-7-19
* 该版本是一个文档补充与代码修复版本。
### Fixed
- 修复了 2.1.1 版本日志的错误说明。
- 修复了 [README-zh.md](README-zh.md) 中某一处的表述错误。

### Changed
- 更新了 [README-zh.md](README-zh.md)，增加了对音频批量处理的说明。
- 更新了 [README.md](../README.md)，修改了新的版本前瞻。

### Deleted
- 移除了多处无意义的包导入。


## [2.2.0]* - 2024-7-19
* 该版本不是一个稳定的版本，稳定性的修复可能会无限期延期。
### Added
- 新增文本管理类 ``SceneText`` 及其配套音频类 ``SoundData``，用于精细化管理剧情文本。
- 新增播放器类 ``SoundManager``，用于批量播放音频。
- 对应新增了 [SoundManagerExample.py](../examples/SoundManagerExample.py) 的示例内容。

### Changed
- 由于扩充了音频播放的功能，修改 ``ScnLoader.py`` 为 [DataLoader](../src/tools/DataLoader.py)。


## [2.1.1]* - 2024-7-19
* 该版本是一个文档补充版本。
### Changed
- 更新了 [README-zh.md](README-zh.md)，增加了对批量管理文件的说明。
- 更新了 [README.md](../README.md)，修改了新的版本前瞻。


## [2.1.0]* - 2024-7-19
* 该版本不是一个稳定的版本，稳定性的修复可能会无限期延期。
### Fixed
- 修复了 ``get_target_list`` 可能报错的问题。（处理了解包数据损失的问题，对于代码健壮性正在逐步加强中）
- 修改并新增了函数内的报错位置，修复了潜在的数据处理错误。

### Added
- 新增 ``Scnfolder`` 类，用于批量处理整个文件夹里的所有 ``.ks.json`` 类型文件

### Changed
- 对应修改了 [ScnLoaderExample.py](../examples/ScnLoaderExample.py) 的示例内容。


## [2.0.0]* - 2024-7-18
* 该版本不是一个稳定的版本，稳定性的修复可能会无限期延期。
### Fixed
- 修复了 ``target`` 指向性的问题。
- 修复了 ``Select.target`` 在特定条件下返回 ``None`` 的问题。

### Added
- 新增 ``Setting`` 类，表示某个场景对应的设置和背景信息。
- 新增 ``ScnBase`` 基类，将 ``Scene`` 与 ``Setting`` 共有属性整合入 ``ScnBase``。
- 新增 ``get_target_list(data,isselect)`` 内部函数，用于处理数据的 ``target`` 属性。
- 新增 ``Scenes.settings`` 与 ``Scenes.setting_index`` 属性，用于在初始化阶段存储所有对应的设置信息与索引。（由数据格式可得，每个文件中都是 ``Scene`` 与 ``Setting`` 交替产生，因此以场景奇偶性进行区分，未来可能会发生不适用的问题）

### Changed
- ``Scenes.index`` 属性已被改为 ``Scenes.scene_index`` 属性，用于与 ``Scenes.setting_index`` 区分。
- 修改了 ``Scene(Scenes).exposeTextWithFilter(filter,output_file,watch_output)`` 的输出文件格式，现在即使不存在文本也会生成相应文件而非直接跳过，从而增强整个文件的连贯性；每个场景的开头结尾如今会标注上 ``Scene.location``，为多文件批量处理做铺垫。
- 修改了 ``Scene.target`` 的返回值内容，如今的返回值是 ``Scene.setting.target`` 的值，即一个由 ``Scene`` 对象组成的 ``list``。
- 对应修改了 [ScnLoaderExample.py](../examples/ScnLoaderExample.py) 的示例内容。


## [1.1.0] - 2024-7-17
### Fixed
- 完善了 [DataLoader.py](../src/tools/DataLoader.py) 报错的输出，减少了一些意外报错的可能性。

### Added
- 添加了新属性 ``Scene.fixname``，以支持修复后的路径格式。

### Changed
- 修改了 ``Scene.target`` 的返回格式，现在返回一个由 ``Scene`` 类对象组成的 ``list``。（若跨文件则只包含 ``Scene.name`` 和 ``Scene.__location`` 两个可调用属性）

### Deleted
- 属性 ``Scene.name`` 现已被废弃，如果需要使用场景原始名称请调用属性 ``Scene._name``。


## [1.0.2] - 2024-7-16
### Fixed
- 修复了 [ScnLoaderExample.py](../examples/ScnLoaderExample.py) 的演示 bug，使其能正常运行。

### Added
- 为 ``Scene`` 与 ``Scenes`` 增加了快速导出清洗后文本的方法。
- 添加了更新日志，并补全了前几个版本的更新日志。

### Changed
- 更新了 [README-zh.md](README-zh.md)，增加了对快速导出文本的说明。
- 更新了 [ScnLoaderExample.py](../examples/ScnLoaderExample.py)，添加了快速导出文本的示例。


## [1.0.1] - 2024-7-16
### Fixed
- 修复了 [README.md](../README.md) 无法正确跳转 [README-zh.md](README-zh.md)的问题。

### Changed
- 更新了 [README-zh.md](README-zh.md) 与 [README-en.md](README-en.md)，规范化了文档说明。
- 更新了 [DataLoader.py](../src/tools/DataLoader.py)，废弃了 ``Scene.goto()`` 方法，以 ``Scene.target`` 属性简化。

### Deleted
- 移除了 [0.0.1] 版本的测试文件 ``DataLoader.py``。


## [1.0.0] - 2024-7-16
### Added
- 新增 [examples](../examples) 演示样例组。
- 新增 [FreeMoteToolkit](../src/FreeMoteToolkit) 工具包。
- 新增 [tools](../src/tools) 主项目文件。

### Changed
- 调整了项目架构，将程序整合入了 [src](../src) 文件夹。
- 调整了文档架构，将中英双语 ``README`` 整合入 [docs](../docs) 文件夹，并对照修改了 [README.md](../README.md) 的导航。
- 调整了示范架构，将示例整合入了 [examples](../examples) 文件夹。


## [0.0.1] - 2024-7-15
### Added
- 新增 [README-en.md](README-en.md)。
- 新增示例文件 ``DataLoader.py``。

### Changed
- 将中文示例 [README-zh.md](README-zh.md) 从 [README.md](../README.md) 中独立出来，并将后者改为导航栏。


## [0.0.0] - 2024-7-15
### Added
- 创建了该项目，并完成了中文说明文档的编写。