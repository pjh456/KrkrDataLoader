package KrkrDataLoader.json;

import KrkrDataLoader.core.ParentChild;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonPath
		extends ParentChild
		implements AutoCloseable
{
	private JsonElement data = null;
	
	// 是否在一个 JsonArray 之中
	private boolean isInRow = false;
	
	public JsonPath(String name, ParentChild parent, JsonElement data, boolean isInRow)
	{
		super(name, parent);
		this.isInRow = isInRow;
		loadFromJson(data);
	}
	
	public JsonPath(String name, ParentChild parent, JsonElement data) { this(name, parent, data, false); }
	
	public JsonPath(String name, JsonElement data) { this(name, null, data); }
	
	public JsonPath(String name) { this(name, null, null); }
	
	public void loadFromJson(JsonElement data)
	{
		setData(data);
		if(data.isJsonArray())
		{
			for(JsonElement child: data.getAsJsonArray())
			{
				addChild(new JsonPath(Integer.toString(childrenMap.size()), this, child, true));
			}
		}
		else if(data.isJsonObject())
		{
			for(Map.Entry<String,JsonElement> child: data.getAsJsonObject().entrySet())
			{
				addChild(new JsonPath(child.getKey(), this, child.getValue(), false));
			}
		}
	}
	
	public void setData(JsonElement data) { this.data = data; }
	
	public JsonElement getData() { return data; }
	
	public boolean isInRow() { return isInRow; }
	
	/**
	 * Get path name in JsonArray and JsonObject differently.
	 *
	 * @return Real path name (integer or string).
	 */
	public Object getObjectName()
	{
		try{ return Integer.decode(getName()); }
		catch(NumberFormatException e){ return getName(); }
	}
	
}
