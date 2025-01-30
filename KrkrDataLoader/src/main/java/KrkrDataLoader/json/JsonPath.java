package KrkrDataLoader.json;

import KrkrDataLoader.core.ParentChild;
import com.google.gson.JsonElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonPath extends ParentChild
		implements AutoCloseable
{
	private JsonElement data = null;
	
	public JsonPath(String name, JsonElement data)
	{
		super(name);
		this.data = data;
	}
	
	public JsonPath(String name) { this(name, null); }
	
	public JsonElement getData(){ return data;}
}
