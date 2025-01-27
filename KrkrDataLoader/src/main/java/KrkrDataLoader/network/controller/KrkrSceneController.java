package KrkrDataLoader.network.controller;

import KrkrDataLoader.core.KrkrData;
import KrkrDataLoader.network.KrkrResponse;
import KrkrDataLoader.network.KrkrResponseFactory;
import KrkrDataLoader.network.RangeCallback;
import KrkrDataLoader.network.service.KrkrSceneFileService;
import KrkrDataLoader.network.service.KrkrSceneInfoService;
import KrkrDataLoader.network.service.KrkrSceneTextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/krkr/api/scene")
public class KrkrSceneController
{
	private final KrkrSceneFileService sceneFileService;
	
	private final KrkrSceneInfoService sceneInfoService;
	
	private final KrkrSceneTextService sceneTextService;
	
	public KrkrSceneController(
			KrkrSceneFileService sceneFileService,
			KrkrSceneInfoService sceneInfoService,
			KrkrSceneTextService sceneTextService
	)
	{
		this.sceneFileService = sceneFileService;
		this.sceneInfoService = sceneInfoService;
		this.sceneTextService = sceneTextService;
	}
	
	/**
	 * Upload scene file .
	 *
	 * @param taskId Unique identity of task. ( name of file now )
	 * @param file   Uploaded file. ( only supported json type )
	 *
	 * @return Http response to show if data is uploaded successfully.
	 */
	@PostMapping("/{taskId}/upload-file")
	public ResponseEntity<Map<String,Object>> sceneFileUpload(
			@PathVariable String taskId, @RequestParam("file") MultipartFile file
	)
	{
		return sceneFileService.handleSceneFileUpload(taskId, file) ?
				KrkrResponseFactory.sceneFileUploadSuccess().toResponse() :
				KrkrResponseFactory.sceneFileUploadFailed("File processing error.").toResponse();
	}
	
	/**
	 * Check if scene file is ready.
	 * @param taskId Unique identity of task. ( name of file now )
	 * @return Http response to show if data is ready.
	 */
	@GetMapping("/{taskId}/check")
	public ResponseEntity<Map<String,Object>> checkFileAvailable(@PathVariable String taskId)
	{
		return sceneFileService.isSceneReady(taskId) ?
				KrkrResponseFactory.resourceReady(taskId).toResponse() :
				KrkrResponseFactory.resourceNotReady().toResponse();
	}
	
	/**
	 * Get info of scene file.
	 * @param taskId  Unique identity of task. ( name of file now )
	 * @param index Index of data's child.
	 * @return Http response that contains info of scene file. ( data name and number of children )
	 */
	@GetMapping("/{taskId}/info")
	public ResponseEntity<Map<String,Object>> getInfo(
			@PathVariable String taskId, @RequestHeader(value = "Index", required = false) String index
	)
	{
		KrkrData currentData = sceneFileService.getScene(taskId);
		
		try{ currentData = index == null ? currentData : getDataChild(currentData, index); }
		catch(NumberFormatException e){ return KrkrResponseFactory.unsupportedType().toResponse(); }
		catch(IndexOutOfBoundsException e){ return KrkrResponseFactory.outOfRange().toResponse(); }
		
		return sceneInfoService.krkrInfo(currentData).toResponse();
	}
	
	/**
	 * Get range info of scene file.
	 * @param taskId  Unique identity of task. ( name of file now )
	 * @param range Range of data's children.
	 * @param index Index of data's child.
	 * @return Http response that contains info of scene file. ( data name and number of children )
	 */
	@GetMapping("/{taskId}/range-info")
	public ResponseEntity<Map<String,Object>> getRangeInfo(
			@PathVariable String taskId,
			@RequestHeader(value = "Range", required = false) String range,
			@RequestHeader(value = "Index", required = false) String index
	)
	{
		KrkrData currentData = sceneFileService.getScene(taskId);
		
		try{ currentData = index == null ? currentData : getDataChild(currentData, index); }
		catch(NumberFormatException e){ return KrkrResponseFactory.unsupportedType().toResponse(); }
		catch(IndexOutOfBoundsException e){ return KrkrResponseFactory.outOfRange().toResponse(); }
		
		return processRangeData(currentData, range, new RangeCallback()
		{
			@Override
			public KrkrResponse callback(Object data, int begin, int end)
			{
				return sceneInfoService.krkrRangeInfo((KrkrData) data, begin, end);
			}
		}).toResponse();
	}
	
