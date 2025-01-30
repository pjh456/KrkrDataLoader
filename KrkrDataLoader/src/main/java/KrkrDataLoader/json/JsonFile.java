package KrkrDataLoader.json;

import KrkrDataLoader.core.KrkrUtils;
import KrkrDataLoader.core.ParentChild;
import com.google.gson.JsonElement;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class JsonFile
{
	private JsonElement data = null;
	
	private String name;
	
	private JsonPath root = null;
	
	private JsonPath currentPath = null;
	
	public JsonFile(String name, JsonElement data)
	{
		setName(name);
		setRoot(new JsonPath(this.name, data));
	}
	
	public JsonFile(JsonElement data) { this("JsonFile", data); }
	
	public JsonFile(String path)
			throws Throwable
	{ this(path, KrkrUtils.loadJsonFile(path)); }
	
	public JsonFile(File file)
			throws Throwable
	{ this(file.getName(), KrkrUtils.loadJsonFile(file)); }
	
	public JsonFile(MultipartFile file)
			throws Throwable
	{ this(file.getName(), KrkrUtils.loadJsonFile(file)); }
	
	public JsonPath gotoChild(String name)
	{
		return currentPath.hasChild(name) ? currentPath = (JsonPath) currentPath.getChild(name) : currentPath;
	}
	
	public JsonPath gotoChild(int index)
	{
		return currentPath.hasChild(index) ? currentPath = (JsonPath) currentPath.getChild(index) : currentPath;
	}
	
	public ParentChild gotoParent()
	{
		return currentPath.getParent() != null ? currentPath = (JsonPath) currentPath.getParent() : currentPath;
	}
	
	public void setName(String name) { this.name = name; }
	
	public String getName() { return this.name; }
	
	public JsonPath getCurrentPath() { return currentPath; }
	
	public void setRoot(JsonPath root) { data = ( currentPath = this.root = root ).getData(); }
	
	public JsonPath getRoot() { return root; }
}
