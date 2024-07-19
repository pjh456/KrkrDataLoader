import os
import json
import sys
sys.path.append("..")
from src.tools.DataLoader import SoundManager,Scenes

file_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\111・芳乃－その後ver1.01.ks.json"
voice_path = "D:\\games\senrenbanka\\KrkrExtract_Output\\voice"

datas = Scenes(file_path)
player = SoundManager(voice_path)

player.playScenes(datas,interval=0.2,print_content=True)