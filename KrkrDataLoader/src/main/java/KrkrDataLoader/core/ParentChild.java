package KrkrDataLoader.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ParentChild
		implements AutoCloseable
{
	protected ParentChild parent = null;
	protected Map<String,ParentChild> childrenMap = new LinkedHashMap<>();
	
	protected String name;
	
	public ParentChild(String name, ParentChild parent)
	{
		this.name = name;
		this.parent = parent;
	}
	
	public ParentChild(String name) { this(name, null); }
	
	public ParentChild(ParentChild parent) { this("default", parent); }
	
	public ParentChild() { this("default"); }
	
	public void setParent(ParentChild parent) { this.parent = parent; }
	
	public ParentChild getParent() { return parent; }
	
	
	/**
	 * Add child data.
	 *
	 * @param child Child data to be added.
	 */
	public void addChild(String name, ParentChild child)
	{
		childrenMap.put(name, child);
		child.setParent(this);
	}
	
	public void addChild(ParentChild child) { if(child != null) { addChild(child.getName(), child); } }
	
	public void addAllChildren(Map<String,ParentChild> childrenMap)
	{ for(Map.Entry<String,ParentChild> child: childrenMap.entrySet()) { addChild(child.getKey(), child.getValue()); } }
	
	public void addAllChildren(List<ParentChild> childrenList)
	{ for(ParentChild child: childrenList) { addChild(child); } }
	
	/**
	 * List all child data.
	 *
	 * @return A List of child data. ( List<ParentChild> )
	 */
	public List<ParentChild> listChildren() { return childrenMap.values().stream().toList(); }
	
	public List<String> listChildrenName() { return listChildren().stream().map(ParentChild::getName).toList(); }
	
	/**
	 * Get child data by name.
	 *
	 * @param name Name of child data.
	 *
	 * @return Selected child data.
	 */
	public ParentChild getChild(String name) { return childrenMap.get(name); }
	
	/**
	 * Get child data by index.
	 *
	 * @param index Index of child data.
	 *
	 * @return Selected child data.
	 *
	 * @throws IndexOutOfBoundsException if index is out of range.
	 */
	public ParentChild getChild(int index)
			throws IndexOutOfBoundsException
	{ return listChildren().get(index); }
	
	public boolean hasChild(String name) { return childrenMap.containsKey(name); }
	
	public boolean hasChild(int index) { return index < size() && index >= 0; }
	
	public void setName(String name) { this.name = name; }
	
	public String getName() { return name; }
	
	public List<ParentChild> listAbstractPath()
	{
		List<ParentChild> parentList = getParent() == null ? new ArrayList<>() : getParent().listAbstractPath();
		parentList.add(this);
		return parentList;
	}
	
	public List<String> listAbstractPathName()
	{
		return listAbstractPath().stream().map(ParentChild::getName).toList();
	}
	
	/**
	 * Get size of child data.
	 *
	 * @return Size of child data.
	 */
	public int size() { return childrenMap.size(); }
	
	/**
	 * Format output of data.
	 *
	 * @return Formatted data. ( only name )
	 */
	@Override
	public String toString() { return name; }
	
	@Override
	public void close()
			throws Exception
	{ for(ParentChild child: listChildren()) { child.close(); } }
}
