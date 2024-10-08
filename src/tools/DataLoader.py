import os,re,io
import json
os.environ['PYGAME_HIDE_SUPPORT_PROMPT'] = "hide"
import pygame
import pyttsx3
import time,tqdm
import langid


STABLE_GAMES = [
    'senrenbanka',
    'sanoba witch'
]

STABLE_DICT = {
    'senrenbanka':{
        'nexts':{
            'target':lambda data:data['target'] if 'target' in data else None,
            'storage':lambda data:data['storage'] if 'storage' in data else Config.defualt_location
        },
        'selects':{
            'target':lambda data:data['target'] if 'target' in data else None,
            'storage':lambda data:data['storage'] if 'storage' in data else Config.defualt_location
        },
        'texts':{
            'speaker':lambda data:data[0] if data[0] else Config.defualt_name,
            'content':lambda data:data[2] if data[2] else Config.defualt_content,
            'sound':lambda data:[sound for sound in data[3]] if data[3] else []
        }
    },
    'sanoba witch':{
        'nexts':{
            'target':lambda data:data['target'] if 'target' in data else None,
            'storage':lambda data:data['storage'] if 'storage' in data else Config.defualt_location
        },
        'selects':{
            'target':lambda data:data['target'] if 'target' in data else '*'+data['tag'],
            'storage':lambda data:data['storage'] if 'storage' in data else Config.defualt_location
        },
        'texts':{
            'speaker':lambda data:data[0] if data[0] else Config.defualt_name,
            'content':lambda data:data[2] if data[2] else Config.defualt_content,
            'sound':lambda data:[sound for sound in data[3]] if data[3] else []
        }
    },
    'cafe stella':{
        'nexts':{
            'target':lambda data:data['target'] if 'target' in data else None,
            'storage':lambda data:data['storage'] if 'storage' in data else Config.defualt_location
        },
        'selects':{
            'target':lambda data:data['target'] if 'target' in data else '*' + data['tag'],
            'storage':lambda data:data['storage'] if 'storage' in data else Config.defualt_location
        },
        'texts':{
            'speaker':lambda data:data[0] if data[0] else Config.defualt_name,
            'content':lambda data:data[1][0][1] if data[1][0] else Config.defualt_content,
            'sound':lambda data:[sound for sound in data[2]] if data[2] else None
        }
    }
}

'''
class AutoDataConfiger:
    def __init__(self):
        pass
    
    def auto_find_next_target():
        pass
    
    def auto_config(self, path:str):
        if not os.path.exists(path):
            raise FileNotFoundError(f'File {path} is not found.')
        
        scenes = json.load(open(path,'r',encoding=Config.encoding))
        
        # 以后这里也要用自动读取文件名的方式进行，要不然容易出问题
        name = scenes['name']
        
        name_dict = {}
        nexts = []
        selects = []
        texts = []
        
        for scene in scenes['scenes']:
            if 'label' not in scene:
                raise Exception("Auto config failed: label do not exist.")
            name_dict[scene['label']] = True
            
            if 'nexts' in scene:
                #raise Exception(f"Auto config failed: nexts do not exist in {scene['label']}.")
                for next in scene['nexts']:
                    nexts.append(next)
            
            if 'selects' in scene:
                for select in scene['selects']:
                    selects.append(select)
            
            if 'texts' in scene:
                texts += scene['texts']
        
        for next in nexts:
            for key, value in next.items():
                if not self.nexts['target']:
                    if value in name_dict:
                        print("nexts.target auto config success.")
                        self.nexts['target'] = lambda data: data[key] if key in data else None
                
                if not self.nexts['storage']:
                    if value == name:
                        print("nexts.storage auto config success.")
                        self.nexts['storage'] = lambda data: data[key] if key in data else Config.defualt_location
        
        for select in selects:
            for key, value in select.items():
                if not self.selects['target']:
                    if value in name_dict:
                        print("selects.target auto config success.")
                        self.selects['target'] = lambda data: data[key] if key in data else None
                        
                if not self.selects['storage']:
                    if value == name:
                        print("selects.storage auto config success.")
                        self.selects['storage'] = lambda data: data[key] if key in data else Config.defualt_location
                    else:
                        # 我希望在这里能够匹配 self.selects['storage']的else 里面的部分
                        pass
                    
'''
            
        
    

