package KrkrDataLoader;

import KrkrDataLoader.command.CommandAutoLoader;
import KrkrDataLoader.config.*;

public class Main
{
	public static void main(String[] args)
	throws Throwable
	{
		Settings.loadFromJson();

//		String config_name = "KrkrDataLoader/src/test/configs.json";
//		String config_name = "KrkrDataLoader/src/test/testConfig.json";
//		Config.loadFromJson(config_name);
//		Config.saveConfigs(config_name);
	}
}
