package KrkrDataLoader.network;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public class KrkrResponseBuilder
{
	private String status = "success";
	private int code = 200;
	private String message = "default";
	private Object data = null;
	
	public ResponseEntity<Map<String,Object>> build()
	{
		Map<String,Object> responseBody = Map.of("status",
				status,
				"code",
				code,
				"message",
				message,
				"data",
				( data == null ) ? Map.of() : data
		);
		
		return ResponseEntity.ok(responseBody);
	}
	
	public KrkrResponseBuilder setStatus(String status)
	{
		this.status = status;
		return this;
	}
	
	public KrkrResponseBuilder setCode(int code)
	{
		this.code = code;
		return this;
	}
	
	public KrkrResponseBuilder setMessage(String message)
	{
		this.message = message;
		return this;
	}
	
	public KrkrResponseBuilder setData(Object data)
	{
		this.data = data;
		return this;
	}
	
}