class Config:
    encoding = 'utf-8'
    audio_suffix = '.ogg'
    
    defualt_name = 'Defualt Name'
    defualt_location = 'Defualt location'
    
    defualt_speaker = 'Unknown'
    defualt_content = 'Unknown'
    
    filter = [r'%[^;]*;',
            r'\[[^\]]*\]',
            r'\\n']
    
    hide_tqdm = False
    debug = False
    
    version = 'senrenbanka'
    window = None
    stop_sound = False
    
    
    label_dict = {}
    #_else_selects_target = lambda x: x
    #_else_selects_storage = lambda x: x
    
    

def get_target_list(data={'nexts':[{'target':[],
                                    'storage':Config.defualt_location}]},
                    isselect=False,
                    defualt_storage=Config.defualt_location):
    """Format target list.

    Args:
        data (_type_): data dict.
        isselect (bool): type of data.

    Returns:
        target_list: a list of target(str).
    """
        
    '''
    return [(item['target'] if 'target' in item else None,
            (item['storage'] if 'storage' in item else defualt_storage)) 
            for item in (data['selects'] if isselect else data['nexts'])]
    '''
    
    '''
    items = []
    if isselect:
        items = data['selects']
    else:
        items = data['nexts']
        
    result_list = []
    
    for item in items:
        target, storage = None, None
        for key, value in item.items():
            if not isinstance(value, str):
                continue
            
            if not target:
                if value in Config.label_dict:
                    target = value
                    continue
                for label_name in Config.label_dict.keys():
                    if (value in label_name) or (label_name in value):
                        target = label_name
                        break    
            if not storage:
                if value.endswith('.ks'):
                    storage = value
        result_list.append((target, storage))
    
    return result_list
    '''
    
    return [(STABLE_DICT[Config.version]['selects' if isselect else 'nexts']['target'](item),
            STABLE_DICT[Config.version]['selects' if isselect else 'nexts']['storage'](item))
            for item in (data['selects'] if isselect else data['nexts'])
            if not STABLE_DICT[Config.version]['selects' if isselect else 'nexts']['target'](item) is None ]
    
    

class Select:
    def __init__(self,
                 data={'text':'Unknown',
                            'storage':Config.defualt_location,
                            'target':[]},
                 storage=Config.defualt_location):
        """A piece of select.

        Args:
            data (dict): Selection data.
        """
        self.text = data['text']
        self.target = []
        self.target = STABLE_DICT[Config.version]['selects']['target'](data)
        #self.target = data['target']
        '''
        for key, value in data.items():
            if value in Config.label_dict:
                self.target.append(value)
        
        if len(self.target) == 0:
            for key, value in data.items():
                for res in Config.label_dict:
                    if value in res:
                        self.target.append(res)
        '''
                        
        self.location = storage
    
    def __str__(self):
        return self.text

