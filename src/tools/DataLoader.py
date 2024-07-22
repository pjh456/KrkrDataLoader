import json
import os
import re
import io
import pygame
import time
import tqdm
import pyttsx3


def get_target_list(data={'nexts':[{'target':[],
                                    'storage':'Defualt location'}]},
                    isselect=False,
                    defualt_storage='Defualt location'):
    """Format target list.

    Args:
        data (_type_): data dict.
        isselect (bool): type of data.

    Returns:
        target_list: a list of target(str).
    """
        
    return [(item['target'] if 'target' in item else None,
            (item['storage'] if 'storage' in item else defualt_storage)) 
            for item in (data['selects'] if isselect else data['nexts'])]

class Select:
    def __init__(self,
                 data={'text':'Unknown',
                            'storage':'defualt',
                            'target':[]},
                 storage='Defualt location'):
        """A piece of select.

        Args:
            data (dict): Selection data.
        """
        self.text = data['text']
        self.target = data['target'] if 'target' in data else '*'+data['tag']
        self.location = storage
    
    def __str__(self):
        return self.text

class ScnBase:
    def __init__(self,
                 name='Defualt Name',
                 location='Defualt Location',
                 data={}):
        """Base Scene class initialization.

        Args:
            name (str):Base Scene name.
            location (str):Base Scene file name.
            data (dict):Base Scene data.
        """
        self._name = name
        self.location = location
        self.isselect = 'selects' in data
        self.target = []
        if data != {}:
            self.target = get_target_list(data,self.isselect,location)
        self.data = data
        
    @property
    def fixname(self):
        """Fixed name which doesn't conflict with the system path.

        Returns:
            Fixed name: name after replace some sensitive characters.
        """
        fixname = self._name.replace('*','').replace(':','-')
        return fixname
    
    def __str__(self):
        return self.fixname


'''
class Setting(ScnBase):
    def __init__(self,
                 name='Defualt Name',
                 owner=None,
                 location='Defualt Location',
                 data={}):
        """A piece of setting which consists of BGM data,image data and so on.

        Args:
            name (str): Setting name.
            owner (Scene): The owner scene of the setting.
            location (str): Setting file name.
            data (dict): Setting file data.
        """
        super().__init__(name,location,data)
        self.owner = owner
        
        self.selects = []
        if self.isselect:
            for single_data in data['selects']:
                new_select = Select(single_data)
                self.selects.append(new_select)
'''

# 我禁止了直接通过SoundData来播放，因为无法指定其地址，批量化进行太麻烦
class SoundData:
    def __init__(self,
                 owner={'speaker':'Unknown','content':"Unknown"},
                 data={'voice':'defualt'},
                 suffix='.ogg'):
        """A piece of sound data.

        Args:
            owner (SceneText): The owner text of the sound.
            data (dict): Sound data.
            suffix (str): Sound file suffix.
        """
        self.owner = owner
        self.data = data
        
        #self.name = data['name']
        #self.pan = data['pan']
        #self.type = data['type']
        self.voice = data['voice']+suffix

class SceneText:
    def __init__(self,owner=None,data=list(),suffix='.ogg'):
        """A piece of text data.

        Args:
            owner (Scene): The owner scene of the text.
            data (dict): _description_. Defaults to list().
        """
        self.owner = owner
        self.data = data
        
        self.speaker = data[0]
        self.content = data[2]
        self.sound = None
        #print(owner._name,data[3],'?????????/')
        if data[3] != None:
            self.sound = [SoundData(self,sound,suffix) for sound in data[3]]
        
        
