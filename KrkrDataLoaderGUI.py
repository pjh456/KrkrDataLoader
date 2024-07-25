import PySimpleGUI as sg
from src.tools.DecompileTool import Decompiler
from src.tools.DataLoader import Config,Scene,Scenes,Scnfolder,SoundManager
from src.tools.FgimageLoader import Fgimage,FgimageFolder
from src.tools.GUIclasses import RedirectStdout


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
                    sg.Multiline(size=(None,50),key='-TEXT-')
                ]
            ]
        )
    ]
]

layout_sound = [
    [sg.Text('这是页面 2 的内容。')]
]

layout_fgimage = [
    [sg.Text('这是页面 3 的内容。')]
]
# 主窗口布局
layout = [
    [sg.TabGroup([[sg.Tab('数据解析', layout_decode), 
                   sg.Tab('剧情处理', layout_scene),
                   sg.Tab('音频播放', layout_sound),
                   sg.Tab('立绘处理', layout_fgimage)]],
                  key='-TABGROUP-', enable_events=True,
                  size=(None,400))],
    
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
#soundManager = SoundManager()
scenes, scenefolder= None, None
scene_dict, scenes_dict = {}, {}
event, values = None, None

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

window.close()