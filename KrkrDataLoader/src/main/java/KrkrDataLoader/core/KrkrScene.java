package KrkrDataLoader.core;

import KrkrDataLoader.config.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class KrkrScene
		extends KrkrData
{
	
	public KrkrScene(JsonElement data) throws Throwable
	{
		//super(Config.getSingleConfig("scene_label").getValueAsJsonPrimitive(data).getAsString());
		super(Config.SceneNameConfig.getValueAsJsonPrimitive(data).getAsString());
		
		JsonArray dialogues_array = null;
		
		try
		{
			//dialogues_array = Config.getSingleConfig("dialogues").getValueAsJsonArray(data);
			dialogues_array = Config.DialoguesConfig.getValueAsJsonArray(data);
			
			int index = 0;
			for(JsonElement element: dialogues_array)
			{
				this.setChild(new KrkrDialogue(Integer.toString(index), element));
				index++;
			}
		}
		catch(Throwable ignored)
		{
		
		}
	}
	
	public List<String> listDialogues()
	{
		List<String> dialogueList = new ArrayList<>();
		for(KrkrData child: listChildren())
		{
			dialogueList.add(((KrkrDialogue)child).toString());
		}
		return dialogueList;
	}
}
