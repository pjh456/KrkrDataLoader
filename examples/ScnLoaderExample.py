import os
import json
import sys
sys.path.append("..")
from src.tools.ScnLoader import Scenes

file_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.json"

datas = Scenes(file_path)

for data in datas:
    print(f"{data}")

print('')

for data in datas:
    print(f"{data} -> {[target.fixname for target in  data.target]}: Text exists = {data.texts is not None}")
    
print('')

#datas[1].exposeTextWithFilter(watch_output = True)
datas.exposeTextWithFilter()
    