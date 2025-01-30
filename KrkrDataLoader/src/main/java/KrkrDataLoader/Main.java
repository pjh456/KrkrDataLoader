package KrkrDataLoader;

import KrkrDataLoader.core.KrkrDialogue;
import KrkrDataLoader.core.KrkrScene;
import KrkrDataLoader.core.KrkrScenes;
import KrkrDataLoader.core.ParentChild;
import KrkrDataLoader.setting.GlobalSetting;
import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
public class Main
		implements WebMvcConfigurer
{
	public static void main(String[] args)
			throws Throwable
	{
		// TODO：准备开始开发前端内容
//		System.out.println(KrkrUtils.isFile(GlobalSetting.getSettingPath()));
		GlobalSetting.loadFromJson();
		String scenePath = "KrkrDataLoader/src/test/001・アーサー王ver1.07.ks.json";
		KrkrScenes scenes = new KrkrScenes(scenePath);
		System.out.println(scenes.getName());
		
		System.out.println(scenes.getChild(2).size());
		
		for(ParentChild dialogue: ( (KrkrScene) scenes.getChild(2) ).listChildren())
		{
			System.out.println((KrkrDialogue) dialogue);
		}
		
		for(ParentChild scene: scenes.listChildren())
		{
			System.out.println(scene.getName());
			for(ParentChild dialogue: ( (KrkrScene) scene ).listChildren())
			{
				System.out.println((KrkrDialogue) dialogue);
			}
		}

//		System.setProperty("server.port", "8080");
//
//		SpringApplication.run(Main.class, args);
	}
	
	@Bean
	public Gson gson()
	{
		return new Gson();
	}
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
	{
		// 注册 GsonHttpMessageConverter
		converters.add(new GsonHttpMessageConverter(gson()));
	}
}
