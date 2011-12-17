package MemoryManagement;

public class InvPTEntry extends PTEntry {
	// Attribute
	private int pid;
	private int pageindex;

	// Konstruktoren
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

	// Setter & Getter
	public int getPid() {
		return pid;
	}

	public int getPageIndex() {
		return pageindex;
	}
}
