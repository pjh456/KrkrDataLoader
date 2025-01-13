package KrkrDataLoader.network;

import KrkrDataLoader.core.KrkrData;
import KrkrDataLoader.core.KrkrDialogue;
import KrkrDataLoader.core.KrkrScene;
import KrkrDataLoader.core.KrkrScenes;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KrkrResponseFactory
{
	public static ResponseEntity<Map<String,Object>> success(String message)
	{
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(200)
										.setMessage("Request succeeded: " + message)
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> error(String message)
	{
		return new KrkrResponseBuilder().setStatus("failed")
										.setCode(400)
										.setMessage("Request failed because: " + message)
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> sceneFileUploadFailed(String message)
	{
		return new KrkrResponseBuilder().setStatus("failed")
										.setCode(400)
										.setMessage("Scene file upload failed because: " + message)
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> sceneFileUploadSuccess()
	{
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(200)
										.setMessage("Scene file uploaded successfully, parsing has started.")
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> sceneFileParsingProgress(int progress)
	{
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(200)
										.setMessage("Getting scene file parsing progress.")
										.setData(progress)
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> resourceNotReady()
	{
		return new KrkrResponseBuilder().setStatus("accepted")
										.setCode(202)
										.setMessage("Resource is not ready, please try again later.")
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> resourceReady(String taskId)
	{
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(200)
										.setMessage("Resource is ready, please get it.")
										.setData(taskId)
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> outOfRange()
	{
		return new KrkrResponseBuilder().setStatus("Failed")
										.setCode(416)
										.setMessage("Request is out of range!")
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> unsupportedType()
	{
		return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage("Type is not supported!").build();
	}
	
	public static ResponseEntity<Map<String,Object>> krkrRangeInfo(KrkrData data, int begin, int end)
	{
		if(data == null) return resourceNotReady();
		
		if(begin < 0 || end > data.size()) return outOfRange();
		
		List<Map<String,Object>> childrenList = new ArrayList<>();
		for(KrkrData child: data.listChildren().subList(begin, end))
		{
			childrenList.add(Map.of("name", child.name, "scene_count", child.size()));
		}
		
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(200)
										.setMessage("The information of parsed data")
										.setData(childrenList)
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> krkrRangeInfo(KrkrData data, int begin)
	{
		return data == null ? resourceNotReady() : krkrRangeInfo(data, begin, data.size());
	}
	
	public static ResponseEntity<Map<String,Object>> krkrRangeInfo(KrkrData data)
	{
		return data == null ?
				resourceNotReady() :
				new KrkrResponseBuilder().setStatus("success")
										 .setCode(200)
										 .setMessage("The information of parsed data")
										 .setData(Map.of("name", data.name, "scene_count", data.size()))
										 .build();
	}
	
	public static ResponseEntity<Map<String,Object>> krkrRangeText(KrkrData data, int begin, int end)
	{
		if(data == null) return resourceNotReady();
		
		if(begin < 0 || end > data.size()) return outOfRange();
		
		List<String> childrenList = new ArrayList<>();
		if(data instanceof KrkrScenes)
		{
			for(KrkrData scene: data.listChildren().subList(begin, end))
			{
				childrenList.addAll(( (KrkrScene) scene ).listDialogues());
			}
		}
		else if(data instanceof KrkrScene) childrenList = ( (KrkrScene) data ).listDialogues().subList(begin, end);
		else return error("Bad Request: Type Error when getting Text. Promise current data is valid.");
		
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(206)
										.setMessage("The information of parsed data")
										.setData(childrenList)
										.build();
	}
	
	public static ResponseEntity<Map<String,Object>> krkrRangeText(KrkrData data, int begin)
	{
		return data == null ? resourceNotReady() : krkrRangeText(data, begin, data.size());
	}
}
