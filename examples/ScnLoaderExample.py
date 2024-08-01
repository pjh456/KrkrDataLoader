import os
import json
import sys
sys.path.append("..")
from src.tools.DataLoader import Scenes,Scnfolder,Select,Config

#Config.version = 'senrenbanka'
#Config.version = 'sanoba witch'
Config.version = 'cafe stella'


#file_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.json"
#file_path = "D:\\games\\sabbat of witch\\output\\data.xp3\\scn\\005.共通－因幡めぐる.ks.json"
file_path = "D:\\games\\cafeStella\\KrkrExtract_Output\\scn\\001.始まりver1.07.ks.json"
#folder_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn"
#folder_path = "D:\\games\\sabbat of witch\\output\\data.xp3\\scn"
folder_path = "D:\\games\\cafeStella\\KrkrExtract_Output\\scn"

datas = Scenes(file_path)
folder = Scnfolder(folder_path)

for data in datas:
    print(f"{data},{type(data)}")
print('')

for data in datas:
    print(f"{data} -> {[target.fixname for target in  data.target]}: Text exists = {data.texts is not None}")
print('')

for file in folder:
    print(f'{file.name}')
    for scene in file:
        print(f'    {scene._name}')
        for target in scene.target:
            print(f'        {target._name}')
print('')

print(datas.get_text())

#datas[3].exposeText(watch_output = True)
#datas.exposeText(watch_output = True)
