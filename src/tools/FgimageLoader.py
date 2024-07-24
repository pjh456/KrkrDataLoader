import os
import csv
import json
import chardet

def detect_encoding(file_path):
    with open(file_path, 'rb') as f:
        result = chardet.detect(f.read())
    return result['encoding']

class Fgimage:
    def __init__(self,
                 rule_path=''):
        if not rule_path.endswith('.txt'):
            raise TypeError('Except a .txt file.')
        
        try:
            with open(rule_path,'r',encoding=detect_encoding(rule_path)) as f:
                pass
        except Exception as e:
            print(e)
            
        