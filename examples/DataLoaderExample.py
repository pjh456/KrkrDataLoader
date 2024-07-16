import os
import json
import sys
sys.path.append("..")
from src.tools.DataLoader import Datas

file_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.json"

data = Datas(file_path)

for i in range(len(data)):
    print(f"{data[i]}->{data[i].goto()}")
    