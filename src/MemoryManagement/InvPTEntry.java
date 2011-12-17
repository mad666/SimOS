package MemoryManagement;

public class InvPTEntry extends PTEntry {

	private int pid;
	private int pageindex;
	
	public InvPTEntry() {
		super();
		this.pid = -2;
		this.pageindex = -1;
	}
	
	public InvPTEntry(int pid, int pageindex) {
		super();
		this.pid = pid;
		this.pageindex = pageindex;
	}

	public int getPid() {
		return pid;
	}

	public int getPageIndex() {
		return pageindex;
	}
}
