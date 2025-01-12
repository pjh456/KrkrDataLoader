package KrkrDataLoader.network;

import KrkrDataLoader.core.KrkrData;
import KrkrDataLoader.core.KrkrScenes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/krkr/api")
public class KrkrServer
{
	private boolean fileIsReady = false;
	private KrkrData currentData = null;
	
	@PostMapping("/scene/upload/json")
	public ResponseEntity<Map<String,Object>> sceneJsonUpload(@RequestBody String jsonData)
	{
		try
		{
			currentData = new KrkrScenes(jsonData);
			fileIsReady = false;
			
			return KrkrResponseFactory.sceneFileUploadSuccess();
		}
		catch(Throwable e)
		{
			return KrkrResponseFactory.sceneFileUploadFailed(e.getMessage());
		}
	}
	
	@PostMapping("/scene/upload/file")
	public ResponseEntity<Map<String,Object>> sceneFileUpload(@RequestParam("file") MultipartFile file)
	{
		try
		{
			currentData = new KrkrScenes(file);
			fileIsReady = false;
			
			return KrkrResponseFactory.sceneFileUploadSuccess();
		}
		catch(Throwable e)
		{
			return KrkrResponseFactory.sceneFileUploadFailed(e.getMessage());
		}
	}
	
	@GetMapping("/scene/check")
	public ResponseEntity<Map<String,Object>> checkFileAvailable()
	{
		return KrkrResponseFactory.resourceReady("0d000721");
	}
	
	@GetMapping("/scene/info")
	public ResponseEntity<Map<String,Object>> getDataInfo(@RequestHeader(value = "Range", required = false) String range)
	{
		if(range == null) return KrkrResponseFactory.krkrRangeInfo(currentData);
		else
		{
			String[] rangeIndex = range.replace("-", " ").split(" ");
			
			if(rangeIndex.length < 2) return KrkrResponseFactory.error("Range value is valid!");
			
			return KrkrResponseFactory.krkrRangeInfo(currentData, Integer.decode(rangeIndex[0]), Integer.decode(rangeIndex[1]));
		}
	}
	
	@GetMapping("/scene/path")
	public ResponseEntity<Map<String,Object>> openPath(@RequestHeader(value = "Index", required = false) String index)
	{
		if(currentData == null) return KrkrResponseFactory.resourceNotReady();
		
		if(index == null)
		{
			if(currentData.parent == null) return KrkrResponseFactory.error("Data doesn't have parent!");
			else
			{
				currentData = currentData.parent;
				return KrkrResponseFactory.success("Go to parent data: " + currentData.name);
			}
		}
		else
		{
			try
			{
				int indexValue = Integer.decode(index);
				if(indexValue < 0 || indexValue >= currentData.size()) return KrkrResponseFactory.outOfRange();
				
				currentData = currentData.listChildren().get(indexValue);
				return KrkrResponseFactory.success("Go to child data: " + currentData.name);
			}
			catch(NumberFormatException e)
			{
				return KrkrResponseFactory.error(e.getMessage());
			}
		}
	}
	
	@GetMapping("/scene/text")
	public ResponseEntity<Map<String,Object>> getRangeText(@RequestHeader(value = "Range", required = false) String range)
	{
		if(range == null) return KrkrResponseFactory.krkrRangeText(currentData, 0);
		else
		{
			String[] rangeValue = range.replace("-", " ").split(" ");
			
			if(rangeValue.length < 2) return KrkrResponseFactory.error("Range value is valid!");
			
			try
			{
				return KrkrResponseFactory.krkrRangeText(currentData, Integer.decode(rangeValue[0]), Integer.decode(rangeValue[1]));
			}
			catch(NumberFormatException e)
			{
				return KrkrResponseFactory.error(e.getMessage());
			}
		}
	}
	
	@GetMapping("/greet/{name}")
	
	public String greet(@PathVariable String name)
	{
		return "Hello, " + name + "!";
	}
	
	
	@PostMapping("/greet")
	public String greetWithBody(@PathVariable String name)
	{
		return "Hello, " + name + "!";
	}
}
