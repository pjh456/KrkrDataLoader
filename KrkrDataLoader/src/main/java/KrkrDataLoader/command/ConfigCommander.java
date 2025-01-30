package KrkrDataLoader.command;

import KrkrDataLoader.config.Configs;
import KrkrDataLoader.core.ParentChild;
import KrkrDataLoader.json.JsonFile;
import KrkrDataLoader.json.JsonPath;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class ConfigCommander
{
	private JsonFile file;
	
	private Configs configs;
	
	public ConfigCommander(String path)
			throws Throwable
	{ loadFromJson(path); }
	
	public void loadFromJson(String path)
			throws Throwable
	{
		file = new JsonFile(path);
		configs = new Configs();
	}
	
	public void commandMain()
			throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		boolean isLoop = true;
		
		while(isLoop)
		{
			List<String> pathList = file.getCurrentPath().listAbstractPathName();
			for(int index = 0; index < pathList.size() - 1; ++ index) { System.out.print(pathList.get(index) + "/"); }
			System.out.print(pathList.get(pathList.size() - 1) + ">>");
			
			isLoop = processCommand(reader.readLine());
		}
	}
	
	public boolean processCommand(String commandLine)
	{
		String[] commands = commandLine.split(" ");
		
		switch(commands[0])
		{
			case "cd":
				if(commands.length >= 2)
				{
					if(Objects.equals(commands[1], "..")) { gotoParent(); }
					else { gotoChild(commands[1]); }
				}
				break;
			case "open":
			case "goto":
				gotoChild(commands[1]);
				break;
			case "..":
			case "back":
				gotoParent();
				break;
			case "list":
			case "ls":
				for(ParentChild childPath: file.getCurrentPath().listChildren())
				{
					System.out.println("	" + childPath.getName());
				}
				break;
			case "value":
			case "val":
			case "get":
				System.out.println(file.getCurrentPath().getData());
				break;
			case "set":
				break;
			case "check":
				break;
			case "load":
			case "ld":
				break;
			case "save":
				break;
			case "exit":
				return false;
			case "":
				break;
			default:
				try
				{
					if(file.getCurrentPath().hasChild(Integer.decode(commands[0])))
					{
						gotoChild(Integer.decode(commands[0]));
					}
				}
				catch(NumberFormatException e)
				{
					if(file.getCurrentPath().hasChild(commands[0])) { gotoChild(commands[0]); }
					else { System.out.println("Unknown Command!"); }
				}
				break;
		}
		return true;
	}
	
	public void valueCommand(String[] commands) { }
	
	public JsonPath gotoChild(String name)
	{
		try{ return gotoChild(Integer.decode(name)); }
		catch(NumberFormatException ignored){ return file.gotoChild(name); }
	}
	
	public JsonPath gotoChild(int index) { return file.gotoChild(index); }
	
	public List<String> listChildNames() { return file.getCurrentPath().listChildrenName(); }
	
	public JsonPath gotoParent() { return (JsonPath) file.gotoParent(); }
	
	public JsonElement getValue() { return file.getCurrentPath().getData(); }
	
}