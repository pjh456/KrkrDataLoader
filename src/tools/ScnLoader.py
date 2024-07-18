import json
import os
import re
import io

def get_target_list(data,isselect):
    return [(item['target'],item['storage']) for item in (data['selects'] if isselect else data['nexts'])]

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
        
class Scene(ScnBase):
    def __init__(self,name,location,data=dict(),setting=None):
        super().__init__(name,location,data)
        try:
            self.title = data['title']
        except KeyError:
            pass
        self.texts = None
        try:
            self.texts = data['texts']
        except KeyError:
            pass
        self.setting = setting
    
    def exposeTextWithFilter(self,filter=None,output_file=None,watch_output=False):
        if filter == None:
            filter = [r'%[^;]*;',
               r'\[[^\]]*\]',
               r'\\n']
        datas = self.texts
        
        outputs_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'outputs')
        close_mark = output_file is None
        if output_file is None:
            output_folder = os.path.join(outputs_path,f'{self.location}')
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
        
        scenes = json.load(open(path,'r',encoding='utf-8'))
        self.hash = scenes['hash']
        self.name = scenes['name']
        self.outlines = scenes['outlines']
        
        self.scenes = []
        self.scene_index = {}
        for index,data in enumerate(scenes['scenes']):
            #if 'texts' in data:
            if index%2 == 0:
                new_scene = Scene(data['label'],self.name,data,None)
                self.scenes.append(new_scene)
        for index,data in enumerate(self.scenes):
            self.scene_index[data._name] = index
        
        #print(self.scene_index)
            
        self.settings = []
        self.setting_index = {}
        #for data in scenes['scenes']:
        for index,data in enumerate(scenes['scenes']):
            if index%2 == 1:
            #if not 'texts' in data:
                new_setting = Setting(data['label'],None,self.name,data)
                self.settings.append(new_setting)
        for index,data in enumerate(self.settings):
            self.setting_index[data._name] = index
        
        
        for scene in self.scenes:
            cache_target = scene.target
            scene.target = []
            for name,storage in cache_target:
                try:
                    new_setting = Setting(name,scene._name,storage,self.settings[self.setting_index[name]].data)
                    set_cache_target = new_setting.target
                    new_setting.target = []
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
                    new_target = Scene(name,storage,{})
                    scene.target.append(new_target)
                except Exception as e:
                    print(e)
            
    def getIndexByName(self,name):
        return self.scene_index[name]
    
    def getNameByIndex(self,index):
        return self.scenes[index]._name

    def exposeTextWithFilter(self,filter=None,output_path=None,watch_output=False):
        outputs_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'outputs')
        output_file = None
        
        if output_path is None:
            output_folder = os.path.join(outputs_path,f'{self.name}')
            if not os.path.exists(output_folder):
                os.makedirs(output_folder)
            output_file = open(os.path.join(output_folder,f'{self.name}'+'.txt'),'w+',encoding='utf-8')
        else:
            output_file = open(output_path,'w+',encoding='utf-8')
            
        for scene in self.scenes:
            scene.exposeTextWithFilter(filter,output_file,watch_output)
                
        output_file.close()
    
    def __getitem__(self,index):
        return self.scenes[index]
    
    def __len__(self):
        return len(self.scenes)
    
    