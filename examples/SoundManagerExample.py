import os
import json
import sys
sys.path.append("..")
from src.tools.DataLoader import SoundManager,Scenes,Scnfolder

file_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.json"
voice_path = "D:\\games\senrenbanka\\KrkrExtract_Output\\voice"

datas = Scenes(file_path)
player = SoundManager(voice_path)
player.playScenes(datas,interval=0.2,print_content=True,using_tts=True)

#files = Scnfolder("D:\\games\\senrenbanka\\outPath\\data.xp3\\scn")
#for file in files:
#    player.playScenes(file,interval=0.2,print_content=True,using_tts=True)