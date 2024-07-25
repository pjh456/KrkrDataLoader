import PySimpleGUI as sg
from src.tools.DecompileTool import Decompiler
from src.tools.DataLoader import Config,Scene,Scenes,Scnfolder,SoundManager
from src.tools.FgimageLoader import Layer,Group,Fgimage,FgimageFolder
from src.tools.GUIclasses import RedirectStdout
from PIL import Image,ImageTk
import os
import copy


treedata = sg.TreeData()
treedata.Insert('','none','none',[False,False])
# 定义各个子窗口的布局
layout_decode = [
    [
        sg.Column(
            layout=[
                [
                    sg.Text('文件选择')
                ]
            ],
            
            expand_x=True
        )
    ],
    [
        sg.Column(layout=[
            [
                sg.Column(
                    layout=[
                        [
                            sg.Input(size=(50, 10), key='-DECODEFILEPATH-',enable_events=True,readonly=True),
                            sg.FileBrowse('浏览文件',key='-DECODEFILECHOOSE-',target='-DECODEFILEPATH-')
                        ]
                    ]
                )
            ],
            [
                sg.Column(
                    layout=[
                        [
                            sg.Input(size=(50, 10), key='-DECODEFOLDERPATH-',enable_events=True,readonly=True), 
                            sg.FolderBrowse('浏览文件夹',key='-DECODEFOLDERCHOOSE-',target='-DECODEFOLDERPATH-')
                        ]
                    ]
                )
            ]
        ])
    ]
]

layout_scene = [
    [
        sg.Column(
            layout=[
                [
                    sg.Text('文件选择')
                ]
            ],
            expand_x=True
        )
    ],
    [
        sg.Column(
            layout=[
                [
                    sg.Column(
                        layout=[
                            [
                                sg.Input(size=(50, 10), key='-SCENEFILEPATH-',enable_events=True,readonly=True),
                                sg.FileBrowse('浏览文件',key='-SCENEFILECHOOSE-',target='-SCENEFILEPATH-')
                            ]
                        ]
                    )
                ],
                [
                    sg.Column(
                        layout=[
                            [
                                sg.Input(size=(50, 10), key='-SCENEFOLDERPATH-',enable_events=True,readonly=True), 
                                sg.FolderBrowse('浏览文件夹',key='-SCENEFOLDERCHOOSE-',target='-SCENEFOLDERPATH-')
                            ]
                        ]
                    )
                ]
            ]
        )
    ],
    [
        sg.Column(
            layout=[
                [
                    sg.Text('文本预览'),
                    sg.Input(size=(25, 10), key='-TEXTOUTPUTPATH-',readonly=True), 
                    sg.FolderBrowse('浏览导出路径',key='-TEXTOUTPUT-'),
                    sg.Button('导出当前文本',key='-CURRENTTEXTOUTPUT-'),
                    sg.Button('导出全部文本',key='-ALLTEXTOUTPUT-')
                ],
                [
                    sg.Tree(data=treedata,
                    headings=['Has texts','Has selects'],
                    auto_size_columns=True,
                    select_mode=sg.TABLE_SELECT_MODE_EXTENDED,
                    num_rows=5,
                    col0_width=5,
                    key='-SCENETREE-',
                    show_expanded=False,
                    enable_events=True,
                    expand_x=True,
                    expand_y=True,
                    )  
                ],
                [
                    sg.Input(key='-SOUNDPATH-',expand_x=True,enable_events=True,readonly=True),
                    sg.FolderBrowse('浏览音频文件夹',key='-SOUNDPATHCHOOSE-',target='-SOUNDPATH-')
                ],
                [
                    sg.Button('播放音频',key='-PLAYSOUND-',size=(None,None),expand_x=True)
                ],
                [
                    sg.Multiline(size=(None,50),key='-TEXT-')
                ]
            ]
        )
    ]
]

