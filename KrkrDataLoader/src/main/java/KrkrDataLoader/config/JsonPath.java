package KrkrDataLoader.config;

import KrkrDataLoader.core.KrkrUtils;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonPath
{
	private static int defaultIndex = 0;
	
	private final JsonElement data;
	
	public Object name;
	
	private boolean isInRow = false;
	
	private final Map<String,JsonPath> childMap = new LinkedHashMap<>();
	
	public JsonPath parent = null;
	
	public JsonPath(JsonElement data, Object name, JsonPath parent, Boolean isInRow)
	throws Exception
	{
		this.data = data;
		
		if(( ! ( name instanceof String ) ) && ( ! ( name instanceof Integer ) ))
		{
			throw new Exception("Type of JsonPath name is unexpected!");
		}
		this.name = name;
		
		this.parent = parent;
		
		if(data instanceof JsonArray)
		{
			for(JsonElement childData: (JsonArray) data)
			{
				childMap.put(Integer.toString(childMap.size()), new JsonPath(childData, childMap.size(), this, true));
			}
		}
		else if(data instanceof JsonObject)
		{
			Map<String,JsonElement> jsonMap = ( (JsonObject) data ).asMap();
			for(String childName: jsonMap.keySet())
			{
				childMap.put(childName, new JsonPath(jsonMap.get(childName), childName, this, false));
			}
		}
		else
		{
			// Ignore
			//System.out.println("Error in JsonPath!");
		}
	}
	
	public JsonPath(JsonElement data, Object name, Boolean isInRow)
	throws Exception
	{ this(data, name, null, isInRow); }
	
	public JsonPath(JsonElement data, Boolean isInRow)
	throws Exception
	{ this(data, getDefaultName(), null, isInRow); }
	
	public JsonPath(JsonElement data, Object name)
	throws Exception
	{ this(data, name, null, false); }
	
	public JsonPath(JsonElement data)
	throws Exception
	{ this(data, getDefaultName(), null, false); }
	
	public boolean isInRow() { return isInRow; }
	
	public List<JsonPath> listPath()
	{
		List<JsonPath> parentPathList = parent == null ? new ArrayList<>() : parent.listPath();
		parentPathList.add(this);
		return parentPathList;
	}
	
	// 为了适配SingleConfig初始化的List<Object>
	public List<Object> listObjectPath()
	{
		List<Object> parentPathList = parent == null ? new ArrayList<>() : parent.listObjectPath();
		parentPathList.add(this);
		return parentPathList;
	}
	
	// 方便调试用的，可以返回一个纯名字的列表
	public List<Object> listNamePath()
	{
		List<Object> parentPathList = parent == null ? new ArrayList<>() : parent.listNamePath();
		parentPathList.add(this.name);
		return parentPathList;
	}
	
	
	public List<JsonPath> listChildren() { return childMap.values().stream().toList(); }
	
	public JsonPath getChild(String name) { return childMap.get(name); }
	
	public JsonPath getChild(int index) { return childMap.values().stream().toList().get(index); }
	
	@Override
	public String toString() { return name.toString(); }
	
	public String getData() { return name + ": " + data; }
	
	public JsonElement getOriginalData() { return data; }
	
	public static String getDefaultName() { return "defaultPath(" + Integer.toString(JsonPath.defaultIndex++) + ")"; }
	
	public int size()
	{
		return childMap.size();
	}
}
