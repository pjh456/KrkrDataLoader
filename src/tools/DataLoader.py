import json
import os
import re
import io
import pygame
import time


def get_target_list(data,isselect):
    return [(item['target'] if 'target' in item else None,item['storage']) for item in (data['selects'] if isselect else data['nexts'])]

class Select:
    def __init__(self,data=dict()):
        self.text = data['text']
        self.location = data['storage']
        self.target = data['target']
    
    def __str__(self):
        return self.text

class ScnBase:
    def __init__(self,name,location,data={}):
        self._name = name
        self.location = location
        self.isselect = 'selects' in data
        self.target = []
        if data != {}:
            self.target = get_target_list(data,self.isselect)
        self.data = data
        
    @property
    def fixname(self):
        fixname = self._name.replace('*','').replace(':','-')
        return fixname
    
    def __str__(self):
        return self.fixname


class Setting(ScnBase):
    def __init__(self,name,owner,location,data=dict()):
        super().__init__(name,location,data)
        self.owner = owner
        
        self.selects = []
        if self.isselect:
            for single_data in data['selects']:
                new_select = Select(single_data)
                self.selects.append(new_select)

# 我禁止了直接通过SoundData来播放，因为无法指定其地址，批量化进行太麻烦
class SoundData:
    def __init__(self,owner={'speaker':'Unknown','content':"Unknown"},data={'voice':'defualt'},suffix='.ogg'):
        self.owner = owner
        self.data = data
        
        #self.name = data['name']
        #self.pan = data['pan']
        #self.type = data['type']
        self.voice = data['voice']+suffix

class SceneText:
    def __init__(self,owner,data=list()):
        self.owner = owner
        self.data = data
        
        self.speaker = data[0]
        self.content = data[2]
        self.sound = None
        #print(owner._name,data[3],'?????????/')
        if data[3] != None:
            self.sound = [SoundData(self,sound) for sound in data[3]]
        
        
class Scene(ScnBase):
    def __init__(self,name,location,data=dict(),setting=None):
        super().__init__(name,location,data)
        try:
            self.title = data['title']
        except KeyError:
            pass
        
        self.texts = None
        try:
            #self.texts = data['texts']
            print(len(data['texts']),self.texts,"::::::")
            self.texts = [SceneText(self,text) for text in data['texts']]
        except KeyError:
            pass
        self.setting = setting
    
    def exposeTextWithFilter(self,filter=None,output_file=None,watch_output=False):
        if filter == None:
            filter = [r'%[^;]*;',
               r'\[[^\]]*\]',
               r'\\n']
        datas = self.texts
        
        defualt_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'outputs')
        close_mark = output_file is None
        if output_file is None:
            output_folder = os.path.join(defualt_path,f'{self.location}')
            if not os.path.exists(output_folder):
                os.makedirs(output_folder)
            output_file = open(os.path.join(output_folder,f'{self.fixname}'+'.txt'),'w+',encoding='utf-8')
        
        if not isinstance(output_file,io.IOBase):
            raise TypeError('File type error!')
        
        if watch_output:
            print(f'【{self.location}/{self.fixname}】')
        output_file.write(f'【{self.location}/{self.fixname}】'+'\n')
        
        if datas is None:
            print(f"No texts in {self.location}/{self._name}, pass.")
        else:
            for data in datas:
                name = data[0]
                content = data[2]
                for rule in filter:
                    content = re.sub(rule,'',content)
                if data[0] is not None:
                    if watch_output:
                        print(f'【{name}】'+':'+content)
                    output_file.write(f'【{name}】'+':'+content+'\n')
                else:
                    if watch_output:
                        print(content)
                    output_file.write(content+'\n')
        
        if self.setting is not None and self.setting.isselect:
            for select in self.setting.selects:
                if watch_output:
                    print(f'【{select.text}】->【{select.target}】')
                output_file.write(f'【{select.text}】->【{select.target}】'+'\n')
        else:
            for target in self.setting.target:
                if watch_output:
                    print(f'【{self.location}/{target}】')
                output_file.write(f'【{self.location}/{target}】'+'\n')
        if watch_output:
            print('')
        output_file.write('\n')
        
        if close_mark:
            output_file.close()
        
