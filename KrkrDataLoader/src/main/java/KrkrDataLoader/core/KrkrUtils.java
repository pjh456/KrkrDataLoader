package KrkrDataLoader.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.jdi.InvalidTypeException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class KrkrUtils
{
	/**
	 * Load and parse single scene file.
	 *
	 * @param file File to parse. ( json type )
	 *
	 * @return Loaded json object.
	 *
	 * @throws IOException If error happens when parsing.
	 */
	public static JsonObject loadJsonFile(MultipartFile file)
			throws IOException
//	{ return loadJsonFile(new BufferedReader(new InputStreamReader(file.getInputStream()))); }
	{
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream())))
		{
			return loadJsonFile(reader);
		}
	}
	
	/**
	 * Load and parse single scene file.
	 *
	 * @param file File to parse. ( json type )
	 *
	 * @return Loaded json object.
	 *
	 * @throws IOException If error happens when parsing.
	 */
	public static JsonObject loadJsonFile(File file)
			throws IOException
//	{ return loadJsonFile(new BufferedReader(new FileReader(file.getPath()))); }
	{
		try(BufferedReader reader = new BufferedReader(new FileReader(file.getPath())))
		{
			return loadJsonFile(reader);
		}
	}
	
	/**
	 * Load and parse single scene file by path.
	 *
	 * @param path Path to file.
	 *
	 * @return Loaded json object.
	 *
	 * @throws FileNotFoundException If the file does not exist or is not a file.
	 * @throws InvalidTypeException  If the file does not have a .json extension.
	 * @throws IOException           If error happens when parsing.
	 */
	public static JsonObject loadJsonFile(String path)
			throws FileNotFoundException, InvalidTypeException, IOException
	{
		if(! isFile(path)) { throw new FileNotFoundException(path); }
		
		if(! path.toLowerCase().endsWith(".json"))
		{
			throw new InvalidTypeException(
					"Invalid file type: " + path.substring(path.lastIndexOf(".")));
		}
		
//		return loadJsonFile(new BufferedReader(new FileReader(path)));
		try(BufferedReader reader = new BufferedReader(new FileReader(path)))
		{
			return loadJsonFile(reader);
		}
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
			if(single_path[single_path.length - 1].equals("json") &&
			   single_path[single_path.length - 2].equals("ks"))
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
	 * Check if path is a file.
	 *
	 * @param path Path to file.
	 *
	 * @return True if the path is a file, otherwise false.
	 */
	public static boolean isFile(String path)
	{
		try{ return Files.isRegularFile(Paths.get(path).normalize()); }
		catch(Throwable e){ return false; }
		// 这俩函数都是通义千问修改的，如果有bug给我提个issue
	}
	
	/**
	 * Check if path is a folder.
	 *
	 * @param path Path to folder.
	 *
	 * @return True if the path is a folder, otherwise false.
	 */
	public static boolean isFolder(String path)
	{
		try{ return Files.isDirectory(Paths.get(path).normalize()); }
		catch(Throwable e){ return false; }
	}
	
//	// 检验是否来自同一个路径
//	public static boolean isPathInPath(JsonPath parentPath, JsonPath childPath)
//	{
//		List<JsonPath> parentPathList = parentPath.listPath();
//		List<JsonPath> childPathList = childPath.listPath();
//
//		if(parentPathList.size() > childPathList.size()) { return false; }
//
//		for(int index = 0; index < parentPathList.size(); ++ index)
//		{
//			if(! parentPathList.get(index).equals(childPathList.get(index))) { return false; }
//		}
//
//		return true;
//	}
//
//	/**
//	 * Remove same prefix in two paths.
//	 *
//	 * @param parentPath Parent path. ( include child )
//	 * @param childPath  Child path. ( included by parent )
//	 *
//	 * @return List of child path after removing same prefix.
//	 *
//	 * @throws Exception If the child path is not in the parent path.
//	 */
//	public static List<JsonPath> removeSamePath(JsonPath parentPath, JsonPath childPath)
//			throws Exception
//	{
//		if(! isPathInPath(parentPath, childPath))
//		{
//			throw new Exception("Check ChildPath is in ParentPath before calling this method!");
//		}
//
//		List<JsonPath> parentPathList = parentPath.listPath();
//		List<JsonPath> childPathList = childPath.listPath();
//
//		return childPathList.subList(parentPathList.size(), childPathList.size());
//	}
//
//
//	public static List<Object> removeSamePath_object(JsonPath parentPath, JsonPath childPath)
//			throws Exception
//	{
//		if(! isPathInPath(parentPath, childPath))
//		{
//			throw new Exception("Check ChildPath is in ParentPath before calling this method!");
//		}
//
//		List<Object> parentPathList = parentPath.listObjectPath();
//		List<Object> childPathList = childPath.listObjectPath();
//
//		return childPathList.subList(parentPathList.size(), childPathList.size());
//	}

	/**
	 * Load json file by reader.
	 *
	 * @param reader Reader to read json file.
	 *
	 * @return Loaded file.
	 *
	 * @throws IOException If error happens when parsing.
	 */
	private static JsonObject loadJsonFile(BufferedReader reader)
			throws IOException
	{

		StringBuilder contentBuilder = new StringBuilder();
		String line;

		while(( line = reader.readLine() ) != null) { contentBuilder.append(line); }

		return new Gson().fromJson(contentBuilder.toString(), JsonObject.class);
	}
}
