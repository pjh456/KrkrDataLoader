'''
import os
import json
import sys
sys.path.append("..")
from src.tools.DataLoader import AutoDataConfiger

configer = AutoDataConfiger()
configer.auto_config(r"D:\games\senrenbanka\outPath\data.xp3\scn\001・アーサー王ver1.07.ks.json")
'''

def func(a,b):
    # 确定前缀
    prefix = b.split(a)[0]
    # 确定后缀
    suffix = b[len(prefix) + len(a):]
    print(prefix,a,suffix)

a = 'ababa'
b = 'aa'
func(a,b)
