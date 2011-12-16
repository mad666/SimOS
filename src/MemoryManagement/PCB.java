/*
 * PCB.java
 *
 * Created on September 1, 2007, 12:28 PM
 *
 */

package MemoryManagement;

import Hardware.RegisterSet;

public class PCB {

	private int pid;
	private int priority;
	private String state;
	private RegisterSet reg;
	private PTEntry[] pageTable;
	private int storageIndex;

	// Konstruktoren
	
	public PCB(int pid, int priority, String state, PTEntry[] pageTable) {
		this.priority = priority;
		this.state = state;
		this.pid = pid;
		reg = new RegisterSet();
		this.pageTable = pageTable;
	}

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
		return pageTable;
	}

	public void setPageTable(PTEntry[] pageTable) {
		this.pageTable = pageTable;
	}
	
	public void setPageTableEntry(PTEntry ptEntry, int index) {
		
	}
	
	public PTEntry getPagteTableEntry(int index) {
		PTEntry ptentry = null;
		return ptentry;
	}

	public int getStorageIndex() {
		return storageIndex;
	}

	public void setStorageIndex(int storageIndex) {
		this.storageIndex = storageIndex;
	}

	// toString
	public String toString() {
		return "[pid " + pid + " priority " + priority + " base "
				+ reg.getBase() + " limit " + reg.getLimit() + "]";
	}

}
