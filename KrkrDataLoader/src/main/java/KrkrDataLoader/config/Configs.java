package KrkrDataLoader.config;

import KrkrDataLoader.core.KrkrUtils;
import KrkrDataLoader.setting.GlobalSetting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Configs
{
	private final Map<String,SingleConfig> configMap = new HashMap<>();
	
	private final static List<String> necessaryConfigs = Arrays.asList("scenes_name",
																	   "scene_label",
																	   "scene",
																	   "dialogues",
																	   "speaker",
																	   "content",
																	   "voice"
	);
	
	public Configs() { }
	
	public Configs(JsonObject data) { loadFromJson(data); }
	
	public Configs(String path)
			throws Throwable
	{ loadFromJson(path); }
	
	public Configs(File file)
			throws Throwable
	{ loadFromJson(file); }
	
	public Configs(MultipartFile file)
			throws Throwable
	{ loadFromJson(file); }
	
	public void loadFromJson(JsonObject data)
	{
		for(Map.Entry<String,JsonElement> json_config: data.entrySet())
		{
			List<Object> fields = new ArrayList<>();
			// TODO: 目前这里只需要每个 JsonArray 的首个匹配项，以后将会改为多模式并行
			// 目前默认是直接单层 JsonArray，即只有一种模式，如果想要多模式还得再嵌套一层遍历
			for(JsonElement field: json_config.getValue().getAsJsonArray())
			{
				if(field.getAsJsonPrimitive().isString()) { fields.add(field.getAsString()); }
				else if(field.getAsJsonPrimitive().isNumber()) { fields.add((Integer) field.getAsInt()); }
			}
			
			setConfig(new SingleConfig(json_config.getKey(), fields));
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
		Path filePath = Paths.get(path);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String,List<List<Object>>> root = new LinkedHashMap<>();
		
		for(String configName: necessaryConfigs)
		{
			if(getConfig(configName) == null) { throw new NullPointerException("Cannot find config: " + configName); }
			root.put(configName, getConfig(configName).getFieldsList());
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()));
		writer.write(gson.toJson(root));
		
		if(GlobalSetting.hasCurrentSetting())
		{
			GlobalSetting.getCurrentSetting().setSetting("config_path", filePath.toString());
			GlobalSetting.saveCurrentSetting(filePath.toString());
		}
		
		writer.close();
	}
	
	public static String checkConfigs(Configs configs)
	{
		for(String configName: necessaryConfigs)
		{
			if(configs.getConfig(configName) == null) { return configName; }
		}
		return null;
	}
	
	public SingleConfig getConfig(String name) { return configMap.get(name); }
	
	public void setConfig(String name, SingleConfig config) { configMap.put(name, config); }
	
	public void setConfig(SingleConfig config) { setConfig(config.getName(), config); }
	
	public void removeConfig(String name) { configMap.remove(name); }
	
	public void clear() { configMap.clear(); }
	
	@Override
	public String toString()
	{
		return configMap.entrySet().stream().map(entry -> "\t" + entry.getKey() + ": " + entry.getValue().toString()).collect(
				Collectors.joining(",\n", "{\n", "\n}"));
		
	}
}
