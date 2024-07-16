import json
import subprocess
import os
import sys


class Decompiler:
    def __init__(self,path=None):
        FreeMoteToolkitPath = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'FreeMoteToolkit')
        print(FreeMoteToolkitPath)
        if path == None:
            path = os.path.join(FreeMoteToolkitPath,'PsbDecompile.exe')
        self.decompiler_path = path
        
    def decompile(self,file_path):
        if not os.path.isfile(self.decompiler_path):
            raise FileNotFoundError(f"Can't find {self.decompiler_path}")
        if not file_path.endswith('.ks.scn'):
            raise TypeError('file must ends with ".ks.scn" ')
        try:
            subprocess.run([self.decompiler_path, file_path])
        except Exception as e:
            print(e)
        
    def decompile_all(self,folder_path=os.getcwd()):
        for root, dirs, files in os.walk(folder_path):
            for file_name in files:
                if file_name.endswith('.ks.scn'):
                    file_path = os.path.join(root, file_name)
                    self.decompile(file_path)