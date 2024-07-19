import pyttsx3

engine = pyttsx3.init()
print(engine.getProperty('rate'))
engine.setProperty('rate',150)
voices = engine.getProperty('voices')

for voice in voices:
    # 输出声音的ID
    print("Voice ID:", voice.id)
    
    # 输出声音的名字
    print("Name:", voice.name)
    
    # 输出支持的语言
    print("Languages:", voice.languages)
    
    # 输出性别
    print("Gender:", voice.gender)
    
    # 输出年龄
    print("Age:", voice.age)
    
    # 打印分割线，便于区分不同声音的信息
    print("-" * 20)

text = "「啊啊……是叫[し]志[な]那[つ]都庄来的吧？」"
engine.say(text)
engine.runAndWait()