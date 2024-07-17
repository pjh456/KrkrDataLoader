import json
import os
import re
import io

class Select:
    def __init__(self,data=dict()):
        self.text = data['text']
        self.location = data['storage']
        self.target = data['target']
    
    def __str__(self):
        return self.text
    
class Setting:
    def __init__(self):
        pass
    
class Scene:
    def __init__(self,name,location,data=dict()):
        self._name = name
        self.__location = location
        try:
            self.title = data['title']
        except KeyError:
            pass
        self.texts = None
        try:
            self.texts = data['texts']
        except KeyError:
            pass
        self.isselect = 'selects' in data
        
        self.__selection = []
        if self.isselect:
            for single_data in data['selects']:
                new_select = Select(single_data)
                self.__selection.append(new_select)
        
        #if self.isselect:
            #print(data)
        try:
            self.target = [(item['target'],item['storage']) for item in (data['selects'] if self.isselect else data['nexts'])]
        except KeyError:
            pass
        
    @property
    def selects(self):
        if not self.isselect:
            raise TypeError('Not a selection.')
        return self.__selection
    
    @property
    def fixname(self):
        fixname = self._name.replace('*','').replace(':','-')
        return fixname
    
    def exposeTextWithFilter(self,filter=None,output_file=None,watch_output=False):
        if filter == None:
            filter = [r'%[^;]*;',
               r'\[[^\]]*\]',
               r'\\n']
        datas = None
        try:
            datas = self.texts
        except KeyError:
            print(f"No texts in {self.__location}/{self._name},pass.")
            return 
        
        outputs_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'outputs')
        close_mark = output_file is None
        if output_file is None:
            output_folder = os.path.join(outputs_path,f'{self.__location}')
            if not os.path.exists(output_folder):
                os.makedirs(output_folder)
            output_file = open(os.path.join(output_folder,f'{self.fixname}'+'.txt'),'w+',encoding='utf-8')
        
        if not isinstance(output_file,io.IOBase):
            raise TypeError('File type error!')
        
        if watch_output:
            print(f'【{self.fixname}】')
        output_file.write(f'【{self.fixname}】'+'\n')
        
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
                
        if self.isselect:
            for select in self.__selection:
                if watch_output:
                    print(f'【{select.name}】->【{select.target}】')
                output_file.write(f'【{select.name}】->【{select.target}】'+'\n')
        else:
            for target in self.target:
                if watch_output:
                    print(f'【{target}】')
                output_file.write(f'【{target}】'+'\n')
        if watch_output:
            print('')
        output_file.write('\n')
        
        if close_mark:
            output_file.close()
        
    def __str__(self):
        return self.fixname

class Scenes:
    def __init__(self,path):
        self.path = path
        
        scenes = json.load(open(path,'r',encoding='utf-8'))
        self.hash = scenes['hash']
        self.name = scenes['name']
        self.outlines = scenes['outlines']
        
        self.scenes = []
        for data in scenes['scenes']:
            new_scene = Scene(data['label'],self.name,data)
            self.scenes.append(new_scene)
        
        self.index = {}
        for index,data in enumerate(self.scenes):
            self.index[data._name] = index
            
        # Make targets as Scene class
        for scene in self.scenes:
            cache_target = scene.target
            scene.target = []
            for name,storage in cache_target:
                try:
                    scene.target.append(self.scenes[self.index[name]])
                except KeyError:
                    new_target = Scene(name,storage,{})
                    scene.target.append(new_target)
                except Exception as e:
                    print(e)
    
    def getIndexByName(self,name):
        return self.index[self.scenes[self.index[name]]['label']]
    
    def getNameByIndex(self,index):
        return self.scenes[index]['label']

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
            if scene.texts is not None:
                scene.exposeTextWithFilter(filter,output_file,watch_output)
                
        output_file.close()
    
    def __getitem__(self,index):
        return self.scenes[index]
    
    def __len__(self):
        return len(self.scenes)
    
    