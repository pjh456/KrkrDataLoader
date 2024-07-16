import json

filename = "D:\\games\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.json"
with open(filename,'r',encoding='utf-8') as f:
    datas = json.load(f)
    datas = datas['scenes'][2]['texts']
    #index = 3
    for index in range(0,50):
    #print(datas['scenes'][2]['texts'][index])
    #datas['scenes'][2]['texts'][遍历文本][1]
        #print("角色:",datas['scenes'][2]['texts'][index][0])
        #print("文本:",datas['scenes'][2]['texts'][index][2])
        if datas[index][0] is None:
            print(datas[index][2])
        else:
            print(datas[index][0]
                + ":"
                + datas[index][2])
        if datas[index][3] is not None:
            print(datas[index][3][0]['voice'])