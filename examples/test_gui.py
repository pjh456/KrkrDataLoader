import PySimpleGUI as sg
import os
import json
import sys
sys.path.append("..")
from src.tools.DataLoader import Scenes,Scnfolder,Select,Config

#Config.version = 'sanoba witch'

file_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.json"
#file_path = "D:\\games\\sabbat of witch\\output\\data.xp3\\scn\\005.共通－因幡めぐる.ks.json"
folder_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn"
#folder_path = "D:\\games\\sabbat of witch\\output\\data.xp3\\scn"

datas = Scenes(file_path)
#folder = Scnfolder(folder_path)

treedata = sg.TreeData()
def build_scene_tree(scenes,treedata):
    treedata.Insert(scenes.owner.name if scenes.owner else '',scenes.hash,scenes.name,scenes.hash)
    for scene in scenes.scenes:
        treedata.Insert(scenes.hash,scene._name,scene._name,scene.location)

build_scene_tree(datas,treedata)
print(treedata)
tree=sg.Tree(data=treedata,
    headings=['location'],
   auto_size_columns=True,
   select_mode=sg.TABLE_SELECT_MODE_EXTENDED,
   num_rows=10,
   col0_width=5,
   key='-TREE-',
   show_expanded=False,
   enable_events=True,
   expand_x=True,
   expand_y=True,
)
layout=[[tree]]
window=sg.Window("Tree Demo", layout, size=(715, 200), resizable=True)
while True:
    event, values = window.read(timeout=100)
    print ("event:",event, "values:",values,values['-TREE-'])
    if event == sg.WIN_CLOSED:
        break