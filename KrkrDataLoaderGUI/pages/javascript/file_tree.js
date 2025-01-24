class fileTree extends HTMLElement {
    static get observedAttributes() {
        return ['data'];
    }

    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
        this.createFileTree();
    }

    connectedCallback() {
        window.addEventListener("updateFilePath",function(event){
            var new_data = this.data;
            new_data[event.detail.scene_name] = event.detail.path_data;
            // console.log(new_data);
            this.data = new_data;
            // file_tree = createFolderStructure(data);
            // console.log("update!");
            this.createFileTree();
        }.bind(this));
      }

    createFileTree(){
        // const shadowRoot = this.attachShadow({ mode: 'open' });
        this.shadowRoot.innerHTML = `
            <link rel='stylesheet' href="./css/file_tree.css">
        `;

        // console.log(this.data);
        const file_tree = this.createFolderStructure(this.data);
        file_tree.id = "file-tree-list";
        file_tree.className = "active";

        // console.log(file_tree);
        this.shadowRoot.appendChild(file_tree);

    }

    createFolderStructure(folderData){
        const ul = document.createElement('ul');
        ul.className = "hidden";

        Object.entries(folderData).forEach(([folderName, contents]) => {
            const li = document.createElement('li');

            const span = document.createElement('span');
            span.textContent = folderName;

            li.appendChild(span);

            if(typeof contents === 'object'){
                span.className = 'title';
                span.addEventListener("click", function() {
                    this.parentElement.querySelector(".hidden").classList.toggle("active");
                    this.classList.toggle("title-down");
                });

                li.appendChild(this.createFolderStructure(contents));
            }
            else{
                span.textContent = contents;
                span.className = 'file';
                span.addEventListener("click", function() {
                    document.dispatchEvent(new CustomEvent('loadFile', {
                        detail: textMap.get(this.textContent),
                        bubbles: true,  // 确保事件冒泡
                        composed: true  // 允许事件穿透 shadow DOM
                    }));  // 触发自定义事件
                });
            }

            if(Array.isArray(contents)){
                span.addEventListener("click", function() {
                    document.dispatchEvent(new CustomEvent('loadFile', {
                        detail: textMap.get(folderName),
                        bubbles: true,  // 确保事件冒泡
                        composed: true  // 允许事件穿透 shadow DOM
                    }));  // 触发自定义事件
                });
            }

            ul.appendChild(li);
        });

        return ul;
    }

    get data(){
        const dataAttr = this.getAttribute('data');
        return dataAttr ? JSON.parse(dataAttr) : {};
    }

    set data(value) {
        this.setAttribute('data', JSON.stringify(value));
    }
}

export {fileTree}