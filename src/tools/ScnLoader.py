import json
import os

class Scene:
    def __init__(self,name,location,data = dict()):
        self.name = name
        self.location = location
        self.data = data
        self.selection = 'selects' in data
        
    def goto(self):
        return None if self.selection else [item['target'] for item in self.data['nexts']]
    
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
    
    def _getIndexByName(self,name):
        return self.index[self.scenes[self.index[name]]['label']]
    
    def _getNameByIndex(self,index):
        return self.scenes[index]['label']

    def __getitem__(self,index):
        return self.scenes[index]
    
    def __len__(self):
        return len(self.scenes)
    
    