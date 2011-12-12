package MemoryManagement;

public class Page {
	public final static int PAGESIZE = 4;
	private int pageIndex;
	private int pid;
	private String[] pageContent;
	private boolean pBit;
	private boolean rBit;
	
	
	
	//Konstruktoren
	public Page(int pageIndex, int pid, boolean pBit, boolean rBit,
			String[] pageContent) {
		this.pageIndex = pageIndex;
		this.pid = pid;
		this.pBit = pBit;
		this.rBit = rBit;
		this.pageContent = pageContent;
	}
	public Page(int pageIndex, int pid, boolean pBit, boolean rBit) {
		this.pageIndex = pageIndex;
		this.pid = pid;
		this.pBit = pBit;
		this.rBit = rBit;
	}
	public Page(int pageIndex, int pid, String[] pageContent) {
		this.pageIndex = pageIndex;
		this.pid = pid;
		this.pBit = false;
		this.rBit = false;
		this.pageContent = pageContent;
	}
	public Page(int pageIndex, int pid) {
		this.pageIndex = pageIndex;
		this.pid = pid;
		this.pBit = false;
		this.rBit = false;
	}


	
	//Setter & Getter
	public String getPageContent(int index) {
		return pageContent[index];
	}
	public String[] getPageContent() {
		return pageContent;
	}
	public void setPageContent(String pageContent, int index) {
		this.pageContent[index] = pageContent;
	}
	public void setPageContent(String[] pageContent) {
		this.pageContent = pageContent;
	}
	
	public boolean getpBit() {
		return pBit;
	}
	public void setpBit(boolean pBit) {
		this.pBit = pBit;
	}
	
	public boolean getrBit() {
		return rBit;
	}
	public void setrBit(boolean rBit) {
		this.rBit = rBit;
	}
	
	public int getPid() {
		return pid;
	}

}
