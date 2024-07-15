# KrkrDataLoader
A unfinished tool for easier disposing KrkrExtract scn datas

## Main functions:

### Decomplie ".ks.scn" files into ".ks.json" files in batches
Based on open source FreeMoteToolkit, We provide a way to decompile files easier.
###### Well...Actually, a unformatted version has been already finished. But we want to provide a better version so that it won't be published right now. Maybe Comming soon?

### Disposing ".ks.json" files and getting all informations of them
Two classes called Scene & Datas are provided.

#### Scene
refers to a concrete scene and some dialogues are in it. Its next scene or selection will be provided too.

Scene.goto() are provided to find its next scene.

#### Datas
refers to a whole ".ks.json" file. It involves many Scenes. You can use Datas[] to get every scene.(or selection?)

It includes the name of the file, hash value and so on.

###### Right now it's half done. Will comming soon.

## Why I build the project?

Ciallo～(∠・ω< )⌒☆
