
// import {require} from '../../node_modules/monaco-editor/min/vs/loader.js';

// require.config({ paths: { 'vs': '../node_modules/monaco-editor/min/vs' } });

// window.MonacoEnvironment = {
//     getWorkerUrl: (moduleId, label) => {
//         return require.resolve('monaco-editor/esm/vs/editor/editor.worker');
//     }
// };


// require(['vs/editor/editor.main'], () => {

//     monaco.languages.register({ id: 'krkrLang' });

//     monaco.languages.setMonarchTokensProvider('krkrLang', {
//         tokenizer: {
//             root: [
//                 //[/\【/, 'speaker'],
//                 //[/\】/, 'speaker'],
//                 [/\「/, 'content'],
//                 [/\」/, 'content'],
//                 [/\『/, 'quote'],
//                 [/\』/, 'quote'],
//                 //[/\[/, 'definition'],
//                 //[/\]/, 'definition'],
//                 [/\【(.*?)\】/, 'speaker'],
//                 [/\[(.*?)\]/, 'definition'],
//             ]
//         }
//     });

//     monaco.editor.defineTheme('krkrTheme', {
//         base: 'vs-dark',  // 基础主题
//         inherit: true,  // 继承默认主题
//         rules: [
//             { token: 'speaker', foreground: "#a8ffff" },
//             { token: 'content', foreground: "#8ad8ff" },
//             { token: 'quote', foreground: "#8ad8ff" },
//             { token: 'definition', foreground: "#fbffad" },
//         ],
//         colors: {}
//     });
// });

// var loader_loaded;
// if (loader_loaded == undefined) {
//   loader_loaded = false;
// }

class editor extends HTMLElement {
  static get observedAttributes() {
    return ['cid','signals'];
  }

  constructor() {
    super();
    // 一个坑：不能直接使用this.signal，必须用一个内部属性存起来，因为setAttribute会将array变为string
    this._signals = [];
    this.editor=undefined;

    this.attachShadow({ mode: 'open' });
    
    this.createEditor();
  }

  // connectedCallback(){
  //   this.createEditor();
  // }

  createEditor() {
    // 插入 HTML 内容
    this.shadowRoot.innerHTML = `
          <link rel="stylesheet" href="../node_modules/monaco-editor/min/vs/editor/editor.main.css">
          <style>
            .view-line span{
                left:0;
            }
          </style>
          <div id="${this.cid}" style="width:100%;height:100%;"></div>
        `;

    const editorContainer = this.shadowRoot.getElementById(this.cid);

    // 动态加载 loader.js 并监听加载完成
    const script = document.createElement('script');
    script.src = '../node_modules/monaco-editor/min/vs/loader.js'; // 确保路径正确

    // 监听 script 标签加载完成
    // console.log(document.getElementById('editor-loader'));
    script.onload = () => {
      this.initializeMonacoEditor(editorContainer);
    };

    // 将 script 标签插入到 shadow DOM 中
    this.shadowRoot.appendChild(script);
  }

  initializeMonacoEditor(container) {
    // Monaco环境配置
    require.config({ paths: { 'vs': '../node_modules/monaco-editor/min/vs' } });

    window.MonacoEnvironment = {
      getWorkerUrl: (moduleId, label) => {
        return require.toUrl('monaco-editor/esm/vs/editor/editor.worker.js');
      }
    };

    // 加载 Monaco 编辑器
    require(['vs/editor/editor.main'], () => {
      this.editor = monaco.editor.create(container, {
        value: '',
        language: 'plaintext',
        theme: 'vs-dark',
        fontSize: 30,
        cursorBlinking: 'Solid',
        minimap: { enabled: true },
        folding: false,
        scrollBeyondLastLine: false,
        diagnosticsOptions: { validate: false, lint: false },
        lineNumbers: true,
        automaticLayout:true
      });

      // 获取自定义属性 'signals'
      const signalsAttr = this.getAttribute('signals');
      
      // 如果存在此属性，解析并转换为数组
      if (signalsAttr) {
        this.signals = signalsAttr;
      }
    });
  }

  setListeners(){
    // 添加新的事件监听
    this._signals.forEach(signal => {
      document.addEventListener(signal, (e) => {
        this.setValue(e.detail);
      });
    });
  }

