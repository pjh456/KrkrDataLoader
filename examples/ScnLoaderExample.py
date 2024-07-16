import os
import json
import sys
sys.path.append("..")
from src.tools.ScnLoader import Scenes

file_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.json"

datas = Scenes(file_path)

for data in datas:
    print(f"{data}->{data.target}{data.texts is not None}")

datas[8].exposeTextWithFilter()
datas.exposeTextWithFilter()
    