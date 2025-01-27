package KrkrDataLoader.core;

import com.google.gson.JsonElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KrkrData
		implements AutoCloseable
{
	public String name;
	
	protected JsonElement data = null;
	
	private final Map<String,KrkrData> children_map;
	
	public KrkrData parent = null;
	
	protected boolean is_init = false;
	
	/**
	 * Initialize data. ( will be defined in children class )
	 *
	 * @throws Throwable If an error occurs while initializing.
	 */
	public void initialize()
			throws Throwable
	{ is_init = true; }
	
	/**
	 * Create new KrkrData that isn't initialized.
	 *
	 * @param name Name of KrkrData.
	 */
	public KrkrData(String name)
	{
		this.name = name;
		this.children_map = new LinkedHashMap<>();
		is_init = false;
	}
	
	/**
	 * Check if data is initialized.
	 *
	 * @return True if data is initialized, false otherwise.
	 */
	public boolean isInit() { return is_init; }
	
	/**
	 * Get child data by name.
	 *
	 * @param name Name of child data.
	 *
	 * @return Selected child data.
	 */
	public KrkrData getChild(String name) { return this.children_map.get(name); }
	
	/**
	 * Get child data by index.
	 *
	 * @param index Index of child data.
	 *
	 * @return Selected child data.
	 *
	 * @throws IndexOutOfBoundsException if index is out of range.
	 */
	public KrkrData getChild(int index)
			throws IndexOutOfBoundsException
	{ return this.children_map.values().stream().toList().get(index); }
	
	/**
	 * Set child data.
	 *
	 * @param child Child data to set.
	 */
	public void setChild(KrkrData child)
	{
		this.children_map.put(child.name, child);
		child.parent = this;
	}
	
	/**
	 * List all child data.
	 *
	 * @return A List of child data. ( List<KrkrData> )
	 */
	public List<KrkrData> listChildren() { return this.children_map.values().stream().toList(); }
	
	/**
	 * Get size of child data.
	 *
	 * @return Size of child data.
	 */
	public int size() { return this.children_map.size(); }
	
	/**
	 * Format output of KrkrData.
	 *
	 * @return Formatted KrkrData. ( only name now )
	 */
	@Override
	public String toString() { return name; }
	
	/**
	 * Close KrkrData. ( defined to automatically close data )
	 *
	 * @throws Exception If an error occurs while closing.
	 */
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
