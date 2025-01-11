package KrkrDataLoader.config;

import KrkrDataLoader.core.KrkrUtils;
import com.google.gson.JsonObject;
import com.sun.jdi.InvalidTypeException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class AutoPathLoader
{
	public JsonObject data;
	private final Stack<JsonPath> pathStack = new Stack<>();
	private final ConfigList configList = new ConfigList();
	
	public AutoPathLoader(JsonObject data)
	{
		this.data = data;
		try
		{
			JsonPath root = new JsonPath(data);
			pathStack.add(root);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public AutoPathLoader(File file)
	throws IOException, InvalidTypeException
	{ this(KrkrUtils.loadJsonFile(file)); }
	
	public AutoPathLoader(String path)
	throws IOException, InvalidTypeException
	{ this(new File(path)); }
	
	public JsonPath getCurrentPath() { return pathStack.peek(); }
	
	public JsonPath getParent() { return getCurrentPath().parent == null ? null : getCurrentPath().parent; }
	
	public JsonPath getChild(String name) { return getParent().getChild(name); }
	
	public JsonPath getChild(int index) { return getParent().getChild(index); }
	
	public List<JsonPath> listChildren() { return getCurrentPath().listChildren(); }
	
	public List<JsonPath> listPath() { return getCurrentPath().listPath(); }
	
	public JsonPath gotoChild(String name)
	{
		JsonPath child = getCurrentPath().getChild(name);
		if(child != null) { pathStack.add(child); }
		return child;
	}
	
	public JsonPath gotoChild(int index)
	{
		JsonPath child = getCurrentPath().getChild(index);
		if(child != null) { pathStack.add(child); }
		return child;
	}
	
	public JsonPath gotoParent()
	{
		if(getCurrentPath().parent != null)
		{
			pathStack.pop();
			return getCurrentPath();
		}
		else { return null; }
	}
	
	public void setScenesNamePath() { configList.setScenesNamePath(getCurrentPath()); }
	
	public void setSceneNamePath() { configList.setSceneNamePath(getCurrentPath()); }
	
	public void setScenePath() { configList.setScenePath(getCurrentPath()); }
	
	public void setDialoguesPath() { configList.setDialoguesPath(getCurrentPath()); }
	
	public void setSpeakerPath() { configList.setSpeakerPath(getCurrentPath()); }
	
	public void setContentPath() { configList.setContentPath(getCurrentPath()); }
	
	public void setVoicePath() { configList.setVoicePath(getCurrentPath()); }
	
	public void setConfig()
	throws Exception
	{
		Config.loadFromConfigList(configList);
	}
	
	public void checkConfig() throws Exception
	{
		if(configList.getScenesNamePath()==null)throw new Exception("Scenes Name Path is null !");
		if(configList.getSceneNamePath()==null)throw new Exception("Scene Name Path is null !");
		if(configList.getScenePath()==null)throw new Exception("Scene Path is null !");
		if(configList.getDialoguesPath()==null)throw new Exception("Dialogues Path is null !");
		if(configList.getSpeakerPath()==null)throw new Exception("Speaker Path is null !");
		if(configList.getContentPath()==null)throw new Exception("Content Path is null !");
		if(configList.getVoicePath()==null)throw new Exception("Voice Path is null !");
	}
}
