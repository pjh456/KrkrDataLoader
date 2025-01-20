
const intervalMap = new Map();
const countMap = new Map();

async function upload_scene_file(event) {
    taskId = '0d000721';

    var e = window.event || event;

    var file = e.target.files[0];
    var formData = new FormData();
    formData.append('file', file);

    console.log(file);
    await fetch('http://localhost:8080/krkr/api/scene/' + taskId + '/upload-file',
        {
            method: 'POST',
            body: formData
        });

    intervalMap.set(taskId, setInterval(() => {
        get_scene_info(taskId);
    }, 2000));

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

            }
        }).catch(error => console.log(error));
}

async function load_scenes_text(taskId) {
    for (let index = 0; index < countMap.get(taskId); index++) {
        load_single_scene_text(index);
    }
}

async function load_single_scene_text(scene_index) {
    fetch('http://localhost:8080/krkr/api/scene/' + taskId + '/text', {
        method: 'GET',
        headers: {
            'Index': scene_index
        }

    })
        .then(response => response.json())
        .then(data => {
            if (data.code == 200 || 206) {
                console.log(data.data);
            }
            else {

            }
        }).catch(error => console.log(error));
}
async function chuck_load_single_scene_text(scene_index) {
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
                                console.log(data.data);
                            }
                            else {
                
                            }
                        }).catch(error => console.log(error));
                }
            }
        });

}