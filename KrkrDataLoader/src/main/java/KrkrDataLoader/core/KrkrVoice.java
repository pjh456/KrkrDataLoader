package KrkrDataLoader.core;

/**
 * Single voice in KrkrDialogue. ( no longer supported )
 */
public class KrkrVoice
		extends KrkrData
{
	public final String path;
	
	public KrkrVoice(String name, String path)
	{
		super(name);
		this.path = path;
	}
	
	public void play(){}
	
	public void stop(){}
}
