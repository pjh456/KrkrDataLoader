
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

class editor extends HTMLElement{
    static get observedAttributes(){
        return ['data'];
    }

    constructor(){
        super();
        this.attachShadow({mode:'open'});
        this.createEditor();
    }
    createEditor() {
        // 插入 HTML 内容
        this.shadowRoot.innerHTML = `
          <link rel="stylesheet" href="../node_modules/monaco-editor/min/vs/editor/editor.main.css">
          <style>
            .view-line span{
                left:0;
            }
          </style>
          <div id="container-editor" style="width:100%;height:100%;"></div>
        `;
    
        const editorContainer = this.shadowRoot.getElementById('container-editor');
    
        // 动态加载 loader.js 并监听加载完成
        const script = document.createElement('script');
        script.src = '../node_modules/monaco-editor/min/vs/loader.js'; // 确保路径正确
    
        // 监听 script 标签加载完成
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
          const editor = monaco.editor.create(container, {
            value: '',
            language: 'plaintext',
            theme: 'vs-dark',
            fontSize: 30,
            cursorBlinking: 'Solid',
            minimap: { enabled: true },
            folding: false,
            scrollBeyondLastLine: false,
            diagnosticsOptions: { validate: false, lint: false },
            lineNumbers: true
          });
    
          document.addEventListener("updateSceneContent", function (event) {
            editor.setValue(event.detail);
          });
    
          document.addEventListener("loadFile", function (event) {
            editor.setValue(textMap.get(event.detail));
          });
        });
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

 export {editor};