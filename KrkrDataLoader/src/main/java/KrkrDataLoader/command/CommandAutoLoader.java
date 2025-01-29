//package KrkrDataLoader.command;
//
//import KrkrDataLoader.core.KrkrScenes;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.List;
//
//public class CommandAutoLoader
//{
//
//	public static void commandMain(String path)
//	throws Throwable
//	{
//		//Scanner scanner = new Scanner(System.in);
//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//		AutoPathLoader loader = new AutoPathLoader(path);
//		KrkrScenes scenes = null;
//		boolean isLoop = true;
//
//		while(isLoop)
//		{
//			List<Object> pathList = loader.getCurrentPath().listNamePath();
//			for(int index = 0; index < pathList.size() - 1; ++ index) { System.out.print(pathList.get(index) + "/"); }
//			System.out.print(pathList.get(pathList.size() - 1) + ">>");
//
//			String[] commands = reader.readLine().split(" ");
//			//continue;
//
//			switch(commands[0])
//			{
//				case "open":
//					loader.gotoChild(commands[1]);
//					break;
//				case "list":
//					for(JsonPath childPath:loader.getCurrentPath().listChildren())
//					{
//						System.out.println("	"+childPath.name);
//					}
//					break;
//				case "back":
//					loader.gotoParent();
//					break;
//				case "goto":
//					try
//					{
//						loader.gotoChild(Integer.decode(commands[1]));
//					}
//					catch(NumberFormatException ignored)
//					{
//						loader.gotoChild(commands[1]);
//					}
//					break;
//				case "value":
//					System.out.println(loader.getCurrentPath().getData());
//					break;
//				case "set":
//					switch(commands[1])
//					{
//						case "scenes_name":
//							loader.setScenesNamePath();
//							break;
//						case "scene_name":
//							loader.setSceneNamePath();
//							break;
//						case "scene":
//							loader.setScenePath();
//							break;
//						case "dialogues":
//							loader.setDialoguesPath();
//							break;
//						case "speaker":
//							loader.setSpeakerPath();
//							break;
//						case "content":
//							loader.setContentPath();
//							break;
//						case "voice":
//							loader.setVoicePath();
//							break;
//						default:
//							System.out.println("No such field to choose.");
//							System.out.println("Select from scenes_name, scene_name, scene, dialogues, speaker, content, voice.");
//							break;
//					}
//					break;
//				case "check":
//					try{loader.checkConfig();}
//					catch(Exception e){System.out.println(e);}
//					break;
//				case "load":
//					try
//					{
//						loader.checkConfig();
//						loader.setConfig();
//					}
//					catch(Exception e)
//					{
//						System.out.println(e);
//					}
//
//					System.out.println("Loading...");
//
////					Main.testOutputFileContent(path);
//					break;
//				case "save":
//					Config.saveConfigs(Settings.config_path);
//					break;
//				case "exit":
//					isLoop = false;
//					break;
//				default:
//					try{loader.gotoChild(Integer.decode(commands[0]));}
//					catch(Exception ignored)
//					{
//						try{loader.gotoChild(commands[0]);}
//						catch(Exception e){System.out.println("Unknown command!");}
//					}
//					break;
//			}
//		}
//	}
//}
