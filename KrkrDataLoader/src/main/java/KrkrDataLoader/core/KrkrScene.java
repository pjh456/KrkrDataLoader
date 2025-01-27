package KrkrDataLoader.core;

import KrkrDataLoader.config.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Single scene in KrkrScene, including dialogues.
 */
public class KrkrScene
		extends KrkrData
{
	/**
	 * Initialize KrkrScene. ( part of a whole file )
	 * @throws Throwable If initialization failed.
	 */
	@Override
	public void initialize()
			throws Throwable
	{
		JsonArray dialogues_array = null;
		
		try
		{
			dialogues_array = Config.DialoguesConfig.getValueAsJsonArray(data);
			
			int index = 0;
			for(JsonElement element: dialogues_array)
			{
				this.setChild(new KrkrDialogue(Integer.toString(index), element));
				index++;
			}
		}
		catch(Throwable ignored){ }
		
		this.data = null;
		is_init = true;
	}
	
	public KrkrScene(JsonElement data, boolean init_now)
			throws Throwable
	{
		super(Config.SceneNameConfig.getValueAsJsonPrimitive(data).getAsString());
		this.data = data;
		if(init_now) initialize();
	}
	
	public KrkrScene(JsonElement data)
			throws Throwable
	{ this(data, true); }
	
	/**
	 * Get all dialogues in this scene.
	 * @return List of dialogues.
	 */
	public List<String> listDialogues()
	{
		List<String> dialogueList = new ArrayList<>();
		for(KrkrData child: listChildren())
		{
			dialogueList.add(( (KrkrDialogue) child ).toString());
		}
		return dialogueList;
	}
}
