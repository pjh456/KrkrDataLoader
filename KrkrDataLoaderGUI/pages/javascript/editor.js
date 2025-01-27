

class editor extends HTMLElement {
  static get observedAttributes() {
    return ['cid','signals','data'];
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
    // 还是有bug，全局变量会反复加载报错，但是不影响（看起来）
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
        this.data = e.detail;
      });
    });
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

  get data() {
    return this.editor.getValue();
  }

  set data(value) {
    this.editor.setValue(value);
  }
}

export { editor };