class ScnBase:
    def __init__(self,
                 name=Config.defualt_name,
                 location=Config.defualt_location,
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


# 我禁止了直接通过SoundData来播放，因为无法指定其地址，批量化进行太麻烦
class SoundData:
    def __init__(self,
                 owner={'speaker':Config.defualt_speaker,
                        'content':Config.defualt_content},
                 data={'voice':'defualt'}):
        """A piece of sound data.

        Args:
            owner (SceneText): The owner text of the sound.
            data (dict): Sound data.
        """
        self.owner = owner
        self.data = data
        
        self.voice = data['voice']

class SceneText:
    def __init__(self,owner=None,data=list()):
        """A piece of text data.

        Args:
            owner (Scene): The owner scene of the text.
            data (dict): _description_. Defaults to list().
        """
        self.owner = owner
        self.data = data
        
        '''
        self.speaker = data[0]
        self.content = data[2]
        self.sound = None
        '''
        #self.speaker = data[0]
        self.speaker = STABLE_DICT[Config.version]['texts']['speaker'](data)
        self.content = STABLE_DICT[Config.version]['texts']['content'](data)
        #self.content = None
        #cache_content = None
        """
        for content_data in data[1:]:
            if isinstance(content_data, str):
                lang, _ = langid.classify(content_data)
                # A funny fact: If sentence is like '......', it will be recognized as the Bengali language. XD
                if lang == 'zh' or lang == 'ja' or lang == 'bn':
                    self.content = content_data
                    break
                else:
                    print(content_data,lang)
                '''
                if content_data.startswith('「') and content_data.endswith('」'):
                    self.content = content_data
                    break
                if content_data.startswith('『') and content_data.endswith('』'):
                    self.content = content_data
                    break
                '''
            if isinstance(content_data, list):
                for value in content_data:
                    if not isinstance(value, list):
                        break
                    #print(value)
                    for sentence in value:
                        if isinstance(sentence, str):
                            lang, _ = langid.classify(sentence)
                            if lang == 'zh' or lang == 'ja' or lang == 'bn':
                                # 《星光咖啡馆与死神之蝶》中，其对话是以列表呈现，而非放在主列表内作为一个元素存在。
                                # 因此，需要重新给说话人命名，以防止发言被顶替。
                                # 我目前看到的就是这两种格式：要么不嵌套列表，可以直接读取；要么嵌套列表，要进入其中读取。
                                # 当然，也有昵称直接放在第一栏说话人那里的，因此要先把可能的句子存下来，等最后再释放。
                                if not cache_content:
                                    cache_content = sentence
                                    continue
                                self.content = sentence
                                break
                            else:
                                print(content_data,lang)
                            '''
                            if sentence.startswith('「') and sentence.endswith('」'):
                                self.content = sentence
                                break
                            '''
                    if self.content:
                        break
                if self.content:
                    break
                
        
        if not self.content:
            self.content = cache_content
        
        if not self.content:
            print(data)
            raise Exception()
        
        sound_index = -1
        for index, sound_data in enumerate(data):
            if not isinstance(sound_data, list):
                continue
            for sound_dict in sound_data:
                if 'voice' in sound_dict:
                    sound_index = index
                    break
            if sound_index != -1:
                break
        
        if sound_index != -1:
            self.sound = [SoundData(self,sound) for sound in data[sound_index]]
        else:
            self.sound = None
        """
        
        if not STABLE_DICT[Config.version]['texts']['sound'](data) is None:
            self.sound = [SoundData(self,sound) for sound in STABLE_DICT[Config.version]['texts']['sound'](data)]
        
        #if data[3] != None:
        #    self.sound = [SoundData(self,sound) for sound in data[3]]
    @property
    def fixcontent(self):
        filter = Config.filter
        content = self.content
        for rule in filter:
            try:
                content = re.sub(rule,'',content)
            except Exception as e:
                print(content)
                raise(e)
        return content
        
    def __str__(self):
        return f'【{self.speaker}】:{self.content}'
        
class Scene(ScnBase):
    def __init__(self,
                 name=Config.defualt_name,
                 location=Config.defualt_location,
                 data={},
                 owner=None):
        super().__init__(name,location,data)
        try:
            self.title = data['title']
        except KeyError:
            pass
        
        self.texts = None
        try:
            #self.texts = data['texts']
            self.texts = [SceneText(self,text) for text in data['texts']]
        except KeyError:
            pass
        
        self.selects = []
        if self.isselect:
            for single_data in data['selects']:
                #print(single_data)
                new_select = Select(single_data,location)
                self.selects.append(new_select)
        
        self.owner = owner
    
    def get_text(self):
        lines = []
        #lines.append(f'【{self.location}/{self.fixname}】'+'\n')
        
        if self.texts:
            for data in self.texts:
                name = data.speaker
                content = data.content
                for rule in Config.filter:
                    content = re.sub(rule,'',content)
                if name is not None:
                    lines.append(f'【{name}】'+':'+content+'\n')
                else:
                    lines.append(content+'\n')
                    
                    
        if self.selects != []:
            for select in self.selects:
                lines.append(select.text+'\n')
                    
                #lines.append(f'【{select.location}/{select.target}】')
                
                #lines.append('\n')
        else:
            lines.append('\n')
            #for target in self.target:
            #    lines.append(f'【{target.location}/{target.fixname}】'+'\n')
        return lines
    
    def exposeText(self,
                    output_path='',
                    watch_output=False,
                    output_file=None,
                    hide_location=True,
                    hide_target=True):
        """Save scene texts.

        Args:
            output_file (str, optional): Output file path. Set 'None' to use defualt path.
            watch_output (bool): Print output text in console. Defualts to False.
        """
        filter = Config.filter
        datas = self.texts
        
        defualt_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'outputs')
        close_mark = output_file is None
        if output_file is None:
            if output_path:
                if not os.path.exists(output_path):
                    os.makedirs(output_path)
                output_file = open(os.path.join(output_path,f'{self.fixname}'+'.txt'),'w+',encoding=Config.encoding)
            else:
                output_folder = os.path.join(defualt_path,f'{self.location}')
                if not os.path.exists(output_folder):
                    os.makedirs(output_folder)
                output_file = open(os.path.join(output_folder,f'{self.fixname}'+'.txt'),'w+',encoding=Config.encoding)
        
        if not isinstance(output_file,io.IOBase):
            raise TypeError('File type error!')
        
        if watch_output:
            print(f'【{self.location}/{self.fixname}】')
            
        if not hide_location:
            output_file.write(f'【{self.location}/{self.fixname}】'+'\n')
        
        if datas is None:
            print(f"No texts in {self.location}/{self._name}, pass.")
        else:
            for data in datas:
                name = data.speaker
                content = data.content
                for rule in filter:
                    content = re.sub(rule,'',content)
                if name != Config.defualt_name and name != None:
                    if watch_output:
                        print(f'【{name}】'+':'+content)
                    output_file.write(f'【{name}】'+':'+content+'\n')
                else:
                    if watch_output:
                        print(content)
                    output_file.write(content+'\n')
                    
        if not hide_target:
            if self.selects != []:
                for select in self.selects:
                    output_file.write(select.text+'\n')
                    if watch_output:
                        print(select.text)
                        
                    output_file.write(f'【{select.location}/{select.target}】')
                    if watch_output:
                        print(f'【{select.location}/{select.target}】',end='')
                    
                    output_file.write('\n')
                    if watch_output:
                        print('')
            else:
                for target in self.target:
                    output_file.write(f'【{target.location}/{target.fixname}】'+'\n')
                    if watch_output:
                        print(f'【{target.location}/{target.fixname}】')
                    
            
        
        if watch_output:
            print('')
        output_file.write('\n')
        
        if close_mark:
            output_file.close()
        
