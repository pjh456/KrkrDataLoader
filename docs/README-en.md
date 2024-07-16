
# KrkrDataLoader

An unfinished project designed to facilitate the handling of script files extracted from the Krkr engine. 

Future enhancements might include additional features that are already implemented but have an unappealing interface at present.

## Main Features

### **Batch Processing of ``.ks.scn`` Files to ``.ks.json`` Format**
Uses the unpacking tool, [FreeMoteToolkit](../src/FreeMoteToolkit), enhanced with subprocess functionality to automate the handling of files without the need for manual drag-and-drop unpacking one by one.
  
A working version exists, but it was not initially intended for open source, resulting in a hard-to-maintain codebase. Essentially, it simplifies repetitive tasks. I plan to refactor it for better maintainability and release it once it's ready.
  
## **Conversion of ``.ks.json`` Content to Objects**

Allows for efficient handling of the content by converting the ``.ks.json`` file format into object structures.

Other features are currently under active development. I am in the process of creating them but contemplating further simplifications, which might delay the release of a more refined version.

## Installation

Download the entire folder and reference the libraries in `src.tools` following the usage instructions provided. 

These are straightforward utilities that save you from having to read through documentation structures and write unpacking scripts manually.

## Usage

### **Configuration File**

In earlier versions, this was used to locate the path to [FreeMoteToolkit](../src/FreeMoteToolkit). 

However, automatic configuration based on the project structure is now supported, making this feature temporarily obsolete. 

It may become relevant again when implementing audio processing, potentially involving ``ffmpeg``.

### **Script Unpacking**

The `Decompiler` class in [DecompileTool.py](../src/tools/DecompileTool.py) is provided for automated unpacking. You can instantiate and call `Decompiler` like this:
~~~python
from tools.DecompileTool import Decompiler
d = Decompiler()
~~~

``Decompiler`` has an optional path parameter for specifying the path to [FreeMoteToolkit](../src/FreeMoteToolkit);

if omitted, it defaults to the project path (which works fine).

To unpack a single ``.ks.scn`` file:
~~~Python
d.decompile("D:\\senrenbanka\\outPath\\data.xp3\\scn\\001・アーサー王ver1.07.ks.scn")
~~~

To unpack all ``.ks.scn`` files in a directory:
~~~python
d.decompile_all("D:\\senrenbanka\\outPath\\data.xp3\\scn")
~~~

For detailed examples, see [DecompileExample.py](../examples/DecompileExample.py).

### **Script Reading**
``Select``, ``Scene``, and ``Scenes`` classes in [ScnLoader.py](../src/tools/ScnLoader.py) are provided for automated data retrieval.

``Select``: Represents specific options in a choice. It has attributes ``Select.text`` and ``Select.target`` indicating the text of the option and the subsequent plot it leads to.

``Scene``: Segments of the plot are separated to enable switching between choices and scene transitions. 

It includes both plot and choice sections, differentiated by the ``Scene.isselect`` attribute. ``True`` indicates a choice, while ``False`` indicates plot content. 

It provides the ``Scene.target`` property to get the next scene, it will return all the target its choice go to.

It provides the ``Scene.selects`` property for all choices in the scene, represented as a list of Select objects. 

Additionally, there's a ``Scene.title`` property for identifying the title associated with each file.

``Scenes``: Represents an entire ``.ks.json`` file. To manage segments efficiently, I've nested each segment within a class structure. 

You can access individual ``Scene`` instances using ``Scenes[]``, and Scenes supports iteration. 

Use ``Scenes.getIndexByName`` to obtain the index corresponding to a ``Scene.name``, and ``Scene.getNameByIndex`` to retrieve the ``Scene.name`` for a given index.

For detailed examples, see [ScnLoaderexample.py](../examples/ScnLoaderExample.py).

## **Open Source Motivation**
Encountered difficulties while unpacking scripts from games using the Krkr2 engine and training models with audio, wasting considerable time on these mysterious scripts. 

With the spirit of "those who come before plant trees for those who follow", I decided to implement this solution. Should anyone stumble upon it in the future, contributions are welcome.

Ciallo～(∠・ω< )⌒☆

### **Future Implementations**

#### **Long Audio Script Synthesis**
Merge an entire file's script into a long audio file (potentially including background music in future improvements) for listening to like an audio drama, eliminating many awkward moments. 

This is currently being developed, although I'm still deliberating whether to use Text-to-Speech (TTS) for voiceless characters and narrators, which could be resource-intensive.

### **Decryption for New Engine Encryption**
The newer encryption methods used by the engine prevent normal unpacking by ``Krkrextract``, necessitating tools like ``KrkrzExtract``, ``KrkrDump``, and ``KrkrPatch`` for simultaneous reading and unpacking. This complicates file categorization and audio unpacking. 

I'm learning reverse engineering, and should I gain proficiency, I intend to upgrade the unpacking tools.

Coming not soon...

## Tips

In the version ``1.0.1``, I use the language model ``qwen2.5`` to update my unformatted ``README`` document.

If you find that there's something wrong in the latest ``README`` document, you can read the ``1.0.0`` version document.

###### It's all not my fault.All the mistakes are made by Qwen2.5 XD.