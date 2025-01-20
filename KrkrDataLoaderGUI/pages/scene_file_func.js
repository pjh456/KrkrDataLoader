
const intervalMap = new Map();
const countMap = new Map();

const textMap = new Map();

async function upload_scene_file(event) {
    // taskId = '0d000721';

    var e = window.event || event;
    
    var file = e.target.files[0];
    var formData = new FormData();
    formData.append('file', file);

    const taskId = file.name;

    console.log(taskId);

    console.log(file);
    await fetch('http://localhost:8080/krkr/api/scene/' + taskId + '/upload-file',
        {
            method: 'POST',
            body: formData
        });

    intervalMap.set(taskId, setInterval(() => {
        get_scene_info(taskId);
    }, 100));


}

async function get_scene_info(taskId) {
    fetch('http://localhost:8080/krkr/api/scene/' + taskId + '/info')
        .then(response => response.json())
        .then(data => {
            if (data.code == 200) {
                countMap.set(taskId, data.data.scene_count);
                console.log(data);
                clearInterval(intervalMap.get(taskId));
                load_scenes_text(taskId);
            }
            else {
                console.log(data);
            }
        }).catch(error => console.log(error));
}

async function load_scenes_text(taskId) {

    var path_data = [];
    var whole_data = [];
    var info_data;

    await fetch('http://localhost:8080/krkr/api/scene/' + taskId + '/info', {
        method: 'GET',
        headers: {
            'Range': "0-" + countMap.get(taskId)
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.code == 200) {
                info_data = data.data;
            }
        });
    
    // console.log(info_data);

    for (let index = 0; index < countMap.get(taskId); index++) {
        const single_data = await load_single_scene_text(taskId, index);
        
        path_data.push(info_data[index].name);
        textMap.set(info_data[index].name, single_data);

        if(single_data.length > 0)
        {
            whole_data.push('\n');
        }
        whole_data.push(single_data);

    }
    whole_data = whole_data.join('\n');

    textMap.set(taskId, whole_data);
    // console.log(path_data);
    
    // console.log(whole_data);

    document.dispatchEvent(new CustomEvent('updateSceneContent', { detail: whole_data }));  // 触发自定义事件

    document.dispatchEvent(new CustomEvent('updateFilePath', {
        detail: { 
            scene_name:taskId, 
            path_data: path_data 
        },
        bubbles: true,  // 确保事件冒泡
        composed: true  // 允许事件穿透 shadow DOM
    }));  // 触发自定义事件
}

async function load_single_scene_text(taskId, scene_index) {
    try{
        const response = await fetch('http://localhost:8080/krkr/api/scene/' + taskId + '/text', {
            method: 'GET',
            headers: {
                'Index': scene_index
            }
    
        })

        const data = await response.json();

        if (data.code == 200 || data.code == 206) {
            // console.log(data.data);
            // console.log(typeof data.data);
            return data.data.join('\n');
        }

    }
    catch (error) {
        console.error(error);
        return null;  // 处理错误并返回 null 或适当的默认值
    }
}
async function chuck_load_single_scene_text(taskId, scene_index) {
    fetch('http://localhost:8080/krkr/api/scene/' + taskId + '/info', {
        method: 'GET',
        headers: {
            'Range': scene_index
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.status == 200) {
                let scene_size = data.data.scene_count;
                for (let index = 0; index * index < scne_size; index++) {
                    fetch('http://localhost:8080/krkr/api/scene/' + taskId + '/text', {
                        method: 'GET',
                        headers: {
                            'Index': index
                        }

                    })
                        .then(response => response.json())
                        .then(data => {
                            if (data.code == 200 || 206) {
                                return data.data;
                            }
                            else {

                            }
                        }).catch(error => console.log(error));
                }
            }
        });

}