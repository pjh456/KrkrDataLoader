import subprocess
import os
import sys
from .DataLoader import Config

class Decompiler:
    def __init__(self,path=None):
        FreeMoteToolkitPath = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'FreeMoteToolkit')
        #print(FreeMoteToolkitPath)
        if path == None:
            path = os.path.join(FreeMoteToolkitPath,'PsbDecompile.exe')
        self.decompiler_path = path
        
    def decompile(self,file_path):
        if not os.path.isfile(self.decompiler_path):
            if Config.debug:
                raise FileNotFoundError(f"Can't find {self.decompiler_path}")
            else:
                print(f"Can't find {self.decompiler_path}")
        if not file_path.endswith('.ks.scn'):
            if Config.debug:
                raise TypeError('file must ends with ".ks.scn" ')
            else:
                print('file must ends with ".ks.scn" ')
        try:
            subprocess.run([self.decompiler_path, file_path],stdout=sys.stdout)
        except Exception as e:
            print(e)
        
    def decompile_all(self,folder_path=os.getcwd()):
        filedirs = [filename for filename in os.listdir(folder_path) if filename.endswith('.ks.scn')]
        for file_name in filedirs:
            file_path = os.path.join(folder_path, file_name)
            self.decompile(file_path)
    
    def decompile_GUI(self,file_path):
        if not os.path.isfile(self.decompiler_path):
            if Config.debug:
                raise FileNotFoundError(f"Can't find {self.decompiler_path}")
            else:
                print(f"Can't find {self.decompiler_path}")
        if not file_path.endswith('.ks.scn'):
            if Config.debug:
                raise TypeError('file must ends with ".ks.scn" ')
            else:
                print('file must ends with ".ks.scn" ')
                
        try:
            process  = subprocess.Popen([self.decompiler_path, file_path], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
                # 逐行读取输出
            for line in iter(process.stdout.readline, ''):
                yield line
            # 等待进程结束
            process.wait()
        except Exception as e:
            print(e)
    
    def decompile_all_GUI(self,folder_path=os.getcwd()):
        filedirs = [filename for filename in os.listdir(folder_path) if filename.endswith('.ks.scn')]
        for file_name in filedirs:
            file_path = os.path.join(folder_path, file_name)
            yield from self.decompile_GUI(file_path)
        