import subprocess
import os
import sys

class Decompiler:
    def __init__(self,path=None):
        FreeMoteToolkitPath = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),'FreeMoteToolkit')
        #print(FreeMoteToolkitPath)
        if path == None:
            path = os.path.join(FreeMoteToolkitPath,'PsbDecompile.exe')
        self.decompiler_path = path
        
    def decompile(self,file_path,stream_output=False):
        if not os.path.isfile(self.decompiler_path):
            raise FileNotFoundError(f"Can't find {self.decompiler_path}")
        if not file_path.endswith('.ks.scn'):
            raise TypeError('file must ends with ".ks.scn" ')
        try:
            if stream_output:
                process  = subprocess.Popen([self.decompiler_path, file_path], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
                    # 逐行读取输出
                for line in iter(process.stdout.readline, ''):
                    yield line
                
                # 等待进程结束
                process.wait()
            else:
                subprocess.run([self.decompiler_path, file_path],stdout=sys.stdout)
        except Exception as e:
            print(e)
        
    def decompile_all(self,folder_path=os.getcwd(),stream_output=False):
        filedirs = [filename for filename in os.listdir(folder_path) if filename.endswith('.ks.scn')]
        for file_name in filedirs:
            file_path = os.path.join(folder_path, file_name)
            if stream_output:
                    yield from self.decompile(file_path,stream_output)
            else:
                self.decompile(file_path,stream_output)