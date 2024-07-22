import os
import json
import sys
sys.path.append("..")
from src.tools.DataLoader import SoundManager,Scenes,Scnfolder

#file_path = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn_jp\\001・アーサー王ver1.07.ks.json"
file_path = "D:\\games\\sabbat of witch\\output\\data.xp3\\scn\\001.共通－オナニーマスター.ks.json"
#voice_path = "D:\\games\senrenbanka\\KrkrExtract_Output\\voice"
voice_path = "D:\\games\sabbat of witch\\output\\voice.xp3"

player = SoundManager(voice_path)
player.engine.setProperty('voice','HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Speech\Voices\Tokens\TTS_MS_JA-JP_HARUKA_11.0')
player.engine.setProperty('rate',150)

#datas = Scenes(file_path,suffix='.wav')
#player.playScenes(datas,interval=0.2,print_content=True,using_tts=True)

#files = Scnfolder("D:\\games\\senrenbanka\\outPath\\data.xp3\\scn_jp")
files = Scnfolder("D:\\games\\sabbat of witch\\output\\data.xp3\\scn")
for file in files:
    player.playScenes(file,interval=0.2,print_content=True,using_tts=True)