class Scenes:
    def __init__(self,path):
        
        self.path = path
        if not os.path.exists(self.path):
            raise FileNotFoundError(f'File {path} is not found.')
        
        scenes = json.load(open(path,'r',encoding='utf-8'))
        self.hash = scenes['hash']
        self.name = scenes['name']
        
        # 列出所有剧情片段
        self.scenes = []
        self.scene_index = {}
        for index,data in enumerate(scenes['scenes']):
            # UNSTABLE
            # 这里使用的是奇偶交替判断，未来可能会报错。
            if index%2 == 0:
                new_scene = Scene(data['label'],self.name,data,None)
                self.scenes.append(new_scene)
        for index,data in enumerate(self.scenes):
            self.scene_index[data._name] = index
        
        # 列出所有设置
        self.settings = []
        self.setting_index = {}
        for index,data in enumerate(scenes['scenes']):
            # UNSTABLE
            # 这里使用的是奇偶交替判断，未来可能会报错。
            if index%2 == 1:
                new_setting = Setting(data['label'],None,self.name,data)
                self.settings.append(new_setting)
        for index,data in enumerate(self.settings):
            self.setting_index[data._name] = index
        
        # 把设置赋给剧情片段，并重定向 Scene.target
        for scene in self.scenes:
            cache_target = scene.target
            scene.target = []
            
            # 取出 Scene 的所有 Setting（通常只有一个）
            for name,storage in cache_target:
                try:
                    # 新建一个 Setting，与 scenes.settings 区分开
                    new_setting = Setting(name,scene._name,storage,self.settings[self.setting_index[name]].data)
                    set_cache_target = new_setting.target
                    new_setting.target = []
                    
                    # 取出每个 Setting 对应的 Scene
                    for set_name,set_storage in set_cache_target:
                        try:
                            new_setting.target.append(self.scenes[self.setting_index[set_name]])
                        except KeyError:
                            new_target = Scene(set_name,set_storage,{})
                            new_setting.target.append(new_target)
                        except Exception as e:
                            print(e)
                            
                    scene.setting = new_setting
                    
                    scene.target += new_setting.target
                except KeyError:
                    # 跨文件的时候得改。
                    new_target = Scene(name,storage,{})
                    scene.target.append(new_target)
                except Exception as e:
                    print(e)
            
    def getIndexByName(self,name):
        return self.scene_index[name]
    
    def getNameByIndex(self,index):
        return self.scenes[index]._name

    # 导出清洗后文件
    def exposeTextWithFilter(self,filter=None,output_path=None,watch_output=False):
        # 清洗后文件的默认输出位置，与 tools 同层
        defualt_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'outputs')
        
        output_file = None
        # 是否已经有指定位置
        if output_path is None:
            output_folder = os.path.join(defualt_path,f'{self.name}')
            if not os.path.exists(output_folder):
                os.makedirs(output_folder)
            output_file = open(os.path.join(output_folder,f'{self.name}'+'.txt'),'w+',encoding='utf-8')
        else:
            output_file = open(output_path,'w+',encoding='utf-8')
        
        # 遍历每个文件并导出
        for scene in self.scenes:
            scene.exposeTextWithFilter(filter,output_file,watch_output)
            
        # 关闭写入文件句柄    
        if output_file is not None:
            output_file.close()
    
    def __getitem__(self,index):
        return self.scenes[index]
    
    def __len__(self):
        return len(self.scenes)
    
    def __str__(self):
        return self.name
    
