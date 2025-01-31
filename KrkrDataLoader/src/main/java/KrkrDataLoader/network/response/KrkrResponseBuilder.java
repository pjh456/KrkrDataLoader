package KrkrDataLoader.network.response;

public class KrkrResponseBuilder
{
	
	private final KrkrResponse response = new KrkrResponse();
	
	public KrkrResponse build() { return this.response; }
	
	public KrkrResponseBuilder setStatus(String status)
	{
		this.response.setStatus(status);
		return this;
	}
	
	public KrkrResponseBuilder setCode(int code)
	{
		this.response.setCode(code);
		return this;
	}
	
	public KrkrResponseBuilder setMessage(String message)
	{
		this.response.setMessage(message);
		return this;
	}
	
	public KrkrResponseBuilder setData(Object data)
	{
		this.response.setData(data);
		return this;
	}
	
}
