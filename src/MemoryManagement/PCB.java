package MemoryManagement;

import Hardware.RegisterSet;

public class PCB {
	// Attribute
	private int pid;
	private int priority;
	private String state;
	private RegisterSet reg;
	//private PTEntry[] pageTable;
	private int storageIndex;

	// Konstruktor
	public PCB(int pid, int priority, String state) {
		this.priority = priority;
		this.state = state;
		this.pid = pid;
		reg = new RegisterSet();
	}

	// Setter & Getter
	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPid() {
		return this.pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public RegisterSet getRegisterSet() {
		return reg;
	}

	public PTEntry[] getPageTable() {
		return this.reg.getPageTable();
	}

	public void setPageTable(PTEntry[] pageTable) {
		this.reg.setPageTable(pageTable);
	}

	public int getStorageIndex() {
		return storageIndex;
	}

	public void setStorageIndex(int storageIndex) {
		this.storageIndex = storageIndex;
	}

	// Funktionen

	// einzelenen Eintrag der Seintentabelle ändern
	public void setPageTableEntry(PTEntry ptEntry, int index) {
		this.reg.getPageTable()[index] = ptEntry;
	}

	// einzelnen Eintrag der Seitentabelle lesen
	public PTEntry getPageTableEntry(int index) {
		return this.reg.getPageTable()[index];
	}

	// toString
	public String toString() {
		return "[pid " + pid + " priority " + priority + " base "
				+ reg.getBase() + " limit " + reg.getLimit() + "]";
	}

}
