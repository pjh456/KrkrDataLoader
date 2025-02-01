package KrkrDataLoader.json;

import KrkrDataLoader.config.Configs;
import KrkrDataLoader.config.SingleConfig;
import KrkrDataLoader.core.KrkrUtils;
import com.google.gson.JsonElement;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class JsonFile
{
	private JsonElement data = null;
	
	private String name;
	
	private JsonPath root = null;
	
	private JsonPath currentPath = null;
	
	private final Configs configs = new Configs();
	
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
	
	public void gotoChild(String name)
	{
		if(currentPath.hasChild(name)) { currentPath = (JsonPath) currentPath.getChild(name); }
	}
	
	public void gotoChild(int index)
	{
		if(currentPath.hasChild(index)) { currentPath = (JsonPath) currentPath.getChild(index); }
	}
	
	public void gotoParent()
	{
		if(currentPath.getParent() != null) { currentPath = (JsonPath) currentPath.getParent(); }
	}
	
	public void setCurrentPathAsConfig(String name)
	{
		JsonPath newPath = getCurrentPath();
		if(newPath != null) { configs.setConfig(new SingleConfig(name, newPath.listAbsolutePathObject())); }
	}
	
	public Configs getConfigs() { return configs; }
	
	public void setName(String name) { this.name = name; }
	
	public String getName() { return this.name; }
	
	public JsonPath getCurrentPath() { return currentPath; }
	
	// 这里必须是当前 File 内部的 JsonPath！不然会错位出 BUG 的！
	public void setCurrentPath(JsonPath currentPath){this.currentPath = currentPath;}
	
	public void setRoot(JsonPath root) { data = ( currentPath = this.root = root ).getData(); }
	
	public JsonPath getRoot() { return root; }
}
