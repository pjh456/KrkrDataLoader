package KrkrDataLoader.network.service;

import KrkrDataLoader.core.KrkrScenes;
import KrkrDataLoader.network.response.KrkrResponse;
import KrkrDataLoader.network.response.KrkrResponseBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KrkrSceneFileService
{
	private final Map<String,KrkrScenes> sceneMap = new ConcurrentHashMap<>();
	
	/**
	 * Handle uploaded scene file.
	 *
	 * @param taskId Unique identity of task. ( name of file now )
	 * @param file   Uploaded file.
	 *
	 * @return If file is updated successfully.
	 */
	public boolean handleSceneFileUpload(String taskId, MultipartFile file)
	{
		try
		{
			KrkrScenes newScene = new KrkrScenes(file, false);
			sceneMap.put(taskId, newScene);

//			new Thread(() ->
//			{
//				try
//				{
//					newScene.initialize();
//				}
//				catch(Throwable ignored){ }
//			}).start();
			// 我线程又写炸了？？
			newScene.initialize();
			
			return true;
		}
		catch(Throwable e){ return false; }
	}
	
	/**
	 * Check if scene file is parsed.
	 *
	 * @param taskId Unique identity of task. ( name of file now )
	 *
	 * @return If file is ready.
	 */
	public boolean isSceneReady(String taskId)
	{
		KrkrScenes scene = sceneMap.get(taskId);
		return scene != null && scene.isInit();
	}
	
	/**
	 * Get scene file.
	 *
	 * @param taskId Unique identity of task. ( name of file now )
	 *
	 * @return Parsed scene file.( only support KrkrScenes now ).
	 */
	public KrkrScenes getScene(String taskId) { return sceneMap.get(taskId); }
	
	/**
	 * Get file parsing progress. ( not available now )
	 *
	 * @param taskId Unique identity of task ( name of file now ).
	 *
	 * @return Number of parsing progress.
	 */
	public KrkrResponse parsingFileProgress(String taskId)
	{
		int progress = 100;
		
		return new KrkrResponseBuilder().setStatus("success").setCode(200).setMessage(
				"Getting scene file parsing progress.").setData(progress).build();
	}
}