class Scnfolder:
    def __init__(self,path='',name='defualt_scene',debug=False):
        self.path = path
        if not os.path.exists(path):
            raise FileNotFoundError(f'Directory {path} is not found.')
        
        self.name = name
        
        # 读取所有非子目录的文件
        filedirs = [filename for filename in os.listdir(path) if filename.endswith('.ks.json')]
        self.datas = []
        self.data_index = {}
        for index,filename in enumerate(filedirs):
            if debug:
                print(f'Open {filename}...')
            filepath = os.path.join(path, filename)
            new_scene = Scenes(filepath)
            self.datas.append(new_scene)
            self.data_index[new_scene.name] = index
            if debug:
                print(f'{filename} Finished.')
                
        # 读完所有文件后再把跨文件的连接建立起来
        for data in self.datas:
            if debug:
                print(f'Fix {data.name} targets...')
            for scene in data.scenes:
                for target in scene.target:
                    if target._name is None:
                        target._name = self.datas[self.data_index[target.location]][0]
                    if target.location != data.name:
                        aim_scenes = self.datas[self.data_index[scene.location]]
                        target_scene = aim_scenes.scenes[aim_scenes.getIndexByName(scene._name)]
                        scene = target_scene
            if debug:
                print(f'Fix {data.name} targets finished.')
                
    def getIndexByName(self,name):
        return self.data_index[name]
    
    def getNameByIndex(self,index):
        return self.datas[index].name
        
    def __getitem__(self,index):
        return self.datas[index]
    
    def __str__(self):
        return f'Scenes files in {self.path}'
            
class SoundManager:
    def __init__(self,path):
        pygame.mixer.init()
        
        self.path = path
        if not os.path.exists(path):
            raise FileNotFoundError(f'Directory {path} is not found.')
        
        '''
        self.ffmpeg = ffmpeg_path
        if not os.path.exists(ffmpeg_path):
            raise FileNotFoundError(f'Directory {ffmpeg_path} is not found.')
        '''
        
    def playsound(self,sound,wait_done=True,tick=0.1,print_content=False):
        if not isinstance(sound,SoundData):
            raise TypeError(f'{sound} must be SoundData.')
        
        sound_path = os.path.join(self.path,sound.voice)
        if not os.path.exists(sound_path):
            raise FileNotFoundError(f'Sound File {sound_path} is not found.')
        
        audio = pygame.mixer.Sound(sound_path)
        if sound.owner.speaker:
            print(f'【{sound.owner.speaker}】:{sound.owner.content}')
        else:
            print(sound.owner.content)
        if wait_done:
            sound_length = audio.get_length()
            # 创建一个计时器
            clock = pygame.time.Clock()
            
            audio.play()

            # 等待声音播放完成
            time_waited = 0.0
            while time_waited < sound_length:
                time.sleep(tick)  # 防止CPU空转，可以调整睡眠时间
                time_waited += 0.1
                clock.tick(60)  # 控制循环的帧率
        else:
            audio.play()
            
    def playsounds(self,soundlist=list(),wait_done=True,tick=0.1,interval=0.0,print_content=False):
        for sound in soundlist:
            if isinstance(sound,SceneText):
                if sound.sound == None:
                    if print_content == True:
                        if sound.speaker:
                            print(f'【{sound.speaker}】:{sound.content}')
                        else:
                            print(sound.content)
                        time.sleep(interval)
                else:
                    self.playsounds(sound.sound,wait_done,tick,interval,print_content)
                continue
            if isinstance(sound,str):
                sound = SoundData(data={'name':'Unknown','voice':sound})
            if isinstance(sound,SoundData):
                self.playsound(sound,wait_done,tick,print_content)
                time.sleep(interval)
            else:
                raise TypeError(f'every element in soundlist must be SoundData, SceneText or str,but not {type(sound)}')
            
    def playScene(self,scene,wait_done=True,tick=0.1,interval=0.0,print_content=False):
        if not isinstance(scene,Scene):
            raise TypeError('scene must be Scene.')
        
        if scene.texts is None or len(scene.texts) == 0:
            print(f"No voice in {scene.location}/{scene._name}, pass.")
            return
        
        self.playsounds(scene.texts,wait_done,tick,interval,print_content)
            
    def playScenes(self,scenes,wait_done=True,tick=0.1,interval=0.0,print_content=False):
        for scene in scenes:
            self.playScene(scene,wait_done,tick,interval,print_content)