package KrkrDataLoader.config;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

public class SingleConfig
{
	public String name;                // 配置的唯一标识符
	// 由于提供对多种匹配模式的支持，这里是一个列表嵌套
	public List<List<Object>> fieldsList = new ArrayList<>();// 相对于上一层级的相对路径
	
	public SingleConfig(String name)
	{
		this.name = name;
	}
	
	public SingleConfig(String name, List<Object> fields)
	throws Exception
	{
		this.name = name;
		
		addFields(fields);
	}
	
	public List<List<Object>> getFieldsList(){return fieldsList;}
	
	public void clearFields() {this.fieldsList = new ArrayList<>();}
	
	public void addFields(List<Object> fields)
	throws Exception
	{
		List<Object> newList = new ArrayList<>();
		for(Object field: fields)
		{
			if(( field instanceof String ) || ( field instanceof Integer )){newList.add(field);}
			else if(field instanceof JsonPath){newList.add(( (JsonPath) field ).name);}
			else{throw new Exception("Fields only support String, Integer or JsonPath !");}
		}
		this.fieldsList.add(newList);
	}
	
	public JsonObject getValueAsJsonObject(JsonElement data)
	throws Throwable
	{return getValueAsJsonElement(data).getAsJsonObject();}
	
	public JsonArray getValueAsJsonArray(JsonElement data)
	throws Throwable
	{return getValueAsJsonElement(data).getAsJsonArray();}
	
	public JsonPrimitive getValueAsJsonPrimitive(JsonElement data)
	throws Throwable
	{return getValueAsJsonElement(data).getAsJsonPrimitive();}
	
	private JsonElement getValueAsJsonElement(JsonElement data)
	throws Throwable
	{
		JsonElement new_data = null;
		for(List<Object> fields: fieldsList)
		{
			if(new_data != null){break;}
			new_data = getValueFromList(data, fields);
		}
		if(new_data == null){throw new NoSuchFieldError("Load Json Error in field " + name + ": Can't get data from fields!");}
		return new_data;
	}
	
	private JsonElement getValueFromList(JsonElement data, List<Object> fields)
	{
		JsonElement cache_element = data;
		for(Object field: fields)
		{
			if(field instanceof String)
			{
				if(cache_element.isJsonObject()){cache_element = cache_element.getAsJsonObject().get(field.toString());}
				else
				{
					// throw new Exception();
				}
			}
			else if(field instanceof Integer)
			{
				if(cache_element.isJsonArray()){cache_element = cache_element.getAsJsonArray().get((Integer) field);}
				else
				{
					// throw new Exception();
				}
			}
			else
			{
				// throw new Exception();
			}
		}
		return cache_element;
	}
}
