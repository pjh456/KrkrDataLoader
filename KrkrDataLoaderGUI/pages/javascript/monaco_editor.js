
// 这个封装不起来，会有bug，只能这么写了

require.config({ paths: { 'vs': '../node_modules/monaco-editor/min/vs' } });

window.MonacoEnvironment = {
    getWorkerUrl: (moduleId, label) => {
        return require.resolve('monaco-editor/esm/vs/editor/editor.worker');
    }
};

var editorContainer = document.getElementById("editor-container");

require(['vs/editor/editor.main'], () => {

    monaco.languages.register({ id: 'krkrLang' });

    monaco.languages.setMonarchTokensProvider('krkrLang', {
        tokenizer: {
            root: [
                //[/\【/, 'speaker'],
                //[/\】/, 'speaker'],
                [/\「/, 'content'],
                [/\」/, 'content'],
                [/\『/, 'quote'],
                [/\』/, 'quote'],
                //[/\[/, 'definition'],
                //[/\]/, 'definition'],
                [/\【(.*?)\】/, 'speaker'],
                [/\[(.*?)\]/, 'definition'],
            ]
        }
    });

    monaco.editor.defineTheme('krkrTheme', {
        base: 'vs-dark',  // 基础主题
        inherit: true,  // 继承默认主题
        rules: [
            { token: 'speaker', foreground: "#a8ffff" },
            { token: 'content', foreground: "#8ad8ff" },
            { token: 'quote', foreground: "#8ad8ff" },
            { token: 'definition', foreground: "#fbffad" },
        ],
        colors: {}
    });

    var editor = monaco.editor.create(editorContainer, {
        value: '',
        language: 'krkrLang',
        theme: 'krkrTheme',
        fontSize: 30,
        cursorBlinking: 'Solid',
        minimap: {
            enabled: true
        },
        folding: false,
        scrollBeyondLastLine: false,
        diagnosticsOptions: {
            // 禁用错误标记
            validate: false,
            lint: false
        },
        lineNumbers: true,
        automaticLayout:true
    });

    document.addEventListener("updateSceneContent", function (event) {
        editor.setValue(event.detail);
    });

    document.addEventListener("loadFile", function (event) {
        editor.setValue(textMap.get(event.detail));

    });

    editors.push(editor);


    // while(1)
    // {
    //     editor.layout();
    // }
    // const observer = new MutationObserver(() => {
    //     if (parentElement.style.display !== 'none') {
    //         editor.layout();  // 手动调整布局
    //     }
    // });

    // observer.observe(parentElement, { attributes: true, attributeFilter: ['style'] });
})