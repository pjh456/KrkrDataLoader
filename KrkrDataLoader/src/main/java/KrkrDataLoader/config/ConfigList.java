package KrkrDataLoader.config;

public class ConfigList
{
	
	private static JsonPath ScenesNamePath = null;
	private static JsonPath SceneNamePath = null;
	private static JsonPath ScenePath = null;
	private static JsonPath DialoguesPath = null;
	private static JsonPath SpeakerPath = null;
	private static JsonPath ContentPath = null;
	private static JsonPath VoicePath = null;
	
	
	public void setScenesNamePath(JsonPath path) { ScenesNamePath = path; }
	
	public JsonPath getScenesNamePath() { return ScenesNamePath; }
	
	public void setSceneNamePath(JsonPath path) { SceneNamePath = path; }
	
	public JsonPath getSceneNamePath() { return SceneNamePath; }
	
	public void setScenePath(JsonPath path) { ScenePath = path; }
	
	public JsonPath getScenePath() { return ScenePath; }
	
	public void setDialoguesPath(JsonPath path) { DialoguesPath = path; }
	
	public JsonPath getDialoguesPath() { return DialoguesPath; }
	
	public void setSpeakerPath(JsonPath path) { SpeakerPath = path; }
	
	public JsonPath getSpeakerPath() { return SpeakerPath; }
	
	public void setContentPath(JsonPath path) { ContentPath = path; }
	
	public JsonPath getContentPath() { return ContentPath; }
	
	public void setVoicePath(JsonPath path) { VoicePath = path; }
	
	public JsonPath getVoicePath() { return VoicePath; }
	
}
