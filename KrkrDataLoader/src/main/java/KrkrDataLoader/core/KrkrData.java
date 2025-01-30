package KrkrDataLoader.core;

import com.google.gson.JsonElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KrkrData
		extends ParentChild
		implements AutoCloseable
{
	protected JsonElement data = null;
	
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
		super(name);
		is_init = false;
	}
	
	/**
	 * Check if data is initialized.
	 *
	 * @return True if data is initialized, false otherwise.
	 */
	public boolean isInit() { return is_init; }
	
}
