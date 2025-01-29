package KrkrDataLoader.core;

import KrkrDataLoader.config.GlobalConfig;
import KrkrDataLoader.setting.GlobalSetting;
import com.google.gson.JsonElement;

/**
 * Single dialogue in KrkrScene.
 */
public class KrkrDialogue
		extends KrkrData
{
	private String speaker = (String) GlobalSetting.getCurrentSetting().getSetting("speaker").getState();
	
	private String content = (String) GlobalSetting.getCurrentSetting().getSetting("content").getState();
	
	private KrkrVoice voice = null;
	
	/**
	 * Create new KrkrDialogue without data.
	 *
	 * @param name Name of KrkrDialogue.
	 */
	public KrkrDialogue(String name) { super(name); }
	
	/**
	 * Create new KrkrDialogue with data.
	 *
	 * @param name Name of KrkrDialogue.
	 * @param data Json data.
	 *
	 * @throws Throwable if data is invalid.
	 */
	public KrkrDialogue(String name, JsonElement data)
			throws Throwable
	{
		super(name);
		
		this.speaker = null;
		try{
			this.speaker = GlobalConfig.getCurrentConfigs()
									   .getConfig("speaker")
									   .matchValueAsJsonPrimitive(data)
									   .getAsString();
		}
		catch(Throwable ignored){ }
		
		// Content is necessary.
		this.content = GlobalConfig.getCurrentConfigs()
								   .getConfig("content")
								   .matchValueAsJsonPrimitive(data)
								   .getAsString();
		
		this.voice = null;
		try{ setChild(voice = new KrkrVoice("voice",
											GlobalConfig.getCurrentConfigs()
														.getConfig("voice")
														.matchValueAsJsonPrimitive(data)
														.getAsString()
		));
		}
		catch(Throwable ignored){ }
	}
	
	/**
	 * Play voice of dialogue if having. ( no longer supported )
	 */
	public void play() { if(voice != null) { voice.play(); } }
	
	/**
	 * Stop voice of dialogue if having. ( no longer supported )
	 */
	public void stop() { if(voice != null) { voice.stop(); } }
	
	/**
	 * Format output of dialogue.
	 *
	 * @return Formatted dialogue.
	 */
	@Override
	public String toString()
	{
		return speaker == null ? ( content ) : ( "【" + speaker + "】" + content );
	}
	
	public String getSpeaker() { return this.speaker; }
	
	public String getContent() { return this.content; }
	
	public KrkrVoice getVoice() { return this.voice; }
}