class Scenes:
    def __init__(self,path):
        """A whole scene file.

        Args:
            path (st): Scene file path.
        """
        
        self.path = path
        if not os.path.exists(self.path):
            raise FileNotFoundError(f'File {path} is not found.')
        
        scenes = json.load(open(path,'r',encoding=Config.encoding))
        self.hash = scenes['hash']
        self.name = scenes['name']
        
        for scene in scenes['scenes']:
            Config.label_dict[scene['label']] = True
        
        # 列出所有剧情片段
        # 由于《魔女的夜宴》中的数据报错，现在将所有设置归为场景处理 - version 3.0.0。
        self.scenes = []
        self.scene_index = {}
        if Config.debug or Config.window:
            print(f'Loading scenes from {self.name}...')
        if Config.hide_tqdm:
            for index,data in enumerate(scenes['scenes']):
                new_scene = Scene(data['label'],self.name,data,self)
                self.scenes.append(new_scene)
        else:
            for index,data in tqdm.tqdm(enumerate(scenes['scenes']),desc=f'Loading {self.name} scenes',total=len(scenes['scenes'])):
                new_scene = Scene(data['label'],self.name,data,self)
                self.scenes.append(new_scene)
        
        for index,data in enumerate(self.scenes):
            self.scene_index[data._name] = index
        
        if Config.debug or Config.window:
            print(f'Redirect from {self.name}...')
        if Config.hide_tqdm:
            for scene in self.scenes:
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
        else:
            for scene in tqdm.tqdm(self.scenes,desc=f'Redirect {self.name}',total=len(self.scenes)):
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
            
    def getIndexByName(self,name):
        return self.scene_index[name]
    
    def getNameByIndex(self,index):
        return self.scenes[index]._name

    def get_text(self):
        lines = []
        for scene in self.scenes:
            lines += scene.get_text()
            lines.append('\n')
        return lines
    
    # 导出清洗后文件
    def exposeText(self,
                    output_path='',
                    watch_output=False):
        """Save scene file texts.

        Args:
            output_file (str ,optional): Output file path. Set 'None' to use defualt path.
            watch_output (bool): Print output text in console. Defualts to False.
        """
        # 清洗后文件的默认输出位置，与 tools 同层
        defualt_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'outputs')
        
        output_file = None
        # 是否已经有指定位置
        if output_path == '':
            output_folder = os.path.join(defualt_path,f'{self.name}')
            if not os.path.exists(output_folder):
                os.makedirs(output_folder)
            output_file = open(os.path.join(output_folder,f'{self.name}'+'.txt'),'w+',encoding=Config.encoding)
        else:
            if not os.path.exists(output_path):
                os.makedirs(output_path)
            #output_file = open(output_path,'w+',encoding=Config.encoding)
            output_file = open(os.path.join(output_path,f'{self.name}'+'.txt'),'w+',encoding=Config.encoding)
        
        # 遍历每个文件并导出
        for scene in self.scenes:
            scene.exposeText(output_file=output_file,watch_output=watch_output)
            
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
                 name='defualt_scene'):
        """A whole scene file folder.

        Args:
            path (str): Folder path.
            name (str, optional): Name of folder. Defaults to 'defualt_scene'.
        """
        self.path = path
        if not os.path.exists(path):
            raise FileNotFoundError(f'Directory {path} is not found.')
        
        
        self.name = name
        
        # 读取所有非子目录的文件
        filedirs = [filename for filename in os.listdir(path) if filename.endswith('.ks.json')]
        self.datas = []
        self.data_index = {}
        for index,filename in enumerate(filedirs):
            if Config.debug or Config.window:
                print(f'Open {filename}.')
            filepath = os.path.join(path, filename)
            new_scene = Scenes(filepath)
            self.datas.append(new_scene)
            self.data_index[new_scene.name] = index
            if Config.debug or Config.window:
                print(f'{filename} Finished.')
                if Config.window:
                    Config.window.refresh()
                
        # 读完所有文件后再把跨文件的连接建立起来
        for data in self.datas:
            if Config.debug or Config.window:
                print(f'Fix {data.name} targets...')
            for scene in data.scenes:
                for target in scene.target:
                    if target._name is None:
                        try:
                            target._name = self.datas[self.data_index[target.location]][0]._name
                        except Exception as e:
                            print(f"something go wrong at {data.name}/{scene._name}\n(If this scene jumps to start menu, it occurs too and it goes without errors.)\n")
                            #print(scene._name)
                            #print(data.name)
                            continue
                            #raise e
                    if target.location != data.name:
                        aim_scenes = self.datas[self.data_index[scene.location]]
                        target_scene = aim_scenes.scenes[aim_scenes.getIndexByName(scene._name)]
                        scene = target_scene
            if Config.debug or Config.window:
                print(f'Fix {data.name} targets finished.')
                if Config.window:
                    Config.window.refresh()
                
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
        
        sound_path = os.path.join(self.path,sound.voice + Config.audio_suffix)
        if not os.path.exists(sound_path):
            raise FileNotFoundError(f'Sound File {sound_path} is not found.')
        
        if print_content:
            if sound.owner.speaker:
                print(f'【{sound.owner.speaker}】:{sound.owner.fixcontent}')
            else:
                print(sound.owner.fixcontent)
            if Config.window:
                Config.window.refresh()
            
        
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
            if Config.stop_sound:
                return
            if isinstance(sound,SceneText):
                if sound.sound == None:
                    if using_tts:
                        if print_content == True:
                            if sound.speaker:
                                print(f'【{sound.speaker}】:{sound.fixcontent}')
                            else:
                                print(sound.fixcontent)
                            if Config.window:
                                Config.window.refresh()
                        self.engine.say(sound.fixcontent)
                        self.engine.runAndWait()
                        time.sleep(interval)
                    elif print_content == True:
                        if sound.speaker:
                            print(f'【{sound.speaker}】:{sound.fixcontent}')
                        else:
                            print(sound.fixcontent)
                        if Config.window:
                            Config.window.refresh()
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
            if Config.stop_sound:
                return
            self.playScene(scene,wait_done,tick,interval,print_content,using_tts)