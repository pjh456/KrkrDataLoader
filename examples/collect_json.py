import sys
import os
import subprocess
import json
import re

# 指定文件夹路径
folder_path = os.getcwd()
replacement = [r'%[^;]*;',
               r'\[[^\]]*\]',
               r'\\n']
def filefilter(text):
    for line in replacement:
        text = re.sub(line,'',text)
    return text

# 遍历文件夹
for root, dirs, files in os.walk(folder_path):
    for file_name in files:
        if file_name.endswith('.ks.json'):
            file_path = os.path.join(root, file_name)
            print("open:"+file_path)
            #output_filename = '../voice_list/{}/{}.list'
            output_filename = '../scn_txt/{}.txt'
            #output_content = '/root/autodl-tmp/{}/{}.ogg|JA|{}'
            #open_files = {}  # 存储已打开的文件句柄
            #try:
            with open(output_filename.format(file_name[0:-8]),'w+',encoding='utf-8') as output_file:
                with open(file_path,'r',encoding='utf-8') as f:
                    datas_list = json.load(f)
                    datas_list = datas_list['scenes']
                    for datas in datas_list:
                        try:
                            datas = datas['texts']
                        except KeyError:
                            continue
                        for data in datas:
                            #if data[3] is not None:
                                #voice_file = data[3][0]['voice']
                                #directory = voice_file[0:3]
                                #print(voice_file + data[2])
                                # 如果文件句柄未打开，则打开文件并存储句柄
                                #if directory not in open_files:
                                    #os.makedirs(os.path.dirname(output_filename.format(voice_file[0:3],voice_file[0:3])), exist_ok=True)
                                    #open_files[directory] = open(output_filename.format(voice_file[0:3],voice_file[0:3]), 'a', encoding='utf-8')

                            # 写入内容到相应文件
                            name = data[0]
                            content = data[2]
                            content = filefilter(content)
                            print(file_name[0:-8])
                            if data[0] is not None:
                                print(f'【{name}】'+':'+content)
                                output_file.write(f'【{name}】'+':'+content+'\n')
                            else:
                                print(content)
                                output_file.write(content+'\n')
                            #content = data[2].replace('「','')
                            #content = content.replace('『','')
                            #content = content.replace('」','')
                            #content = content.replace('』','')
                            #open_files[directory].write(output_content.format(voice_file[0:3],voice_file,content)+'\n')
            #finally:
                # 确保所有打开的文件句柄都关闭
                #for file in open_files.values():
                    #file.close()