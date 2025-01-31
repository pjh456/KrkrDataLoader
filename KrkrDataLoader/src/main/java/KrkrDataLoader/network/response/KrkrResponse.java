package KrkrDataLoader.network.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Schema(description = "Standard response format for Krkr data loader API response.")
public class KrkrResponse
{
	@Schema(description = "Status of the response.")
	private String status;
	
	@Schema(description = "HTTP status code")
	private int code;
	
	@Schema(description = "Message of the response.")
	private String message;
	
	@Schema(description = "Data of the response.")
	private Object data;
	
	public KrkrResponse()
	{
		this.status = "success";
		this.code = 200;
		this.message = "default";
		this.data = null;
	}
	
	public KrkrResponse(String status, int code, String message, Object data) {
		this.status = status;
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	public ResponseEntity<Map<String,Object>> toResponse()
	{
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("status", status);
		responseBody.put("code", code);
		responseBody.put("message", message);
		responseBody.put("data", (data == null) ? Map.of() : data);
		
		return ResponseEntity.ok(responseBody);
	}
	
	public void setStatus(String status){this.status = status;}
	
	public String getStatus(){return this.status;}
	
	public void setCode(int code){this.code = code;}
	
	public int getCode(){return this.code;}
	
	public void setMessage(String message){this.message = message;}
	
	public String getMessage(){return this.message;}
	
	public void setData(Object data){this.data = data;}
	
	public Object getData(){return this.data;}
}
