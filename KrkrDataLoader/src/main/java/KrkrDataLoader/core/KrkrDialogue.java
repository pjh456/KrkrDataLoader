package KrkrDataLoader.core;

import KrkrDataLoader.config.Config;
import KrkrDataLoader.config.Settings;
import com.google.gson.JsonElement;

public class KrkrDialogue
		extends KrkrData
{
	public String speaker = Settings.speaker;
	public String content = Settings.content;
	private KrkrVoice voice = null;
	
	public KrkrDialogue(String name)
	{
		super(name);
	}
	
	public KrkrDialogue(String name, JsonElement data)
	throws Throwable
	{
		super(name);
		
		this.speaker = null;
		try
		{
			//this.speaker = Config.getSingleConfig("speaker").getValueAsJsonPrimitive(data).getAsString();
			this.speaker = Config.SpeakerConfig.getValueAsJsonPrimitive(data).getAsString();
		}
		catch(Throwable ignored)
		{
		
		}
		
		//this.content = Config.getSingleConfig("content").getValueAsJsonPrimitive(data).getAsString();
		this.content = Config.ContentConfig.getValueAsJsonPrimitive(data).getAsString();
		
		this.voice = null;
		try
		{
			//voice = new KrkrVoice("voice", Config.getSingleConfig("voice").getValueAsJsonPrimitive(data).getAsString());
			voice = new KrkrVoice("voice", Config.VoiceConfig.getValueAsJsonPrimitive(data).getAsString());
			setChild(voice);
		}
		catch(Throwable ignored)
		{
		
		}
	}
	
	public void play()
	{
		if(voice != null)
		{
			voice.play();
		}
	}
	
	public void stop()
	{
		if(voice != null)
		{
			voice.stop();
		}
	}
	
	@Override
	public String toString()
	{
		return speaker == null ? ( content ) : ( "【" + speaker + "】" + content );
	}
}
