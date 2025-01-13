package KrkrDataLoader.core;

import com.google.gson.JsonElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KrkrData
		implements AutoCloseable
{
	public String name;
	private Map<String,KrkrData> children_map;
	public KrkrData parent = null;
	
	protected JsonElement data = null;
	protected boolean is_init = false;
	
	public void initialize()
			throws Throwable
	{ is_init = true; }
	
	public KrkrData(String name)
	{
		this.name = name;
		this.children_map = new LinkedHashMap<>();
		is_init = false;
	}
	
	public boolean isInit() { return is_init; }
	
	public KrkrData getChild(String name)
	{
		return this.children_map.get(name);
	}
	
	public KrkrData getChild(int index)
			throws IndexOutOfBoundsException
	{ return this.children_map.values().stream().toList().get(index); }
	
	public void setChild(KrkrData child)
	{
		this.children_map.put(child.name, child);
		child.parent = this;
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
	
	@Override
	public void close()
			throws Exception
	{
		for(KrkrData child: this.listChildren())
		{
			child.close();
		}
	}
}