	/**
	 * Get range text of scene(or its child) dialogues.
	 * @param taskId  Unique identity of task. ( name of file now )
	 * @param range Range of data's dialogues.
	 * @param index Index of data's child.
	 * @return Http response that contains text of scene data.
	 */
	@GetMapping("/{taskId}/text")
	public ResponseEntity<Map<String,Object>> getRangeText(
			@PathVariable String taskId,
			@RequestHeader(value = "Range", required = false) String range,
			@RequestHeader(value = "Index", required = false) String index
	)
	{
		KrkrData currentData = sceneFileService.getScene(taskId);
		
		try{ currentData = index == null ? currentData : getDataChild(currentData, index); }
		catch(NumberFormatException e){ return KrkrResponseFactory.unsupportedType().toResponse(); }
		catch(IndexOutOfBoundsException e){ return KrkrResponseFactory.outOfRange().toResponse(); }
		
		return processRangeData(currentData, range, new RangeCallback()
		{
			@Override
			public KrkrResponse callback(Object data, int begin, int end)
			{
				return sceneTextService.krkrRangeText((KrkrData) data, begin, end);
			}
		}).toResponse();
	}
	
	/**
	 * Get child of data by index. ( only one step )
	 *
	 * @param data  KrkrData. ( formatted scene data )
	 * @param index An integer index of child.
	 *
	 * @return Pointed Child of data.
	 *
	 * @throws IndexOutOfBoundsException If index is out of range of data's children.
	 */
	private KrkrData getDataChild(KrkrData data, int index)
			throws IndexOutOfBoundsException
	{ return data.getChild(index); }
	
	/**
	 * Get child of data by index. ( any steps )
	 *
	 * @param data  KrkrData. ( formatted scene data )
	 * @param index String array of index of child.
	 *
	 * @return Pointed Child of data.
	 *
	 * @throws NumberFormatException     If index[n] is not integer.
	 * @throws IndexOutOfBoundsException If index[n] is out of range of data's children.
	 */
	private KrkrData getDataChild(KrkrData data, String[] index)
			throws NumberFormatException, IndexOutOfBoundsException
	{
		for(String childIndex: index)
		{ data = getDataChild(data, Integer.decode(childIndex)); }
		return data;
	}
	
	/**
	 * Get child of data by index. ( any steps )
	 *
	 * @param data  KrkrData. ( formatted scene data )
	 * @param index A string of index of child.
	 *
	 * @return Pointed Child of data.
	 *
	 * @throws NumberFormatException     If index is not be composed of integers and ','.
	 * @throws IndexOutOfBoundsException If one of index is out of range of data's children.
	 */
	private KrkrData getDataChild(KrkrData data, String index)
			throws NumberFormatException, IndexOutOfBoundsException
	{ return getDataChild(data, index.split(",")); }
	
	/**
	 * Handle range data using callback.
	 *
	 * @param data          Data need to be handled.
	 * @param range         Range of data.
	 * @param rangeCallback Callback function used to handle data.
	 *
	 * @return handled range data.
	 */
	public KrkrResponse processRangeData(Object data, String range, RangeCallback rangeCallback)
	{
		if(range == null) return rangeCallback.callback(data);
		else
		{
			String[] rangeValue = range.split("-");
			
			try
			{
				if(rangeValue.length == 1) return rangeCallback.callback(data,
						Integer.decode(rangeValue[0]),
						Integer.decode(rangeValue[0]) + 1
				);
				else return rangeCallback.callback(data, Integer.decode(rangeValue[0]), Integer.decode(rangeValue[1]));
			}
			catch(NumberFormatException e){ return KrkrResponseFactory.unsupportedType(); }
		}
	}
}
