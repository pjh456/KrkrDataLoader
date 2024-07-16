## [1.0.2] - 2024-7-16 ***latest**
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
- 更新了 [ScnLoader.py](../src/tools/ScnLoader.py)，废弃了 ``Scene.goto()`` 方法，以 ``Scene.target`` 属性简化。

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