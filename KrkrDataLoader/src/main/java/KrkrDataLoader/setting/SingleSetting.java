package KrkrDataLoader.setting;

public class SingleSetting
{
	private final String name;
	
	private Object state;
	
	public SingleSetting(String name, Object state)
	{
		this.name = name;
		this.state = state;
	}
	
	public SingleSetting(String name) { this(name, null); }
	
	public String getName() { return this.name; }
	
	public Object getState() { return this.state; }
	
	public void setState(Object state) { this.state = state; }
}
