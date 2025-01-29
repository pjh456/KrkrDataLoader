## [0.2.0] -2025.1.29 **latest**
重构了配置路径框架，并修复了部分 BUG，测试通过。

接下来的版本（0.2.x）将着重构建配置文件的 API 与架构。

### Added
- 新增 SingleSetting 等多个配置类，用于细化配置职能。
- 提供了多种报错方式，并增加了多重确保来进行代码检查。

### Changed
- 整体重构了配置路径框架，暂时禁用了 CommandAutoLoader 的控制台配置功能。
- 修改了配置储存方式，再次使用对象池而非固定参数来指定，提高了代码的健壮性。
- 规范化了核心代码，重构了部分 KrkrData 子类，优化了代码结构，并提供了多态的方法调用声明。

### Fixed
- 修复了 Range header 的负数请求头问题，-1 应对应最后一个对象，因此整体下标 +1 以适应偏移。


## [0.1.5] -2025.1.29
为接下来路径自动配置器的大规模重构留档。

### Added
- 新增了配置用的 KrkrConfigController 和 KrkrConfigFileService 类，但内容暂未编写。

## [0.1.4] -2025.1.28
新年前的最后一更，之后在高考结束前会减少更新频率。
### Added
- 为 API 添加了 springdoc 的文档说明。

### Changed
- 移除了部分 KrkrResponseFactory 的方法，转而在 Controller 中使用 KrkrResponseBuilder 生成 KrkrResponse，提高了文件的可读性。

### Fixed
- 修复了未关闭 BufferedReader 导致的无法删除文件问题。

## [0.1.3] - 2025.1.27
### Added
- 定义了 KrkrResponse， 进一步封装了 Http 返回值。
- 添加了对 Spring Boot api 函数的签名 。
- 封装了部分内部函数用于简化 Controller 处理的逻辑，提高了代码的可读性。
- 添加了可支持范围下标的 RangeCallback 接口，可以在 Controller 中被调用，简化了 Range 字符串的处理逻辑。
- 增添了大量类和函数签名，提高了可读性。

### Changed
- 将 Controller 细化为场景文件处理的 SceneController，细化了 Controller 的权责。
- 将 Controller 与 Service 分离，降低了框架的耦合度。
- 将部分原来由 KrkrResponseFactory 处理的逻辑交由 Service 处理，更为合理。
- 将获取部分文件信息的 api 与获取全体文件的 url 路径划分开来，使得各自实现逻辑更为合理。

## [0.1.2] - 2025.1.24

### Added
- 添加了完整的 Monaco Editor Web Component，目前可以自主传参设置容器名称和传入文本的事件组名称。

### Changed
- 将 menu.html 中的编辑器组件替换为 Web Component 版本。
- 更改了 FileTree loadFile 事件的 detail 内容，统一化了编辑器调用方式。

### Fixed
- 修复了若干由 Monaco Editor 产生的 bug。

## [0.1.1] - 2025.1.22

### Added
- 增添了对切换页面的支持。

### Changed
- 简单封装了 Monaco Editor，使其能够作为 Web Component 进行使用。
- 细化了文件架构，分开了 HTML,CSS,JavaScript 文件的存放位置。

### Fixed
- 修复了编辑页不适应窗口大小的问题。


## [0.1.0] - 2025.1.20
完成了前端剧情解析部分的搭建，提供了完整读取的方案。

接下来的版本（0.1.*）中，将会在前端提供更多功能的支持，并再次优化页面。

### Added
- 实现了前后端的交互，并优化了页面。
- 使用 Monaco Editor 搭建了文件浏览页面。
- 提供了异步的加载方案，并为远程通信预设了轮询机制。
### Changed
- 略微修改了文件架构与引入的 NPM 库。

## [0.0.4]
完成了 Java 后端的剧情加载部分的搭建，提供了完整的读取剧情方案。

下个版本（0.1.0）将转向前端 Electron 的开发，提供可视化的加载方案。

在下下个大版本（0.2.0）中，将会提供自动化路径匹配方案的 API 方案。
### Changed
- 修改了 API 访问的方式，taskId 将会在路径中间出现以防止遗漏，提供了更符合逻辑的访问路径。
### Fixed
- 修复了异步访问的线程问题，提供了更高效的异步加载方式。


## [0.0.3] - 2025.1.13
### Added
- 添加了部分 API 的使用说明，添加了说明文档。
### Changed
- 部分修改了说明文档，更新了一部分新版本的内容。
- 修改了 KrkrData 的初始化方式，现在可以延后初始化以实现异步加载，但目前尚未支持。
- 提供了更加良好的 API 访问方式。
### Fixed
- 修复了使用 /scene/text 获取部分文本时 index 消息头无法正常指向的问题。
- 修复了多线程同时访问时出现的数据竞争问题。


## [0.0.2] - 2025.1.13
### Added
- 新增了多个 Restful API 用于交互文件。
- 新增了多个构造函数，用于简化读取流程。
- 新增了父子节点的交互方式，现在 KrkrData 可以通过 parent 属性访问父节点。
- 新增了 Electron 架构。
- 新增了响应体的工厂模式和建造者模式。
- 测试通过了后端响应。
- 新增了 KrkrData 的索引查询方式。
### Changed
- 修改了 gradle 的导入配置，移除了不必要的库。
- 改变了报错方式，跟 Restful API 有关错误将会以 HTTP 形式返回，不阻塞程序运行。
- 修改了配置文件的内容，以正常使用配置。
### Fixed
- 修复了配置文件无法正常读取的问题。


## [0.0.1] - 2025.1.11
### Fixed
- 修复了项目文档的语法错误。
### Changed
- 修改了项目说明。
### Added
- 新增更新日志。
- 新增 network Restful API 软件包。
