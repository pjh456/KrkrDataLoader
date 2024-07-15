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

class Datas:
    def __init__(self,path):
        self.path = path
        
        datas = json.load(open(path,'r',encoding='utf-8'))
        self.hash = datas['hash']
        self.name = datas['name']
        self.outlines = datas['outlines']
        
        self.datas = []
        for data in datas['scenes']:
            new_scene = Scene(data['label'],self.name,data)
            self.datas.append(new_scene)
        
        self.index = {}
        for index,data in enumerate(self.datas):
            self.index[data.name] = index
    
    def _getIndexByName(self,name):
        return self.index[self.datas[self.index[name]]['label']]
    
    def _getNameByIndex(self,index):
        return self.datas[index]['label']

    def __getitem__(self,index):
        return self.datas[index]
    
    def __len__(self):
        return len(self.datas)
    
    