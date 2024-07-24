import os
import csv
import json
import pandas
import chardet
import numpy
from PIL import Image

def detect_encoding(file_path):
    with open(file_path, 'rb') as f:
        result = chardet.detect(f.read())
    return result['encoding']

class LayerBase:
    def __init__(self,row):
        if not isinstance(row,pandas.Series):
            raise TypeError('Must be a Series object!')
        
        self.layer_type = int(row['layer_type'])
        self.name = row['name']
        self.left = int(row['left'])
        self.top = int(row['top'])
        self.width = int(row['width'])
        self.height = int(row['height'])
        self.type = int(row['type'])
        self.opacity = row['opacity']
        self.visible = int(row['visible'])
        self.index = int(row['layer_id'])

class Layer(LayerBase):
    def __init__(self,path,row):
        super().__init__(row)
        self.path = path
        
        try:
            self.group_layer_id = int(row['group_layer_id'])
        except ValueError:
            self.group_layer_id = 0
        
            
class Group(LayerBase):
    def __init__(self,row):
        super().__init__(row)
        self.layers = []

class Fgimage:
    def __init__(self,
                 rule_path='',
                 suffix='.png'):
        if not rule_path.endswith('.txt'):
            raise TypeError('Except a .txt file.')
        
        
        self.name = rule_path.split('\\')[-1].split('.')[0]
        self.suffix = suffix
        
        self.data = None
        try:
            #print(detect_encoding(rule_path))
            with open(rule_path,'r',encoding=detect_encoding(rule_path)) as f:
                lines = f.readlines()
                # 移除空格和制表符，确保字段正确对齐
                cleaned_lines = [line.replace('\t', ',').replace(' ', '').replace('#','') for line in lines]
                # 将第一行转换为CSV标题
                header = cleaned_lines[0].strip().split(',')
                csv_lines = [header] + [line.strip().split(',') for line in cleaned_lines[1:] if line.strip()]
            
                # 写入CSV文件
                rule_path = rule_path.replace('.txt','.csv')
                with open(rule_path, 'w+', newline='', encoding='utf-8') as csvfile:
                    writer = csv.writer(csvfile)
                    writer.writerows(csv_lines)
            
            self.data = pandas.read_csv(rule_path)
                
        except Exception as e:
            print(e)
            
        self.layers = []
        self.groups = []
        self.groups.append(Group(pandas.Series(
            [2,'Defualt',0,0,0,0,0,0,0,0,0],
            ['layer_type','name','left','top','width','height','type','opacity','visible','layer_id','group_layer_id'])))
        
        
        self.layer_index = {}
        self.group_index = {0:0}
        for index,row in self.data.iterrows():
            if index == 0:
                continue
            if row['layer_type'] == 2:
                new_group = Group(row)
                self.group_index[row['layer_id']] = len(self.groups)
                self.groups.append(new_group)
            
        for index,row in self.data.iterrows():
            if index == 0:
                continue
            if row['layer_type'] == 0:
                layer_path = os.path.join(os.path.dirname(rule_path),f"{self.name}_{int(row['layer_id'])}"+self.suffix)
                new_layer = Layer(layer_path,row)
                self.layer_index[row['layer_id']] = len(self.layers)
                self.layers.append(new_layer)
                self.groups[self.group_index[new_layer.group_layer_id]].layers.append(new_layer)
    
    def getNameByIndex(self,index):
        return self.layers[self.layer_index[index]].name
    
    def get_image(self,layers=[],show=False,background=(0,0,0,0)):
        def adjust_opacity(image, opacity):
            alpha = image.split()[-1]
            alpha = Image.fromarray((numpy.array(alpha) * opacity / 255).astype(numpy.uint8))
            image.putalpha(alpha)
            return image
        
        # 计算包围盒
        layers = [self.layers[self.layer_index[index]] for index in layers]
        left = min(layer.left for layer in layers)
        top = min(layer.top for layer in layers)
        right = max(layer.left + layer.width for layer in layers)
        bottom = max(layer.top + layer.height for layer in layers)
        width = right - left
        height = bottom - top
         # 创建新图像
        result_image = Image.new("RGBA", (width, height), background)
        # 绘制每个图层
        for layer in layers:
            img = Image.open(layer.path).convert('RGBA')  # 假设image_path是Layer类的一个属性
            img = img.resize((layer.width, layer.height), resample=Image.LANCZOS)

            # 调整透明度
            img = adjust_opacity(img, layer.opacity)

            # 创建一个与最终图像相同大小的透明图像
            positioned_img = Image.new("RGBA", result_image.size, (0, 0, 0, 0))

            # 将图层放置在适当的位置
            position = (layer.left - left, layer.top - top)
            positioned_img.paste(img, position)

            # 使用alpha_composite将图层复合到最终图像上
            result_image = Image.alpha_composite(result_image, positioned_img)
            
        if show:
            result_image.show()
        return result_image
    
            
    def __getitem__(self,index):
        return self.layers[self.layer_index[index]]
            
class FgimageFolder:
    def __init__(self,path='',suffix='.png'):
        if not os.path.isdir(path):
            raise Exception(f'{path} must be a folder!')
        
        self.path = path
        filedirs = [filename for filename in os.listdir(path) if filename.endswith('.txt')]
        
        self.fgimages = []
        self.fgimage_index = {}
        for index,filename in enumerate(filedirs):
            filepath = os.path.join(path, filename)
            new_image = Fgimage(filepath,suffix)
            self.fgimages.append(new_image)
            self.fgimage_index[new_image.name] = index
    
    def __getitem__(self,index):
        return self.fgimages[index]
        