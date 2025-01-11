package KrkrDataLoader.config;

import KrkrDataLoader.core.KrkrUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Settings
{
	private static boolean is_init = false;
	
	public static String settingPath = "KrkrDataLoader/src/main/resources/settings.json";
	
	public static String audioSuffix = ".ogg";
	
	// Default Data Configs
	public static String config_path;
	public static String speaker;
	public static String content;
	public static String scene_title;
	
	// Default GUI Configs
	
	public static String window_title;
	public static String window_icon;
	
	// Default Fxml Configs
	public static String scene_fxml_path;
	
	public static boolean isInit()
	{
		return is_init;
	}
	
	public static void loadFromJson(String path)
	throws Throwable
	{
		JsonObject obj = KrkrUtils.loadJsonFile(path);
		
		audioSuffix = obj.get("audio_suffix").getAsString();
		
		config_path = obj.get("config_path").getAsString();
		
		speaker = obj.get("speaker").getAsString();
		content = obj.get("content").getAsString();
		scene_title = obj.get("scene_title").getAsString();
		
		window_title = obj.get("window_title").getAsString();
		window_icon = obj.get("window_icon").getAsString();
		
		scene_fxml_path = obj.get("scene_fxml_path").getAsString();
		
		is_init = true;
		
		Config.loadFromJson(config_path);
	}
	
	public static void loadFromJson()
	throws Throwable
	{
		loadFromJson(settingPath);
	}
	
	public static void saveSettings() throws Exception
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		Map<String, String> root = new LinkedHashMap<>();
		root.put("audio_suffix", audioSuffix);
		root.put("config_path", config_path);
		root.put("speaker", speaker);
		root.put("content", content);
		root.put("scene_title", scene_title);
		root.put("window_title", window_title);
		root.put("window_icon", window_icon);
		root.put("scene_fxml_path", scene_fxml_path);
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(settingPath))) {
			writer.write(gson.toJson(root));
		}
		//System.out.println(gson.toJson(root));
	}
}
