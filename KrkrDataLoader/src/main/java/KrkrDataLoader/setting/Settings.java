package KrkrDataLoader.setting;

import KrkrDataLoader.core.KrkrUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Settings
{
	private String name;
	
	private final Map<String,SingleSetting> currentSettings = new LinkedHashMap<>();
	
	public Settings(String name, JsonElement data)
			throws Throwable
	{
		setName(name);
		loadFromJson(data);
	}
	
	public Settings(String name, String path)
			throws Throwable
	{
		setName(name);
		loadFromJson(path);
	}
	
	public Settings(String name, File file)
			throws Throwable
	{
		setName(name);
		loadFromJson(file);
	}
	
	public Settings(String name, MultipartFile file)
			throws Throwable
	{
		setName(name);
		loadFromJson(file);
	}
	
	public Settings(JsonElement data)
			throws Throwable{this("defaultName", data);}
	
	public Settings(String path)
			throws Throwable{this("defaultName", path);}
	
	public Settings(File file)
			throws Throwable{this("defaultName", file);}
	
	public Settings(MultipartFile file)
			throws Throwable{this("defaultName", file);}
	
	public void loadFromJson(JsonElement data)
			throws Throwable
	{
		for(Map.Entry<String,JsonElement> json_setting: data.getAsJsonObject().entrySet())
		{
			setSetting(new SingleSetting(json_setting.getKey(), json_setting.getValue().getAsString()));
		}
	}
	
	public void loadFromJson(String path)
			throws Throwable
	{ loadFromJson(KrkrUtils.loadJsonFile(path)); }
	
	public void loadFromJson(File file)
			throws Throwable
	{ loadFromJson(KrkrUtils.loadJsonFile(file)); }
	
	public void loadFromJson(MultipartFile file)
			throws Throwable
	{ loadFromJson(KrkrUtils.loadJsonFile(file)); }
	
	public void save(String path)
			throws NullPointerException, IOException
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String,Object> root = new LinkedHashMap<>();
		
		for(Map.Entry<String,SingleSetting> setting: currentSettings.entrySet())
		{
			root.put(setting.getKey(), setting.getValue().getState());
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(gson.toJson(root));
		
		writer.close();
	}
	
	public SingleSetting getSetting(String name) { return currentSettings.get(name); }
	
	public void setName(String name){ this.name = name;}
	
	public String getName(){ return name; }
	
	public void setSetting(String name, SingleSetting setting) { currentSettings.put(name, setting); }
	
	public void setSetting(String name, Object state) { setSetting(name, new SingleSetting(name, state)); }
	
	public void setSetting(SingleSetting setting) { setSetting(setting.getName(), setting); }
}
