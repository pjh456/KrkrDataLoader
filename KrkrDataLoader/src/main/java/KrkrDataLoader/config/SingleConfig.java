package KrkrDataLoader.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

public class SingleConfig
{
	private final String name;
	
	// 未来可能有多模式匹配，因此暂时先是列表嵌套
	// 每一个 Object 都是 String 或 int 类型，分别用于指定该层级相对于上一层级的位置，分别对应 JsonObject 和 JsonArray
	private final List<List<Object>> fieldsList = new ArrayList<>();
	
	public SingleConfig(String name, List<Object> fieldList)
			throws NullPointerException, IllegalArgumentException
	{
		this.name = name;
		addFields(fieldList);
	}
	
	public SingleConfig(String name) { this(name, null); }
	
	/**
	 * Add new list of fields for matching data value.
	 *
	 * @param fieldList List of fields.
	 *
	 * @throws NullPointerException     If fieldsList is null.
	 * @throws IllegalArgumentException If fieldsList is not fully made up of String and Integer.
	 */
	public void addFields(List<Object> fieldList)
			throws NullPointerException, IllegalArgumentException
	{
		if(fieldList == null) { throw new NullPointerException("FieldList cannot be null!"); }
		
		if(! SingleConfig.checkFields(fieldList))
		{
			throw new IllegalArgumentException("Type of objects in fieldsList must be String or Integer!");
		}
		
		fieldsList.add(new ArrayList<>(fieldList));
	}
	
	public List<List<Object>> getFieldsList() { return fieldsList; }
	
	public void clearFields() { fieldsList.clear(); }
	
	// 每一项必须是 String 或 int 类型，分别对应 JsonObject 和 JsonArray
	public static boolean checkFields(List<Object> fieldList)
	{
		for(Object field: fieldList)
		{
			if(field instanceof String || field instanceof Integer) { continue; }
			return false;
		}
		return true;
	}
	
	public String getName() { return this.name; }
	
	public JsonObject matchValueAsJsonObject(JsonElement data) { return matchValueAsJsonELement(data).getAsJsonObject(); }
	
	public JsonArray matchValueAsJsonArray(JsonElement data) { return matchValueAsJsonELement(data).getAsJsonArray(); }
	
	public JsonPrimitive matchValueAsJsonPrimitive(JsonElement data) { return matchValueAsJsonELement(data).getAsJsonPrimitive(); }
	
	
	/**
	 * Match value from json data.
	 *
	 * @param data Json data.
	 *
	 * @return Matched json element value.
	 */
	private JsonElement matchValueAsJsonELement(JsonElement data)
	{
		JsonElement new_data = null;
		// 多个模式匹配数据
		for(List<Object> field: fieldsList)
		{
			if(new_data != null) { break; }
			try{ new_data = matchValueFromData(data, field); }
			catch(NoSuchFieldException ignored){ }
		}
		return new_data;
	}
	
	/**
	 * Match value from json data using field list.
	 *
	 * @param data      Json data.
	 * @param fieldList List of fields, each field is String or Integer.
	 *
	 * @return Matched json element value.
	 *
	 * @throws NoSuchFieldException if field type and data are not matched.
	 */
	private JsonElement matchValueFromData(JsonElement data, List<Object> fieldList)
			throws NoSuchFieldException
	{
		JsonElement cache_element = data;
		for(Object field: fieldList)
		{
			if(field instanceof String && cache_element.isJsonObject())
			{
				cache_element = cache_element.getAsJsonObject().get(field.toString());
			}
			else if(field instanceof Integer && cache_element.isJsonArray())
			{
				cache_element = cache_element.getAsJsonArray().get((Integer) field);
			}
			else
			{
				throw new NoSuchFieldException("Field type and Data are not matched!");
			}
		}
		return cache_element;
	}
	
	@Override
	public String toString()
	{
		return "SingleConfig\n\t{\n\t\t" + "name='" + name + '\'' + ", \n\t\tfieldsList=" + fieldsList + "\n\t}";
	}
}
