import sys
import os
sys.path.append("..")
from src.tools.FgimageLoader import FgimageFolder,Fgimage

f = Fgimage("D:\\games\\senrenbanka\\outPath\\fgimage720.xp3\\ムラサメa.txt")

image = f.get_image([1950,1399,1273],True,background=(255,255,255,255))
image.save(os.path.join(os.path.dirname(os.path.abspath(__file__)),'exampleImage.png'))

folder = FgimageFolder('D:\\games\\senrenbanka\\outPath\\fgimage720.xp3')
for file in folder:
    print(file.name)
    for group in file.groups:
        print(f'    {group.name}')
    