package KrkrDataLoader.core;

import KrkrDataLoader.config.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class KrkrScenes
		extends KrkrData
{
	public KrkrScenes(JsonElement data) throws Throwable
	{
		//super(Config.getSingleConfig("scenes_name").getValueAsJsonPrimitive(data).getAsString());
		super(Config.ScenesNameConfig.getValueAsJsonPrimitive(data).getAsString());
		
		//JsonArray scene_array = Config.getSingleConfig("scene").getValueAsJsonArray(data);
		JsonArray scene_array = Config.SceneConfig.getValueAsJsonArray(data);
		
		for(JsonElement object: scene_array)
		{
			setChild(new KrkrScene(object));
		}
	}
	
	public KrkrScenes(String path) throws Throwable
	{
		this(KrkrUtils.loadJsonFile(path));
	}
}