  setValue(value){
    // console.log(value);
    this.editor.setValue(value);
  }

  get cid() {
    return this.getAttribute('cid') || 'editor-container';
  }

  set cid(value) {
    this.setAttribute('cid', value);
  }

  get signals() {
    return this.getAttribute('signals').split(',').map(event=>event.trim()) || [];
  }

  set signals(newSignals) {
    // this._signals.forEach(event => {
    //   document.removeEventListener(event, function (event) {
    //     console.log(event);
    //     this.setValue(textMap.get(event.detail));
    //   });
    // });

    this.setAttribute('signals', newSignals);
    this._signals = newSignals.split(',').map(event=>event.trim());

    this.setListeners();
  }

  // createEditor() {
  //     // 插入 HTML 内容
  //     this.shadowRoot.innerHTML = `
  //       <link rel="stylesheet" href="../node_modules/monaco-editor/min/vs/editor/editor.main.css">
  //       <div id="container-editor" style="width:100%;height:100%;"></div>
  //     `;

  //     const editorContainer = this.shadowRoot.getElementById('container-editor');

  //     // 动态加载 loader.js 并监听加载完成
  //     const script = document.createElement('script');
  //     script.src = '../node_modules/monaco-editor/min/vs/loader.js'; // 确保路径正确

  //     // 监听 script 标签加载完成
  //     script.onload = () => {
  //       this.initializeMonacoEditor(editorContainer);
  //     };

  //     // 将 script 标签插入到 shadow DOM 中
  //     this.shadowRoot.appendChild(script);
  //   }

  //   initializeMonacoEditor(container) {
  //     // Monaco环境配置
  //     require.config({ paths: { 'vs': '../node_modules/monaco-editor/min/vs' } });

  //     window.MonacoEnvironment = {
  //       getWorkerUrl: (moduleId, label) => {
  //         return require.toUrl('monaco-editor/esm/vs/editor/editor.worker.js');
  //       }
  //     };

  //     // 加载 Monaco 编辑器
  //     require(['vs/editor/editor.main'], () => {
  //       const editor = monaco.editor.create(container, {
  //         value: '',
  //         language: 'plaintext',
  //         theme: 'vs-dark',
  //         fontSize: 30,
  //         cursorBlinking: 'Solid',
  //         minimap: { enabled: true },
  //         folding: false,
  //         scrollBeyondLastLine: false,
  //         diagnosticsOptions: { validate: false, lint: false },
  //         lineNumbers: true
  //       });

  //       document.addEventListener("updateSceneContent", function (event) {
  //         editor.setValue(event.detail);
  //       });

  //       document.addEventListener("loadFile", function (event) {
  //         editor.setValue(textMap.get(event.detail));
  //       });
  //     });
  //   }

  // createEditor(){
  //     this.shadowRoot.innerHTML = `
  //         <script src="../node_modules/monaco-editor/min/vs/loader.js"></script>
  //         <link rel='stylesheet' href="./node_modules/monaco-editor/min/vs/editor/editor.main.css">
  //         <div id="container-editor" style="width:100%;height:100%;"></div>
  //     `;

  //     const editorContainer = this.shadowRoot.getElementById('container-editor');

  //     require(['vs/editor/editor.main'], () => {
  //         var editor = monaco.editor.create(editorContainer, {
  //             value: '',
  //             // language: 'krkrLang',
  //             // theme: 'krkrTheme',
  //             language:'plaintext',
  //             theme:'vs-dark',
  //             fontSize: 30,
  //             cursorBlinking: 'Solid',
  //             minimap: {
  //                 enabled: true
  //             },
  //             folding: false,
  //             scrollBeyondLastLine: false,
  //             diagnosticsOptions: {
  //                 // 禁用错误标记
  //                 validate: false,
  //                 lint: false
  //             },
  //             lineNumbers: true,
  //             automaticLayout:true
  //         });

  //         document.addEventListener("updateSceneContent", function (event) {
  //             editor.setValue(event.detail);
  //         });

  //         document.addEventListener("loadFile", function (event) {
  //             editor.setValue(textMap.get(event.detail));

  //         });
  //     });
  // }
}

export { editor };