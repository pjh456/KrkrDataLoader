package KrkrDataLoader.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import KrkrDataLoader.core.KrkrUtils;

public class Config
{
	private static boolean is_init = false;
	
	public static SingleConfig ScenesNameConfig = null;
	public static SingleConfig SceneNameConfig = null;
	public static SingleConfig SceneConfig = null;
	public static SingleConfig DialoguesConfig = null;
	public static SingleConfig SpeakerConfig = null;
	public static SingleConfig ContentConfig = null;
	public static SingleConfig VoiceConfig = null;
	
	private static Map<String,SingleConfig> single_config_map = new HashMap<>();
	
	public static boolean isInit() { return is_init; }
	
	public static SingleConfig getSingleConfig(String name) { return single_config_map.get(name); }
	
	public static void setSingleConfig(SingleConfig config) { single_config_map.put(config.name, config); }
	
	public static void loadFromJson(JsonObject data)
	throws Exception
	{
		single_config_map = new HashMap<>();
		
		for(Map.Entry<String,JsonElement> json_config: data.entrySet())
		{
			List<Object> fields = new ArrayList<>();
			//TODO 目前这里只用第一个匹配就够了，等以后多模式匹配的时候再把 get(0) 改了
			for(JsonElement field: json_config.getValue().getAsJsonArray().get(0).getAsJsonArray())
			{
				if(field.getAsJsonPrimitive().isString()) { fields.add(field.getAsString()); }
				else if(field.getAsJsonPrimitive().isNumber()) { fields.add((Integer) field.getAsInt()); }
			}
			//setSingleConfig(new SingleConfig(json_config.getKey(), fields));
			SingleConfig singleConfig = new SingleConfig(json_config.getKey(), fields);
			
			switch(json_config.getKey())
			{
				case "scenes_name":
					ScenesNameConfig = singleConfig;
					break;
				case "scene_label":
					SceneNameConfig = singleConfig;
					break;
				case "scene":
					SceneConfig = singleConfig;
					break;
				case "dialogues":
					DialoguesConfig = singleConfig;
					break;
				case "speaker":
					SpeakerConfig = singleConfig;
					break;
				case "content":
					ContentConfig = singleConfig;
					break;
				case "voice":
					VoiceConfig = singleConfig;
					break;
			}
			
			setSingleConfig(singleConfig);
		}
		
		is_init = true;
	}
	
	public static void loadFromJson(String path)
	throws Throwable
	{
		loadFromJson(KrkrUtils.loadJsonFile(path));
	}
	
	public static void loadFromJson(File file)
	throws Throwable
	{
		loadFromJson(KrkrUtils.loadJsonFile(file));
	}
	
	public static void loadFromConfigList(ConfigList configList)
	throws NullPointerException, Exception
	{
		JsonPath ScenesNameConfigAbstractPath = Objects.requireNonNull(configList.getScenesNamePath());
		JsonPath SceneNameConfigAbstractPath = Objects.requireNonNull(configList.getSceneNamePath());
		JsonPath SceneConfigAbstractPath = Objects.requireNonNull(configList.getScenePath());
		JsonPath DialoguesConfigAbstractPath = Objects.requireNonNull(configList.getDialoguesPath());
		JsonPath SpeakerConfigAbstractPath = Objects.requireNonNull(configList.getSpeakerPath());
		JsonPath ContentConfigAbstractPath = Objects.requireNonNull(configList.getContentPath());
		JsonPath VoiceConfigAbstractPath = Objects.requireNonNull(configList.getVoicePath());
		
		List<Object> ScenesNameConfigPath = ScenesNameConfigAbstractPath.listObjectPath();
		List<Object> SceneNameConfigPath = KrkrUtils.removeSamePath_object(SceneConfigAbstractPath, SceneNameConfigAbstractPath);
		List<Object> SceneConfigPath = SceneConfigAbstractPath.listObjectPath();
		List<Object> DialoguesConfigPath = KrkrUtils.removeSamePath_object(SceneConfigAbstractPath,DialoguesConfigAbstractPath);
		List<Object> SpeakerConfigPath = KrkrUtils.removeSamePath_object(DialoguesConfigAbstractPath, SpeakerConfigAbstractPath);
		List<Object> ContentConfigPath = KrkrUtils.removeSamePath_object(DialoguesConfigAbstractPath, ContentConfigAbstractPath);
		List<Object> VoiceConfigPath = KrkrUtils.removeSamePath_object(DialoguesConfigAbstractPath, VoiceConfigAbstractPath);
		
//		System.out.println(SceneConfigAbstractPath.listNamePath());
//		System.out.println(DialoguesConfigAbstractPath.listNamePath());
		
		
		ScenesNameConfig = new SingleConfig("scenes_name", ScenesNameConfigPath.subList(1,ScenesNameConfigPath.size()));
		SceneNameConfig = new SingleConfig("scene_label", SceneNameConfigPath.subList(1,SceneNameConfigPath.size()));
		SceneConfig = new SingleConfig("scene", SceneConfigPath.subList(1,SceneConfigPath.size()));
		DialoguesConfig = new SingleConfig("dialogues", DialoguesConfigPath.subList(1,DialoguesConfigPath.size()));
		SpeakerConfig = new SingleConfig("speaker", SpeakerConfigPath.subList(1,SpeakerConfigPath.size()));
		ContentConfig = new SingleConfig("content", ContentConfigPath.subList(1,ContentConfigPath.size()));
		VoiceConfig = new SingleConfig("voice", VoiceConfigPath.subList(1,VoiceConfigPath.size()));
		
		is_init = true;
	}
	
	public static void saveConfigs(String path)
	throws IOException
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, List<List<Object>>> root = new LinkedHashMap<>();
		root.put("scenes_name", ScenesNameConfig.getFieldsList());
		root.put("scene_label", SceneNameConfig.getFieldsList());
		root.put("scene", SceneConfig.getFieldsList());
		root.put("dialogues", DialoguesConfig.getFieldsList());
		root.put("speaker", SpeakerConfig.getFieldsList());
		root.put("content", ContentConfig.getFieldsList());
		root.put("voice", VoiceConfig.getFieldsList());
		System.out.println(gson.toJson(root));
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
			writer.write(gson.toJson(root));
			Settings.config_path = path;
			Settings.saveSettings();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		//gson.toJson(root, new FileWriter(defaultOutputPath));
	}
	
	
}
