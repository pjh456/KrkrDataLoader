package KrkrDataLoader.core;

import KrkrDataLoader.config.JsonPath;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.jdi.InvalidTypeException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class KrkrUtils
{
	public static JsonObject loadJsonFile(File file)
	throws FileNotFoundException, InvalidTypeException, IOException
	{
		
		// 使用BufferedReader逐行读取文件内容
		StringBuilder contentBuilder = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new FileReader(file.getPath())))
		{
			String line;
			while(( line = reader.readLine() ) != null)
			{
				contentBuilder.append(line);
			}
		}
		// 创建Gson对象以解析JSON
		Gson gson = new Gson();
		// 将读取的内容解析为JsonObject并返回
		return gson.fromJson(contentBuilder.toString(), JsonObject.class);
	}
	
	/**
	 * 加载并解析JSON文件
	 * 该方法首先验证给定路径的文件是否存在且为JSON文件，然后读取文件内容并将其解析为JsonObject
	 *
	 * @param path JSON文件的路径
	 *
	 * @return 解析后的JsonObject对象
	 *
	 * @throws FileNotFoundException 如果指定路径的文件不存在
	 * @throws InvalidTypeException  如果文件扩展名不是.json
	 * @throws Throwable             如果文件读取过程中发生错误
	 */
	public static JsonObject loadJsonFile(String path)
	throws FileNotFoundException, InvalidTypeException, IOException
	{
		// 检查指定路径是否为文件，如果不是，则抛出异常
		if(! isFile(path))
		{
			throw new FileNotFoundException(path);
		}
		
		if(! path.toLowerCase().endsWith(".json"))
		{
			throw new InvalidTypeException("Invalid file type: " + path.substring(path.lastIndexOf(".")));
		}
		
		// 使用BufferedReader逐行读取文件内容
		StringBuilder contentBuilder = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new FileReader(path)))
		{
			String line;
			while(( line = reader.readLine() ) != null)
			{
				contentBuilder.append(line);
			}
		}
		// 创建Gson对象以解析JSON
		Gson gson = new Gson();
		// 将读取的内容解析为JsonObject并返回
		return gson.fromJson(contentBuilder.toString(), JsonObject.class);
	}
	
	/**
	 * 加载指定路径下的所有JSON文件
	 * 该方法会遍历指定路径下的所有文件，找到所有扩展名为.json的文件，并将其内容加载到一个JsonObject列表中
	 *
	 * @param path 文件夹的路径，用于指定要加载JSON文件的目录
	 *
	 * @return 返回一个包含所有加载的JsonObject的列表
	 *
	 * @throws Throwable 如果指定路径不是一个文件夹，则抛出FileNotFoundException
	 */
	public static List<JsonObject> loadJsonFolder(String path)
	throws Throwable
	{
		// 检查指定路径是否为文件夹，如果不是，则抛出异常
		if(! isFolder(path))
		{
			throw new FileNotFoundException(path);
		}
		
		// 创建File对象以访问指定路径下的文件和文件夹
		File folder = new File(path);
		// 获取文件夹下的所有文件和子文件夹的数组
		File[] files = folder.listFiles();
		// 如果files为空，则抛出异常
		if(files == null)
		{
			throw new IOException("Failed to list files in directory: " + path);
		}
		// 创建一个列表以存储所有的JsonObject
		List<JsonObject> json_list = new ArrayList<>();
		for(File file: files)
		{
			// 分割文件路径以获取文件扩展名
			String[] single_path = file.getPath().split("\\.");
			// 检查文件是否为.json且倒数第二个部分为.ks，如果是，则加载文件
			if(single_path[single_path.length - 1].equals("json") && single_path[single_path.length - 2].equals("ks"))
			{
				// 打印加载文件的路径
				System.out.println("loading " + file.getPath());
				// 加载JSON文件并添加到列表中
				json_list.add(loadJsonFile(file.getAbsolutePath()));
			}
		}
		// 返回包含所有JsonObject的列表
		return json_list;
	}
	
	/**
	 * 判断给定路径是否为文件
	 *
	 * @param path 文件路径
	 *
	 * @return 如果路径表示为文件，则返回true；否则返回false
	 */
	public static boolean isFile(String path)
	{
		// 对路径进行规范化，防止路径注入攻击
		Path normalizedPath = Paths.get(path).normalize();
		
		// 使用Files类的方法一次性检查文件的存在性和类型
		try{ return Files.isRegularFile(normalizedPath); }
		catch(Throwable e){ return false; }
	}
	
	/**
	 * 判断给定路径是否为文件夹
	 *
	 * @param path 文件或目录的路径
	 *
	 * @return 如果路径表示的是一个文件夹，则返回true；否则返回false
	 */
	public static boolean isFolder(String path)
	{
		// 对路径进行规范化，防止路径注入攻击
		Path normalizedPath = Paths.get(path).normalize();
		
		// 使用Files类的方法一次性检查文件的存在性和类型
		try{ return Files.isDirectory(normalizedPath); }
		catch(Throwable e){ return false; }
	}
	
	// 检验是否来自同一个路径
	public static boolean isPathInPath(JsonPath parentPath, JsonPath childPath)
	{
		List<JsonPath> parentPathList = parentPath.listPath();
		List<JsonPath> childPathList = childPath.listPath();
		
		if(parentPathList.size() > childPathList.size()) { return false; }
		
		for(int index = 0; index < parentPathList.size(); ++ index)
		{
			if(! parentPathList.get(index).equals(childPathList.get(index))) { return false; }
		}
		
		return true;
	}
	
	public static List<JsonPath> removeSamePath(JsonPath parentPath, JsonPath childPath)
	throws Exception
	{
		if(! isPathInPath(parentPath, childPath))
		{
			throw new Exception("Check ChildPath is in ParentPath before calling this method!");
		}
		
		List<JsonPath> parentPathList = parentPath.listPath();
		List<JsonPath> childPathList = childPath.listPath();
		
		return childPathList.subList(parentPathList.size(), childPathList.size());
	}
	
	
	public static List<Object> removeSamePath_object(JsonPath parentPath, JsonPath childPath)
	throws Exception
	{
		if(! isPathInPath(parentPath, childPath))
		{
			throw new Exception("Check ChildPath is in ParentPath before calling this method!");
		}
		
		List<Object> parentPathList = parentPath.listObjectPath();
		List<Object> childPathList = childPath.listObjectPath();
		
		return childPathList.subList(parentPathList.size(), childPathList.size());
	}
}
