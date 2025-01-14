package KrkrDataLoader.network;

import KrkrDataLoader.core.KrkrData;
import KrkrDataLoader.core.KrkrScenes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/krkr/api")
public class KrkrServer
{
	private final Map<String,KrkrScenes> sceneMap = new LinkedHashMap<>();
	
	@PostMapping("/scene/{taskId}/upload-file")
	public ResponseEntity<Map<String,Object>> sceneFileUpload(
			@PathVariable String taskId, @RequestParam("file") MultipartFile file
	)
	{
		try
		{
			KrkrScenes newScene = new KrkrScenes(file, false);
			sceneMap.put(taskId, newScene);
			Thread thread = new Thread(() ->
			{
				try{ newScene.initialize(); }
				catch(Throwable ignored){ }
			});
			thread.start();
			
			return KrkrResponseFactory.sceneFileUploadSuccess();
		}
		catch(Throwable e)
		{
			return KrkrResponseFactory.sceneFileUploadFailed(e.getMessage());
		}
	}
	
	@GetMapping("/scene/{taskId}/check")
	public ResponseEntity<Map<String,Object>> checkFileAvailable(@PathVariable String taskId)
	{
		try
		{
			return sceneMap.get(taskId).isInit() ?
					KrkrResponseFactory.resourceReady(taskId) :
					KrkrResponseFactory.resourceNotReady();
		}
		catch(NullPointerException e){ return KrkrResponseFactory.resourceNotReady(); }
	}
	
	@GetMapping("/scene/{taskId}/info")
	public ResponseEntity<Map<String,Object>> getDataInfo(
			@PathVariable String taskId, @RequestHeader(value = "Range", required = false) String range
	)
	{
		KrkrScenes currentData = sceneMap.get(taskId);
		
		if(range == null) return KrkrResponseFactory.krkrRangeInfo(currentData);
		else
		{
			String[] rangeIndex = range.replace("-", " ").split(" ");
			
			try
			{
				if(rangeIndex.length == 1) return KrkrResponseFactory.krkrRangeInfo(currentData,
						Integer.decode(rangeIndex[0]),
						Integer.decode(rangeIndex[0]) + 1
				);
				else return KrkrResponseFactory.krkrRangeInfo(currentData,
						Integer.decode(rangeIndex[0]),
						Integer.decode(rangeIndex[1])
				);
			}
			catch(NumberFormatException e){ return KrkrResponseFactory.unsupportedType(); }
		}
	}
	
	@GetMapping("/scene/{taskId}/text")
	public ResponseEntity<Map<String,Object>> getRangeText(
			@PathVariable String taskId,
			@RequestHeader(value = "Range", required = false) String range,
			@RequestHeader(value = "Index", required = false) String index
	)
	{
		KrkrData currentData = sceneMap.get(taskId);
		
		try
		{
			if(index != null)
			{
				for(String childIndex: index.split(","))
				{ currentData = currentData.getChild(Integer.decode(childIndex)); }
			}
		}
		catch(NumberFormatException e){ return KrkrResponseFactory.unsupportedType(); }
		catch(IndexOutOfBoundsException e){ return KrkrResponseFactory.outOfRange(); }
		
		if(range == null) return KrkrResponseFactory.krkrRangeText(currentData, 0);
		else
		{
			String[] rangeValue = range.split("-");
			
			try
			{
				if(rangeValue.length == 1) return KrkrResponseFactory.krkrRangeText(currentData,
						Integer.decode(rangeValue[0]),
						Integer.decode(rangeValue[0]) + 1
				);
				else return KrkrResponseFactory.krkrRangeText(currentData,
						Integer.decode(rangeValue[0]),
						Integer.decode(rangeValue[1])
				);
			}
			catch(NumberFormatException e){ return KrkrResponseFactory.unsupportedType(); }
		}
	}
	
	@GetMapping("/greet/{name}")
	public String greet(@PathVariable String name) { return "Hello, " + name + "!"; }
	
	@PostMapping("/greet")
	public String greetWithBody(@PathVariable String name) { return "Hello, " + name + "!"; }
}
