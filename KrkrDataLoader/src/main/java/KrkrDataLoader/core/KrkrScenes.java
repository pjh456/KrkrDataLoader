package KrkrDataLoader.core;

import KrkrDataLoader.config.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class KrkrScenes
		extends KrkrData
{
	/**
	 * Initialize KrkrScenes. ( a whole scene file )
	 * @throws Throwable If failed to initialize.
	 */
	@Override
	public void initialize()
			throws Throwable
	{
		List<Thread> threadList = new ArrayList<>();
		for(JsonElement object: Config.SceneConfig.getValueAsJsonArray(data))
		{
			KrkrScene newChild = new KrkrScene(object, false);
			setChild(newChild);
			
			Thread thread = new Thread(() ->
			{
				try{ newChild.initialize(); }
				catch(Throwable ignored){ }
			});
			threadList.add(thread);
			thread.start();
		}
		
		for(Thread thread: threadList) { thread.join(); }
		
		this.data = null;
		is_init = true;
	}
	
	/**
	 * Create new KrkrScenes.
	 * @param data Unformatted data. ( json type )
	 * @param init_now Initialize immediately.
	 * @throws Throwable If failed to create.
	 */
	public KrkrScenes(JsonElement data, boolean init_now)
			throws Throwable
	{
		super(Config.ScenesNameConfig.getValueAsJsonPrimitive(data).getAsString());
		this.data = data;
		if(init_now) initialize();
	}
	
	/**
	 * Create new KrkrScenes.
	 * @param path Path to local file. ( json type )
	 * @param init_now Initialize immediately.
	 * @throws Throwable If failed to create.
	 */
	public KrkrScenes(String path, boolean init_now)
			throws Throwable
	{ this(KrkrUtils.loadJsonFile(path), init_now); }
	
	/**
	 * Create new KrkrScenes.
	 * @param file File to load. ( json type )
	 * @param init_now Initialize immediately.
	 * @throws Throwable If failed to create.
	 */
	public KrkrScenes(File file, boolean init_now)
			throws Throwable
	{ this(KrkrUtils.loadJsonFile(file), init_now); }
	
	/**
	 * Create new KrkrScenes.
	 * @param file MultipartFile to load. ( json type )
	 * @param init_now Initialize immediately.
	 * @throws Throwable If failed to create.
	 */
	public KrkrScenes(MultipartFile file, boolean init_now)
			throws Throwable
	{ this(KrkrUtils.loadJsonFile(file), init_now); }
	
	/**
	 * Create new KrkrScenes, initialize immediately.
	 * @param path Path to local file. ( json type )
	 * @throws Throwable If failed to create.
	 */
	public KrkrScenes(String path)
			throws Throwable
	{ this(path, true); }
	
	/**
	 * Create new KrkrScenes, initialize immediately.
	 * @param file File to load. ( json type )
	 * @throws Throwable If failed to create.
	 */
	public KrkrScenes(File file)
			throws Throwable
	{ this(file, true); }
	
	/**
	 * Create new KrkrScenes, initialize immediately.
	 * @param file MultipartFile to load. ( json type )
	 * @throws Throwable If failed to create.
	 */
	public KrkrScenes(MultipartFile file)
			throws Throwable
	{ this(file, true); }
}

