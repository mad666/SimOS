package MemoryManagement;

public class Frame {
	public final static int FRAMESIZE = 4;
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
