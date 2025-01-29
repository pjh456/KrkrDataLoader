package KrkrDataLoader.config;

import com.google.gson.JsonObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class GlobalConfig
{
	
	private static Configs currentConfigs = null;
	
	public static void loadFromJson(JsonObject data)
	{ setCurrentConfigs(new Configs(data)); }
	
	public static void loadFromJson(String path)
			throws Throwable
	{ setCurrentConfigs(new Configs(path)); }
	
	public static void loadFromJson(File file)
			throws Throwable
	{ setCurrentConfigs(new Configs(file)); }
	
	public static void loadFromJson(MultipartFile file)
			throws Throwable
	{ setCurrentConfigs(new Configs(file)); }
	
	public static boolean isInit() { return currentConfigs != null; }
	
	public static Configs getCurrentConfigs() { return currentConfigs; }
	
	public static void setCurrentConfigs(Configs configs) { currentConfigs = configs; }
	
	public static boolean hasCurrentConfigs() { return currentConfigs != null; }
	
	public static void saveCurrentConfigs(String path)
			throws NullPointerException, IOException
	{ currentConfigs.save(path); }
}
