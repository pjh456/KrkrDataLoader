package KrkrDataLoader.network.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KrkrConfigFileService
{
	private final Map<String, String> configMap = new ConcurrentHashMap<>();
	
	public boolean handleConfigFileUpload(String taskId, MultipartFile file)
	{
		
//		configMap.put(taskId, fileContent);
		return true;
	}
}
