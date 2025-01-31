package KrkrDataLoader.network.response;

public class KrkrResponseFactory
{
	public static KrkrResponse success(String message)
	{
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(200)
										.setMessage("Request succeeded: " + message)
										.build();
	}
	
	public static KrkrResponse error(String message)
	{
		return new KrkrResponseBuilder().setStatus("failed")
										.setCode(400)
										.setMessage("Request failed because: " + message)
										.build();
	}
	
	public static KrkrResponse sceneFileUploadSuccess()
	{
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(200)
										.setMessage("Scene file uploaded successfully, parsing has started.")
										.build();
	}
	
	public static KrkrResponse resourceNotReady()
	{
		return new KrkrResponseBuilder().setStatus("accepted")
										.setCode(202)
										.setMessage("Resource is not ready, please try again later.")
										.build();
	}
	
	public static KrkrResponse resourceReady(String taskId)
	{
		return new KrkrResponseBuilder().setStatus("success")
										.setCode(200)
										.setMessage("Resource is ready, please get it.")
										.setData(taskId)
										.build();
	}
	
	public static KrkrResponse outOfRange()
	{
		return new KrkrResponseBuilder().setStatus("Failed")
										.setCode(416)
										.setMessage("Request is out of range!")
										.build();
	}
	
	public static KrkrResponse unsupportedType()
	{
		return new KrkrResponseBuilder().setStatus("Failed").setCode(416).setMessage("Type is not supported!").build();
	}
	
}
