package KrkrDataLoader.network.controller;

import KrkrDataLoader.core.KrkrData;
import KrkrDataLoader.network.response.KrkrResponse;
import KrkrDataLoader.network.response.KrkrResponseBuilder;
import KrkrDataLoader.network.RangeCallback;
import KrkrDataLoader.network.service.KrkrSceneFileService;
import KrkrDataLoader.network.service.KrkrSceneInfoService;
import KrkrDataLoader.network.service.KrkrSceneTextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
		name = "Scene API",
		description = "APIs for loading and parsing scene file data. ( .json )"
)
@RestController
@CrossOrigin(
		origins = "*",
		allowedHeaders = "*"
)
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
	@Operation(
			summary = "Upload scene file",
			description = " loading a parsed scene data."
	)
	@Parameters(
			value = {
					@Parameter(
							name = "taskId",
							description = "Unique id of uploaded file.",
							required = true,
							in = ParameterIn.PATH
					),
					@Parameter(
							name = "file",
							description = "Scene file, only accept .json file.",
							required = true,
							in = ParameterIn.QUERY
					)
			}
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "Success",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"success\", " +
													  "\"code\": 200, " +
													  "\"message\": \"Scene file uploaded successfully, parsing has started.\"" +
													  "}"
									)
							)
					),
					@ApiResponse(
							responseCode = "400",
							description = "Failure",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"failed\", " +
													  "\"code\": 400, " +
													  "\"message\": \"File processing error.\"" +
													  "}"
									)
							)
					),
					@ApiResponse(
							responseCode = "415",
							description = "Failure",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"failed\", " +
													  "\"code\": 415, " +
													  "\"message\": \"No file uploaded. / Invalid file format. Only .json files are allowed. / Invalid content type. Only JSON files are accepted.\"" +
													  "}"
									)
							)
					)
			}
	)
	@PostMapping("/{taskId}/upload-file")
	public KrkrResponse sceneFileUpload(
			@PathVariable String taskId, @RequestParam("file") MultipartFile file
	)
	{
		// 检查文件是否为空
		if(file.isEmpty())
		{
			return new KrkrResponseBuilder().setStatus("failed").setCode(415).setMessage(
					"No file uploaded.").build();
		}
		
		// 检查文件类型是否为 JSON
		String fileName = file.getOriginalFilename();
		if(fileName == null || ! fileName.endsWith(".json"))
		{
			return new KrkrResponseBuilder().setStatus("failed").setCode(415).setMessage(
					"Invalid file format. Only .json files are allowed.").build();
		}
		
		// 检查 MIME 类型是否为 JSON
		String contentType = file.getContentType();
		if(contentType == null || ! contentType.equals("application/json"))
		{
			return new KrkrResponseBuilder().setStatus("failed").setCode(415).setMessage(
					"Invalid content type. Only JSON files are accepted.").build();
		}
		
		return sceneFileService.handleSceneFileUpload(taskId, file) ?
			   new KrkrResponseBuilder().setStatus("success").setCode(200).setMessage(
					   "Scene file uploaded successfully, parsing has started.").build() :
			   new KrkrResponseBuilder().setStatus("failed").setCode(400).setMessage(
					   "File processing error.").build();
	}
	
	/**
	 * Check if scene file is ready.
	 *
	 * @param taskId Unique identity of task. ( name of file now )
	 *
	 * @return Http response to show if data is ready.
	 */
	@Operation(
			summary = "Check if scene file is ready",
			description = "Check if the scene file is ready for use."
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "Resource is ready",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"success\", " +
													  "\"code\": 200, " +
													  "\"message\": \"Resource is ready, please get it.\", " +
													  "\"data\": \"taskId\"}"
									)
							)
					),
					@ApiResponse(
							responseCode = "202",
							description = "Resource not ready",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"accepted\", " +
													  "\"code\": 202, " +
													  "\"message\": \"Resource is not ready, please try again later.\"}"
									)
							)
					)
			}
	)
	@GetMapping("/{taskId}/check")
	public KrkrResponse checkFileAvailable(@PathVariable String taskId)
	{
		return sceneFileService.isSceneReady(taskId) ?
			   new KrkrResponseBuilder().setStatus("success").setCode(200).setMessage(
					   "Resource is ready, please get it.").setData(taskId).build() :
			   new KrkrResponseBuilder().setStatus("accepted").setCode(202).setMessage(
					   "Resource is not ready, please try again later.").build();
	}
	
	
	/**
	 * Get info of scene file.
	 *
	 * @param taskId Unique identity of task. ( name of file now )
	 * @param index  Index of data's child.
	 *
	 * @return Http response that contains info of scene file. ( data name and number of children )
	 */
	@Operation(
			summary = "Get info of scene file",
			description = "Retrieve information about the scene file, including data name and number of children."
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "Scene file information.",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"success\", " +
													  "\"code\": 200, " +
													  "\"message\": \"Scene file info retrieved.\", " +
													  "\"data\": {" +
													  "\"name\": \"exampleScene\", " +
													  "\"scene_size\": 3" +
													  "}}"
									)
							)
					),
					@ApiResponse(
							responseCode = "202",
							description = "Resource is not ready.",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"accepted\", " +
													  "\"code\": 202, " +
													  "\"message\": \"Resource is not ready, please try again later.\"}"
									)
							)
					),
					@ApiResponse(
							responseCode = "416",
							description = "Invalid request",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"failed\", " +
													  "\"code\": 416, " +
													  "\"message\": \"Type of index is not supported! / Index is out of range!\"}"
									)
							)
					)
			}
	)
	@GetMapping("/{taskId}/info")
	public KrkrResponse getInfo(
			@PathVariable String taskId, @RequestHeader(
			value = "Index",
			required = false
	) String index
	)
	{
		KrkrData currentData = sceneFileService.getScene(taskId);
		
		try
		{
			currentData = index == null ? currentData : getDataChild(currentData, index);
		}
		catch(NumberFormatException e)
		{
			return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage(
					"Type of index is not supported!").build();
		}
		catch(IndexOutOfBoundsException e)
		{
			return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage(
					"Index is out of range!").build();
		}
		
		return sceneInfoService.krkrInfo(currentData);
	}
	
	
	/**
	 * Get range info of scene file.
	 *
	 * @param taskId Unique identity of task. ( name of file now )
	 * @param range  Range of data's children.
	 * @param index  Index of data's child.
	 *
	 * @return Http response that contains info of scene file. ( data name and number of children )
	 */
	@Operation(
			summary = "Get range info of scene file",
			description = "Retrieve range information from the scene file, including the name and number of children."
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "The information of range parsed data",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"success\", " +
													  "\"code\": 200, " +
													  "\"message\": \"Range info retrieved.\", " +
													  "\"data\": [" +
													  "{" +
													  "\"name\": \"exampleScene1\", " +
													  "\"scene_size\": 5" +
													  "}, " +
													  "{" +
													  "\"name\": \"exampleScene2\", " +
													  "\"scene_size\": 7" +
													  "}" +
													  "]" +
													  "}"
									)
							)
					),
					@ApiResponse(
							responseCode = "202",
							description = "Resource not ready",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"accepted\", " +
													  "\"code\": 202, " +
													  "\"message\": \"Resource is not ready, please try again later.\"}"
									)
							)
					),
					@ApiResponse(
							responseCode = "416",
							description = "Invalid request",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"failed\", " +
													  "\"code\": 416, " +
													  "\"message\": \"Request is out of range!\"}"
									)
							)
					)
			}
	)
	@GetMapping("/{taskId}/range-info")
	public KrkrResponse getRangeInfo(
			@PathVariable String taskId, @RequestHeader(
			value = "Range",
			required = false
	) String range, @RequestHeader(
			value = "Index",
			required = false
	) String index
	)
	{
		KrkrData currentData = sceneFileService.getScene(taskId);
		
		try
		{
			currentData = index == null ? currentData : getDataChild(currentData, index);
		}
		catch(NumberFormatException e)
		{
			return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage(
					"Type is not supported!").build();
		}
		catch(IndexOutOfBoundsException e)
		{
			return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage(
					"Request is out of range!").build();
		}
		
		//TODO: 需要具体写明返回格式，修改前面注解中的Content
		return processRangeData(currentData, range, new RangeCallback()
		{
			@Override
			public KrkrResponse callback(Object data, int begin, int end)
			{
				return sceneInfoService.krkrRangeInfo((KrkrData) data, begin, end);
			}
		});
	}
	
	
	/**
	 * Get range text of scene(or its child) dialogues.
	 *
	 * @param taskId Unique identity of task. ( name of file now )
	 * @param range  Range of data's dialogues.
	 * @param index  Index of data's child.
	 *
	 * @return Http response that contains text of scene data.
	 */
	@Operation(
			summary = "Get range text of scene (or its child) dialogues",
			description = "Retrieve range of text data from the scene or its child dialogues."
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "Range text retrieved successfully",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"success\", " +
													  "\"code\": 200, " +
													  "\"message\": \"Range text retrieved.\", " +
													  "\"data\":  [" +
													  "\"【exampleSpeaker1】「exampleContent1」\", " +
													  "\"「exampleContent2」\", " +
													  "\"【exampleSpeaker3】「exampleContent3」\"" +
													  "]}"
									)
							)
					),
					@ApiResponse(
							responseCode = "202",
							description = "Resource not ready",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"accepted\", " +
													  "\"code\": 202, " +
													  "\"message\": \"Resource is not ready, please try again later.\"}"
									)
							)
					),
					@ApiResponse(
							responseCode = "416",
							description = "Invalid request",
							content = @Content(
									mediaType = "application/json",
									schema = @Schema(
											type = "object",
											example = "{" +
													  "\"status\": \"failed\", " +
													  "\"code\": 416, " +
													  "\"message\": \"Type of index is not supported! / Index is out of range! / Request is out of range!\"}"
									)
							)
					)
			}
	)
	@GetMapping("/{taskId}/text")
	public KrkrResponse getRangeText(
			@PathVariable String taskId, @RequestHeader(
			value = "Range",
			required = false
	) String range, @RequestHeader(
			value = "Index",
			required = false
	) String index
	)
	{
		KrkrData currentData = sceneFileService.getScene(taskId);
		
		try
		{
			currentData = index == null ? currentData : getDataChild(currentData, index);
		}
		catch(NumberFormatException e)
		{
			return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage(
					"Type of index is not supported!").build();
		}
		catch(IndexOutOfBoundsException e)
		{
			return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage(
					"Index is out of range!").build();
		}
		
		return processRangeData(currentData, range, new RangeCallback()
		{
			@Override
			public KrkrResponse callback(Object data, int begin, int end)
			{
				return sceneTextService.krkrRangeText((KrkrData) data, begin, end);
			}
		});
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
	{ return (KrkrData)data.getChild(index); }
	
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
		if(range == null) { return rangeCallback.callback(data); }
		else
		{
			String[] rangeValue = range.split("-");
			
			try
			{
				if(rangeValue.length == 1)
				{
					return rangeCallback.callback(data,
												  Integer.decode(rangeValue[0]),
												  Integer.decode(rangeValue[0]) + 1
					);
				}
				else
				{
					return rangeCallback.callback(data,
												  Integer.decode(rangeValue[0]),
												  Integer.decode(rangeValue[1])
					);
				}
			}
			catch(NumberFormatException e)
			{
				return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage(
						"Type is not supported!").build();
			}
		}
	}
}
