import sys
sys.path.append("..")
from src.tools.FgimageLoader import detect_encoding

path = "D:\\games\\senrenbanka\\outPath\\fgimage720.xp3\\face白狗a.txt"
print(detect_encoding(path))
with open(path,'r',encoding=detect_encoding(path)) as f:
    print(f.readlines())