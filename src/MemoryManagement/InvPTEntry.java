package MemoryManagement;

public class InvPTEntry extends PTEntry {

	private int pid;
	private int pageindex;

	public InvPTEntry(int pid, int pageindex) {
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
