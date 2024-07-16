import json
import os

class Select:
    def __init__(self,data=dict()):
        self.text = data['text']
        self.location = data['storage']
        self.target = data['target']
    
class Scene:
    def __init__(self,name,location,data=dict()):
        self.name = name
        self.location = location
        self.title = data['title']
        self.__data = data
        self.isselect = 'selects' in data
        
        self.__selection = []
        for data in data['selects']:
            new_select = Select(data)
            self.__selection.append(new_select)
            
        self.target = [item['target'] for item in (data['selects'] if self.isselect else data['nexts'])]
        
    @property
    def selects(self):
        if not self.isselect:
            raise TypeError('Not a selection.')
        return self.__selection
    
    def __str__(self):
        return self.name

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
            self.index[data.name] = index
    
    def getIndexByName(self,name):
        return self.index[self.scenes[self.index[name]]['label']]
    
    def getNameByIndex(self,index):
        return self.scenes[index]['label']

    def __getitem__(self,index):
        return self.scenes[index]
    
    def __len__(self):
        return len(self.scenes)
    
    