package KrkrDataLoader.core;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KrkrData
{
	public String name;
	private Map<String, KrkrData> children_map;
	
	public KrkrData(String name)
	{
		this.name = name;
		this.children_map = new LinkedHashMap<>();
	}
	
	public KrkrData getChild(String name)
	{
		return this.children_map.get(name);
	}
	
	public void setChild(KrkrData child)
	{
		this.children_map.put(child.name, child);
	}
	
	public List<KrkrData> listChildren()
	{
		return this.children_map.values().stream().toList();
	}
	
	public int size()
	{
		return this.children_map.size();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
