package KrkrDataLoader.core;

import KrkrDataLoader.config.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class KrkrScenes
		extends KrkrData
{
	@Override
	public void initialize()
	throws Throwable
	{
		for(JsonElement object: Config.SceneConfig.getValueAsJsonArray(data))
		{
			setChild(new KrkrScene(object));
		}
		
		this.data = null;
		is_init = true;
	}
	
	public KrkrScenes(JsonElement data, boolean init_now)
	throws Throwable
	{
		super(Config.ScenesNameConfig.getValueAsJsonPrimitive(data).getAsString());
		this.data = data;
		if(init_now) initialize();
	}
	
	public KrkrScenes(String path, boolean init_now)
	throws Throwable
	{ this(KrkrUtils.loadJsonFile(path), init_now); }
	
	public KrkrScenes(File file, boolean init_now)
	throws Throwable
	{ this(KrkrUtils.loadJsonFile(file), init_now); }
	
	public KrkrScenes(MultipartFile file, boolean init_now)
	throws Throwable
	{ this(KrkrUtils.loadJsonFile(file), init_now); }
	
	public KrkrScenes(String path)
	throws Throwable
	{ this(path, true); }
	
	public KrkrScenes(File file)
	throws Throwable
	{ this(file, true); }
	
	public KrkrScenes(MultipartFile file)
	throws Throwable
	{ this(file, true); }
}

