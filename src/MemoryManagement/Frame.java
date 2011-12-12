package MemoryManagement;

public class Frame {
	private int frameID;
	private Page frameContent;
	
	public Frame(int frameID) {
		this.frameID = frameID;
		this.frameContent = null;
	}
	
	public Frame(int frameID, Page frameContent) {
		this.frameID = frameID;
		this.frameContent = frameContent.clone();
	}
	
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
