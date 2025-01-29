package KrkrDataLoader.setting;

import KrkrDataLoader.config.GlobalConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class GlobalSetting
{
	private static String settingPath = "KrkrDataLoader/src/main/resources/settings.json";
	
	private static Settings currentSetting = null;
	
	public static void loadFromJson(String path)
			throws Throwable
	{ setCurrentSetting(new Settings(path)); }
	
	public static void loadFromJson(File file)
			throws Throwable
	{ setCurrentSetting(new Settings(file)); }
	
	public static void loadFromJson(MultipartFile file)
			throws Throwable
	{ setCurrentSetting(new Settings(file)); }
	
	public static void loadFromJson()
			throws Throwable
	{ loadFromJson(settingPath); }
	
	public static void loadCurrentConfig()
			throws Throwable
	{
		if(! hasCurrentSetting()) { throw new NullPointerException("No current setting!"); }
		GlobalConfig.loadFromJson(currentSetting.getSetting("config_path").getState().toString());
	}
	
	public static void saveCurrentSetting(String path)
			throws NullPointerException, IOException
	{
		if(! hasCurrentSetting()) { throw new NullPointerException("No current setting!"); }
		currentSetting.save(path);
	}
	
	public static void saveCurrentSetting()
			throws NullPointerException, IOException
	{ saveCurrentSetting(settingPath); }
	
	
	public static Settings getCurrentSetting() { return currentSetting; }
	
	public static void setCurrentSetting(Settings setting)
			throws Throwable
	{
		if(( currentSetting = setting ) != null) { loadCurrentConfig(); }
	}
	
	public static boolean hasCurrentSetting() { return currentSetting != null; }
	
	public static String getSettingPath() { return settingPath; }
	
	public static void setSettingPath(String path) { settingPath = path; }
}
