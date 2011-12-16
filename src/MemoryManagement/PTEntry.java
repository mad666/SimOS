package MemoryManagement;

import MainBoot.BootLoader;

public class PTEntry{
	private boolean pBit;
	private boolean rBit;
	private boolean mBit;
	
	//Konstruktor
	public PTEntry() {
		this.pBit = false;
		this.rBit = false;
		this.mBit = false;
	}

	//Setter & Getter
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
	
	public boolean getmBit() {
		return mBit;
	}

	public void setmBit(boolean mBit) {
		this.mBit = mBit;
	}
}
