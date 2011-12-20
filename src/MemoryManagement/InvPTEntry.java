package MemoryManagement;

public class InvPTEntry extends PTEntry {
	// Attribute
	private int pid;

	// Konstruktoren
	public InvPTEntry() {
		super();
		this.pid = -1;
	}

	public InvPTEntry(int pid) {
		super();
		this.pid = pid;
	}

	// Setter & Getter
	
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public int getPid() {
		return pid;
	}
}
