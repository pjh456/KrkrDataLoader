package KrkrDataLoader.command;

import KrkrDataLoader.config.Configs;
import KrkrDataLoader.core.ParentChild;
import KrkrDataLoader.json.JsonFile;
import KrkrDataLoader.json.JsonPath;
import com.google.gson.JsonElement;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
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
	
	public void commandMain(InputStream in, OutputStream out)
			throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		PrintWriter writer = new PrintWriter(out);
		
		boolean isLoop = true;
		
		while(isLoop)
		{
			List<String> pathList = file.getCurrentPath().listAbsolutePathName();
			for(int index = 0; index < pathList.size() - 1; ++ index) { writer.print(pathList.get(index) + "/"); }
			writer.print(pathList.get(pathList.size() - 1) + ">>");
			writer.flush();
			
			isLoop = parseCommand(reader.readLine(), writer);
		}
		
		reader.close();
		writer.close();
	}
	
	public boolean parseCommand(String commandLine, PrintWriter writer)
	{
		List<String> responses = parseCommandWithResponse(commandLine);
		if(responses != null)
		{
			for(String response: responses) { writer.println(response); }
			writer.flush();
			return ! "exit".equals(responses.get(0));
		}
		else { return true; }
	}
	
	public List<String> parseCommandWithResponse(String commandLine)
	{
		String[] commands = commandLine.split(" ");
		
		switch(commands[0])
		{
			case "cd":
			case "open":
			case "goto":
				if(commands.length == 2)
				{
					if(Objects.equals(commands[1], "..")) { gotoParent(); }
					else { gotoChild(commands[1]); }
				}
				else if(commands.length > 2)
				{
					JsonPath newPath = file.getCurrentPath();
					boolean pathValid = true;
					for(int index = 1; index < commands.length; ++ index)
					{
						Object command = commands[index];
						try{ command = Integer.decode((String) command); }
						catch(NumberFormatException ignored){ }
						if(command instanceof Integer)
						{
							if(newPath.hasChild((Integer) command))
							{
								newPath = (JsonPath) newPath.getChild((Integer) command);
							}
							else
							{
								pathValid = false;
								break;
							}
						}
						else
						{
							if(newPath.hasChild((String) command))
							{
								newPath = (JsonPath) newPath.getChild((String) command);
							}
							else
							{
								pathValid = false;
								break;
							}
						}
					}
					if(pathValid) { file.setCurrentPath(newPath); }
				}
				break;
			case "..":
			case "back":
				gotoParent();
				break;
			case "list":
			case "ls":
				List<String> stringList = new ArrayList<>();
				for(ParentChild childPath: file.getCurrentPath().listChildren())
				{
					stringList.add("	" + childPath.getName());
				}
				return stringList;
			case "value":
			case "val":
			case "get":
				return Collections.singletonList(file.getCurrentPath().getData().toString());
			case "set":
			case "st":
				if(commands.length >= 2) { file.setCurrentPathAsConfig(commands[1]); }
				else { return Collections.singletonList("Config needs a name!"); }
				break;
			case "config":
			case "cf":
				return Collections.singletonList(file.getConfigs().toString());
			case "remove":
			case "rm":
				if(commands.length >= 2) { file.getConfigs().removeConfig(commands[1]); }
				else { return Collections.singletonList("Config needs a name!"); }
			case "check":
			case "ck":
				return Collections.singletonList(Objects.requireNonNullElse(
						Configs.checkConfigs(file.getConfigs()),
						"Config is ready."
				));
			case "load":
			case "ld":
				break;
			case "save":
				try{ file.getConfigs().save(commands[1]); }
				catch(IOException e){ return Collections.singletonList(e.toString()); }
				break;
			case "exit":
			case "quit":
				return Collections.singletonList("exit");
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
					else { return Collections.singletonList("Unknown Command!"); }
				}
				break;
		}
		return null;
	}
	
	public void gotoChild(String name)
	{
		try{ gotoChild(Integer.decode(name)); }
		catch(NumberFormatException ignored){ file.gotoChild(name); }
	}
	
	public void gotoChild(int index) { file.gotoChild(index); }
	
	public List<String> listChildNames() { return file.getCurrentPath().listChildrenName(); }
	
	public void gotoParent() { file.gotoParent(); }
	
	public JsonElement getValue() { return file.getCurrentPath().getData(); }
	
}