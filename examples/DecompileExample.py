import sys
sys.path.append("..")
from src.tools.DecompileTool import Decompiler
import os
import subprocess

d = Decompiler()

#d.decompile_all("D:\\games\\cafeStella\\KrkrExtract_Output\\scn")
d.decompile("D:\\games\\cafeStella\\KrkrExtract_Output\\scn\\001.始まりver1.07.ks.scn")
#d.decompile("D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.scn")