layout_fgimage = [
    [
        sg.Column(
            layout=[
                [
                    sg.Text('文件选择')
                ]
            ],
            expand_x=True
        )
    ],
    [
        sg.Column(
            layout=[
                [
                    sg.Column(
                        layout=[
                            [
                                sg.Input(size=(50, 10), key='-IMGFILEPATH-',enable_events=True,readonly=True),
                                sg.FileBrowse('浏览文件',key='-IMGFILECHOOSE-',target='-IMGFILEPATH-')
                            ]
                        ]
                    )
                ],
                [
                    sg.Column(
                        layout=[
                            [
                                sg.Input(size=(50, 10), key='-IMGFOLDERPATH-',enable_events=True,readonly=True), 
                                sg.FolderBrowse('浏览文件夹',key='-IMGFOLDERCHOOSE-',target='-IMGFOLDERPATH-')
                            ]
                        ]
                    )
                ]
            ]
        )
    ],
    [
        sg.Text('立绘导出'),
        sg.Input(size=(25, 10), key='-IMGOUTPUTPATH-',readonly=True), 
        sg.FolderBrowse('浏览导出路径',key='-IMGOUTPUT-'),
        sg.Button('导出当前立绘',key='-CURRENTIMGOUTPUT-')
    ],
    [
        sg.Column(
            layout=[
                [
                    sg.Text('立绘预览')
                ],
                [
                    sg.Combo([],[],size=(None,5),key='-CHARACTER-',enable_events=True,readonly=True,expand_x=True),
                    sg.Combo([],[],size=(None,5),key='-GROUP-',enable_events=True,readonly=True,expand_x=True),
                    sg.Combo([],[],size=(None,5),key='-LAYER-',enable_events=True,readonly=True,expand_x=True),
                    sg.Button('添加',key='-ADDLAYER-',enable_events=True)
                ]
            ],
            expand_x=True
        )
    ],
    [
        sg.Column(
            layout=[
                [
                    sg.Listbox(values=[], key='-LAYERLIST-', enable_events=True,size=(20,9)),
                    sg.Image(expand_x=True,expand_y=True,key='-PREVIEW-',size=(17,12)),
                    sg.Image(expand_x=True,expand_y=True,key='-IMAGE-',size=(17,12))
                ]
            ],
            size=(None,250),
            expand_x=True,
            element_justification='center'
        )
    ],
    [
        sg.Column(
            layout=[
                [
                    sg.Button('上移', key='-LAYERUP-'), 
                    sg.Button('下移', key='-LAYERDOWN-')
                ]
            ],
            expand_x=True
        )
    ]
]
# 主窗口布局
layout = [
    [sg.TabGroup([[sg.Tab('数据解析', layout_decode), 
                   sg.Tab('剧情处理', layout_scene),
                   sg.Tab('立绘处理', layout_fgimage)]],
                  key='-TABGROUP-', enable_events=True,
                  size=(None,500))],
    
    [
        sg.Column(
            layout=[
                [
                    sg.Text('终端结果')
                ],
                [
                    sg.Multiline(size=(None,5), horizontal_scroll=False,key='-PROCESS-')
                ]
            ],
            expand_x=True
        )
    ]
]

window = sg.Window('主窗口', layout)

Config.debug = True
Config.hide_tqdm = False
Config.window = window
decomplier = Decompiler()
soundManager = None
scenes, scenefolder= None, None
scene_dict, scenes_dict = {}, {}
event, values = None, None
layer_dict, group_dict,character_dict = {}, {}, {}
layer, group, character, fgimagefolder = None, None, None, None
layer_list = []
last_selected_index = None
max_graph_size = (200,200)
result_image = None

def show_text(value=None):
    if (not value) and values['-SCENETREE-'][0] == 'none':
        return 
    if not value:
        value = values['-SCENETREE-'][0]
        
    if value in scenes_dict:
        view_scenes = scenes_dict[value]
    else:
        view_scenes = scene_dict[value]
    #if value:
    #    window['-COMBO-'].update(value=value)
    #view_scenes = scenes_dict[value]
    lines = view_scenes.get_text()
    lines = ''.join(lines)
    window['-TEXT-'].update(value=lines)
    
def build_scene_tree(scenes,treedata):
    treedata.Insert('',scenes.name,scenes.name,['True','True'])
    for scene in scenes:
        treedata.Insert(scenes.name,scene._name,scene._name,[(not scene.texts is None),scene.isselect])
    
def build_scenes_tree(scnfolder):
    new_treedata = sg.TreeData()
    for scenes in scnfolder:
        build_scene_tree(scenes,new_treedata)
    return new_treedata

def update_graph():
    global result_image
    if not character:
        return
    if len(layer_list) > 0:
        new_list = layer_list.copy()
        new_list.reverse()
        result_image = character.get_image([layer.index for layer in new_list])
        view_image = result_image.copy()
        view_image.thumbnail(max_graph_size)
        window['-IMAGE-'].update(data=ImageTk.PhotoImage(view_image))

