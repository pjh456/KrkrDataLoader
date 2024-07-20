import os
import json
import sys
sys.path.append("..")
from src.tools.DataLoader import Scenes,Scnfolder,Select

file_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.json"
folder_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn"

datas = Scenes(file_path)
folder = Scnfolder(folder_path,debug=False)


for data in datas:
    print(f"{data}")
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

#datas[1].exposeTextWithFilter(watch_output = True)
#datas.exposeTextWithFilter()
