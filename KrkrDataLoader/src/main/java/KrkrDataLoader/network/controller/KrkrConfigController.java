package KrkrDataLoader.network.controller;

import KrkrDataLoader.network.KrkrResponse;
import KrkrDataLoader.network.KrkrResponseBuilder;
import KrkrDataLoader.network.service.KrkrConfigFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
		name = "Config API",
		description = "APIs for loading and setting config file data. ( .json )"
)
@RestController
@CrossOrigin(
		origins = "*",
		allowedHeaders = "*"
)
@RequestMapping("/krkr/api/config")
//TODO: 重构 Config 相关类
public class KrkrConfigController
{
	private final KrkrConfigFileService configFileService;
	
	public KrkrConfigController(KrkrConfigFileService configFileService)
	{
		this.configFileService = configFileService;
	}
	
	@PostMapping("/{taskId}/upload-file")
	public KrkrResponse configFileUpload(
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
		
		return configFileService.handleConfigFileUpload(taskId, file) ?
			   new KrkrResponseBuilder().setStatus("success").setCode(200).setMessage(
					   "Config file uploaded successfully, parsing has started.").build() :
			   new KrkrResponseBuilder().setStatus("failed").setCode(400).setMessage(
					   "File processing error.").build();
	}
}