while True:
    #print(treedata)
    event, values = window.read(timeout=100)
    #print(event,values)
    #window.refresh()
    if event == sg.WINDOW_CLOSED:
        break
    if event == '-DECODEFILEPATH-':
        with RedirectStdout(window['-PROCESS-']):
            for line in decomplier.decompile(values['-DECODEFILEPATH-'],True):
                window['-PROCESS-'].print(line, end='')
                window.refresh()
        
    if event == '-DECODEFOLDERPATH-':
        with RedirectStdout(window['-PROCESS-']):
            for line in decomplier.decompile_all(values['-DECODEFOLDERPATH-'],True):
                window['-PROCESS-'].print(line, end='')
                window.refresh()
                
    if event == '-SCENEFILEPATH-':
        with RedirectStdout(window['-PROCESS-']):
            scenes = Scenes(values['-SCENEFILEPATH-'])
            window['-PROCESS-'].print('Done.', end='')
            #window['-COMBO-'].update(values=[scenes.name],value=scenes.name)
            scenes_dict = {scenes.name:scenes}
            scene_dict = {scene._name:scene for scene in scenes}
            treedata = sg.TreeData()
            build_scene_tree(scenes,treedata)
            window['-SCENETREE-'].update(values=treedata)
            window.refresh()
        show_text(scenes.name)
        
    if event == '-SCENEFOLDERPATH-':
        with RedirectStdout(window['-PROCESS-']):
            scenefolder = Scnfolder(values['-SCENEFOLDERPATH-'])
            window['-PROCESS-'].print('Done.', end='')
            #window['-COMBO-'].update(values=[data.name for data in scenefolder.datas],value=scenefolder[0].name)
            scenes_dict = {data.name: data for data in scenefolder.datas}
            scene_dict = {scene._name:scene for data in scenefolder.datas for scene in data}
            treedata = build_scenes_tree(scenefolder)
            window['-SCENETREE-'].update(values=treedata)
            window.refresh()
        show_text(scenefolder[0].name)
    
    if event == '-SCENETREE-':
        show_text()
        
    if event == '-CURRENTTEXTOUTPUT-':
        if  len(values['-SCENETREE-']) == 0 or values['-SCENETREE-'][0] == 'none':
            window['-PROCESS-'].print('Please choose a file first!', end='\n')
            continue
        value = values['-SCENETREE-'][0]
        if value in scenes_dict:
            view_scenes = scenes_dict[value]
        else:
            view_scenes = scene_dict[value]
        #print(len(values['-TEXTOUTPUTPATH-']),type(values['-TEXTOUTPUTPATH-']))
        view_scenes.exposeText(values['-TEXTOUTPUTPATH-'])
        window['-PROCESS-'].print('Done.', end='')
        
    if event == '-ALLTEXTOUTPUT-':
        if  (not scene_dict) and (not scenes_dict):
            window['-PROCESS-'].print('Please choose a folder or a file first!', end='\n')
            continue
        for key,value in scenes_dict.items():
            value.exposeText(values['-TEXTOUTPUTPATH-'])
        window['-PROCESS-'].print('Done.', end='')

    
    if event == '-SOUNDPATH-':
        with RedirectStdout(window['-PROCESS-']):
            soundManager = SoundManager(values['-SOUNDPATH-'])
        
    if event == '-PLAYSOUND-':
        if not soundManager:
            window['-PROCESS-'].print('Please choose the voice folder first!', end='\n')
            continue
        if  len(values['-SCENETREE-']) == 0 or values['-SCENETREE-'][0] == 'none':
            window['-PROCESS-'].print('Please choose a file first!', end='\n')
            continue
        value = values['-SCENETREE-'][0]
        with RedirectStdout(window['-PROCESS-']):
            if value in scenes_dict:
                listen_scenes = scenes_dict[value]
                soundManager.playScenes(listen_scenes,True,0.1,0.1,True,True)
                
            else:
                listen_scene = scene_dict[value]
                soundManager.playScene(listen_scenes,True,0.1,0.1,True,True)
                
    if event == '-IMGFILEPATH-':
        layer_list = []
        window['-LAYERLIST-'].update(values=[])
        
        last_selected_index = None
        with RedirectStdout(window['-PROCESS-']):
            character = Fgimage(values['-IMGFILEPATH-'])
            group = character.groups[0]
            layer = group.layers[0]
            
            character_dict = {character.name: character}
            group_dict = {group.name: group for group in character.groups}
            layer_dict = {layer.name: layer for group in character.groups for layer in group.layers}
            
            window['-CHARACTER-'].update(values=[character.name],value=character.name)
            window['-GROUP-'].update(values=[group.name for group in character.groups],value=group.name)
            window['-LAYER-'].update(values=[layer.name for layer in group.layers],value = layer.name)
            
            new_layer = copy.deepcopy(layer)
            window['-PREVIEW-'].update(data=ImageTk.PhotoImage(new_layer.load_image(max_graph_size)))
    
    if event == '-IMGFOLDERPATH-':
        layer_list = []
        window['-LAYERLIST-'].update(values=[])
        
        last_selected_index = None
        with RedirectStdout(window['-PROCESS-']):
            fgimagefolder = FgimageFolder(values['-IMGFOLDERPATH-'])
            window['-PROCESS-'].print('Done.', end='')
            
            character = fgimagefolder.fgimages[0]
            group = character.groups[0]
            layer = group.layers[0]
            
            character_dict = {character.name: character for character in fgimagefolder}
            group_dict = {group.name: group for character in fgimagefolder for group in character.groups}
            layer_dict = {layer.name: layer for character in fgimagefolder for group in character.groups for layer in group.layers}
            
            window['-CHARACTER-'].update(values=[character.name for character in fgimagefolder.fgimages],value=character.name)
            window['-GROUP-'].update(values=[group.name for group in character.groups],value=group.name)
            window['-LAYER-'].update(values=[layer.name for layer in group.layers],value = layer.name)
            
            new_layer = copy.deepcopy(layer)
            window['-PREVIEW-'].update(data=ImageTk.PhotoImage(new_layer.load_image(max_graph_size)))
            
    if event == '-CURRENTIMGOUTPUT-':
        save_path = os.path.join(os.path.dirname(os.path.abspath(__file__)),'src/output')
        if values['-IMGOUTPUTPATH-']:
            save_path = values['-IMGOUTPUTPATH-']
        if not os.path.exists(save_path):
            os.makedirs(save_path)
        save_index = 0
        while True:
            if not os.path.exists(os.path.join(save_path,f'fgimage{save_index}.png')):
                result_image.save(os.path.join(save_path,f'fgimage{save_index}.png'))
                break
            save_index += 1
        

    if event == '-CHARACTER-':
        character = character_dict[values['-CHARACTER-']]
        group = character.groups[0]
        layer = group.layers[0]
        
        group_dict = {group.name: group for group in character.groups}
        layer_dict = {layer.name: layer for group in character.groups for layer in group.layers}
        
        window['-GROUP-'].update(values=[group.name for group in character.groups],value=group.name)
        window['-LAYER-'].update(values=[layer.name for layer in group.layers],value=layer.name)
        
        new_layer = copy.deepcopy(layer)
        window['-PREVIEW-'].update(data=ImageTk.PhotoImage(new_layer.load_image(max_graph_size)))
        
    if event == '-GROUP-':
        group = group_dict[values['-GROUP-']]
        layer = group.layers[0]
        
        layer_dict = {layer.name: layer for group in character.groups for layer in group.layers}
        
        window['-LAYER-'].update(values=[layer.name for layer in group.layers],value=layer.name)
        
        new_layer = copy.deepcopy(layer)
        window['-PREVIEW-'].update(data=ImageTk.PhotoImage(new_layer.load_image(max_graph_size)))
    
    if event == '-LAYER-':
        layer = layer_dict[values['-LAYER-']]
        new_layer = copy.deepcopy(layer)
        window['-PREVIEW-'].update(data=ImageTk.PhotoImage(new_layer.load_image(max_graph_size)))
        
    if event == '-ADDLAYER-':
        new_layer = layer
        if new_layer and new_layer not in layer_list:
            layer_list.append(new_layer)
            window['-LAYERLIST-'].update(values=[layer.name for layer in layer_list])
            update_graph()
    
    if event == '-LAYERLIST-':  # 当选择列表项时
        selected_indices = window['-LAYERLIST-'].get_indexes()
        if selected_indices:
            last_selected_index = selected_indices[0]

    if event == '-LAYERUP-':
        if last_selected_index is not None and last_selected_index > 0:
            layer_list[last_selected_index], layer_list[last_selected_index - 1] = layer_list[last_selected_index - 1], layer_list[last_selected_index]
            window['-LAYERLIST-'].update(values=[layer.name for layer in layer_list])
            # 更新 last_selected_index 以反映新的位置
            last_selected_index -= 1
            update_graph()  # 立即调用 update_graph

    if event == '-LAYERDOWN-':
        if last_selected_index is not None and last_selected_index < len(layer_list) - 1:
            layer_list[last_selected_index], layer_list[last_selected_index + 1] = layer_list[last_selected_index + 1], layer_list[last_selected_index]
            window['-LAYERLIST-'].update(values=[layer.name for layer in layer_list])
            # 更新 last_selected_index 以反映新的位置
            last_selected_index += 1
            update_graph()  # 立即调用 update_graph

window.close()