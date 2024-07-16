from pydub import AudioSegment
from pydub.playback import play
import time
import pygame

import os
import json

pygame.mixer.init()

sound_path_head = 'D:\\games\\senrenbanka\\KrkrExtract_Output\\voice'
folder_path = os.getcwd()

for root, dirs, files in os.walk(folder_path):
    for filename in files:
        if filename.endswith('.ks.json'):
            with open(filename,'r',encoding='utf-8') as f:
                datas = json.load(f)
                datas = datas['scenes'][2]['texts']
                #index = 3
                for data in datas:
                #print(datas['scenes'][2]['texts'][index])
                #datas['scenes'][2]['texts'][遍历文本][1]
                    #print("角色:",datas['scenes'][2]['texts'][index][0])
                    #print("文本:",datas['scenes'][2]['texts'][index][2])
                    if data[0] is None:
                        print(data[2])
                    else:
                        print(data[0]
                            + ":"
                            + data[2])
                    if data[3] is not None:
                        #print(data[3][0]['voice'])
                        sound_path = os.path.join(sound_path_head,data[3][0]['voice']+'.ogg')
                        audio = pygame.mixer.Sound(sound_path)
                        audio.play()
                        
                        """
                        audio.export('cache_audio.wav',format='wav')
                        time.sleep(1)
                        cache_audio = AudioSegment.from_file('cache_audio.wav')
                        play(cache_audio)
                        
"""