package MemoryManagement;

public class Frame {
	private int frameID;
	private Page frameContent;
	
	
	
	//Setter & Getter
	public Page getFrameContent() {
		return frameContent;
	}
	public void setFrameContent(Page frameContent) {
		this.frameContent = frameContent;
	}
	public int getFrameID() {
		return frameID;
	}
	
}