class Scene(ScnBase):
    def __init__(self,
                 name='Defualt Name',
                 location='Defualt Location',
                 data={},
                 setting=None,
                 suffix='.ogg'):
        """A piece of setting which consists of BGM data,image data and so on.

        Args:
            name (str): Setting name.
            location (str): Setting file name.
            data (dict): Setting file data.
            setting (Setting): Scene setting.
        """
        super().__init__(name,location,data)
        try:
            self.title = data['title']
        except KeyError:
            pass
        
        self.texts = None
        try:
            #self.texts = data['texts']
            self.texts = [SceneText(self,text,suffix) for text in data['texts']]
        except KeyError:
            pass
        self.setting = setting
        self.suffix = suffix
        
        self.selects = []
        if self.isselect:
            for single_data in data['selects']:
                #print(single_data)
                new_select = Select(single_data,location)
                self.selects.append(new_select)
    
    def exposeTextWithFilter(self,
                             filter=None,
                             output_file=None,
                             watch_output=False):
        """Save scene texts with filter.

        Args:
            filter (list, optional): A list of regular expression strs. Set 'None' to use defualt filter.
            output_file (str, optional): Output file path. Set 'None' to use defualt path.
            watch_output (bool): Print output text in console. Defualts to False.
        """
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
    def __init__(self,path,suffix='.ogg'):
        """A whole scene file.

        Args:
            path (st): Scene file path.
        """
        
        self.path = path
        if not os.path.exists(self.path):
            raise FileNotFoundError(f'File {path} is not found.')
        
        scenes = json.load(open(path,'r',encoding='utf-8'))
        self.hash = scenes['hash']
        self.name = scenes['name']
        self.suffix = suffix
        
        # 列出所有剧情片段
        # 由于《魔女的夜宴》中的数据报错，现在将所有设置归为场景处理 - version 3.0.0。
        self.scenes = []
        self.scene_index = {}
        for index,data in tqdm.tqdm(enumerate(scenes['scenes']),desc=f'Loading {self.name} scenes',total=len(scenes['scenes'])):
            # UNSTABLE
            # 这里使用的是奇偶交替判断，未来可能会报错。
            #if index%2 == 0:
            if True:
                new_scene = Scene(data['label'],self.name,data,None,suffix)
                self.scenes.append(new_scene)
        for index,data in enumerate(self.scenes):
            self.scene_index[data._name] = index
        
        '''
        # 列出所有设置
        self.settings = []
        self.setting_index = {}
        #for index,data in enumerate(scenes['scenes']):
        for index,data in tqdm.tqdm(enumerate(scenes['scenes']),desc=f'Loading {self.name} settings',total=len(scenes['scenes'])):
            # UNSTABLE
            if not 'texts' in data:
                new_setting = Setting(data['label'],None,self.name,data)
                self.settings.append(new_setting)
        for index,data in enumerate(self.settings):
            self.setting_index[data._name] = index
        '''
        
        for scene in tqdm.tqdm(self.scenes,desc=f'Redirect {self.name} targets',total=len(self.scenes)):
            cache_target = scene.target
            scene.target = []
            for name,storage in cache_target:
                try:
                    scene.target.append(self.scenes[self.scene_index[name]])
                except KeyError:
                    new_target = Scene(name,storage,{})
                    scene.target.append(new_target)
                except Exception as e:
                    print(e)
        
        '''
        # 把设置赋给剧情片段，并重定向 Scene.target
        for scene in tqdm.tqdm(self.scenes,desc=f'Redirect {self.name} targets',total=len(self.scenes)):
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
        '''
            
    def getIndexByName(self,name):
        return self.scene_index[name]
    
    def getNameByIndex(self,index):
        return self.scenes[index]._name

    # 导出清洗后文件
    def exposeTextWithFilter(self,
                             filter=None,
                             output_path=None,
                             watch_output=False):
        """Save scene file texts with filter.

        Args:
            filter (list, optional): A list of regular expression strs. Set 'None' to use defualt filter.
            output_file (str ,optional): Output file path. Set 'None' to use defualt path.
            watch_output (bool): Print output text in console. Defualts to False.
        """
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
    def __init__(self,
                 path,
                 name='defualt_scene',
                 debug=False,
                 suffix='.ogg'):
        """A whole scene file folder.

        Args:
            path (str): Folder path.
            name (str, optional): Name of folder. Defaults to 'defualt_scene'.
            debug (bool, optional): Debug mode which prints process of loading. Defaults to False.
        """
        self.path = path
        if not os.path.exists(path):
            raise FileNotFoundError(f'Directory {path} is not found.')
        
        
        self.name = name
        self.suffix = suffix
        
        # 读取所有非子目录的文件
        filedirs = [filename for filename in os.listdir(path) if filename.endswith('.ks.json')]
        self.datas = []
        self.data_index = {}
        #for index,filename in tqdm.tqdm(enumerate(filedirs),desc='Loading files',total=len(filedirs)):
        for index,filename in enumerate(filedirs):
            if debug:
                print(f'Open {filename}...')
            filepath = os.path.join(path, filename)
            new_scene = Scenes(filepath,suffix)
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
        """A tool using to play sounds simpler.

        Args:
            path (str): Where the voice files are in.
        """
        pygame.mixer.init()
        
        self.path = path
        if not os.path.exists(path):
            raise FileNotFoundError(f'Directory {path} is not found.')
        
        self.engine = pyttsx3.init()
        
    def playsound(self,
                  sound,
                  wait_done=True,
                  tick=0.1,
                  print_content=False):
        """Play a piece of sound.

        Args:
            sound (SoundData): A piece of sound data.
            wait_done (bool, optional): Blocking the stream or not. Defaults to True.
            tick (float, optional): How often does a checking to stop sound playing happens. Defaults to 0.1.
            print_content (bool, optional): Print lines of sound. Defaults to False.
        """
        if not isinstance(sound,SoundData):
            raise TypeError(f'{sound} must be SoundData.')
        
        sound_path = os.path.join(self.path,sound.voice)
        if not os.path.exists(sound_path):
            raise FileNotFoundError(f'Sound File {sound_path} is not found.')
        
        if print_content:
            if sound.owner.speaker:
                print(f'【{sound.owner.speaker}】:{sound.owner.content}')
            else:
                print(sound.owner.content)
            
        
        audio = pygame.mixer.Sound(sound_path)
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
            
    def playsounds(self,
                   soundlist=[],
                   wait_done=True,
                   tick=0.1,
                   interval=0.0,
                   print_content=False,
                   using_tts=False):
        """Play several pieces of sounds.

        Args:
            soundlist (list): List of sound.Supporting SceneText,SoundData and str type. Defaults to [].
            wait_done (bool, optional): Blocking the stream or not. Defaults to True.
            tick (float, optional): How often does a checking to stop sound playing happens. Defaults to 0.1.
            interval (float, optional): The time between two sounds. Defaults to 0.0.
            print_content (bool, optional): Print lines of sound. Defaults to False.
            using_tts (bool, optional): Using tts engine to fill lines without voice. Defaults to False.
        """
        for sound in soundlist:
            if isinstance(sound,SceneText):
                if sound.sound == None:
                    if using_tts:
                        if print_content == True:
                            if sound.speaker:
                                print(f'【{sound.speaker}】:{sound.content}')
                            else:
                                print(sound.content)
                        self.engine.say(sound.content)
                        self.engine.runAndWait()
                        time.sleep(interval)
                    elif print_content == True:
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
            
    def playScene(self,
                  scene,
                  wait_done=True,
                  tick=0.1,
                  interval=0.0,
                  print_content=False,
                  using_tts=False):
        """Play all the texts in a scene.

        Args:
            scene (Scene): The scene.
            wait_done (bool, optional): Blocking the stream or not. Defaults to True.
            tick (float, optional): How often does a checking to stop sound playing happens. Defaults to 0.1.
            interval (float, optional): The time between two sounds. Defaults to 0.0.
            print_content (bool, optional): Print lines of sound. Defaults to False.
            using_tts (bool, optional): Using tts engine to fill lines without voice. Defaults to False.
        """
        if not isinstance(scene,Scene):
            raise TypeError('scene must be Scene.')
        
        if scene.texts is None or len(scene.texts) == 0:
            print(f"No texts in {scene.location}/{scene._name}, pass.")
            return
        
        self.playsounds(scene.texts,wait_done,tick,interval,print_content,using_tts)
            
    def playScenes(self,scenes,wait_done=True,tick=0.1,interval=0.0,print_content=False,using_tts=False):
        """Play all the scene in a scene file.

        Args:
            scenes (Scenes): The scene file.
            wait_done (bool, optional): Blocking the stream or not. Defaults to True.
            tick (float, optional): How often does a checking to stop sound playing happens. Defaults to 0.1.
            interval (float, optional): The time between two sounds. Defaults to 0.0.
            print_content (bool, optional): Print lines of sound. Defaults to False.
            using_tts (bool, optional): Using tts engine to fill lines without voice. Defaults to False.
        """
        for scene in scenes:
            self.playScene(scene,wait_done,tick,interval,print_content,using_tts)