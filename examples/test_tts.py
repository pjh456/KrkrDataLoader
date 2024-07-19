import pyttsx3

engine = pyttsx3.init()
text = "原神启动！"
engine.say(text)
engine.runAndWait()