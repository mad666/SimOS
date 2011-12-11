package MemoryManagement;

public class Page {
	public final static int PAGESIZE = 4;
	private int pageIndex;
	private int pid;
	private String[] pageContent;
	private boolean pBit;
	private boolean rBit;
	
	
	
	//Setter & Getter
	public String getPageContent(int index) {
		return pageContent[index];
	}
	public void setPageContent(String pageContent, int index) {
		this.pageContent[index] = pageContent;
	}
	public boolean ispBit() {
		return pBit;
	}
	public void setpBit(boolean pBit) {
		this.pBit = pBit;
	}
	public boolean isrBit() {
		return rBit;
	}
	public void setrBit(boolean rBit) {
		this.rBit = rBit;
	}
	public int getPid() {
		return pid;
	}

}
