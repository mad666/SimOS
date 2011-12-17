package MemoryManagement;

public class PTEntry {
	// Attribute
	protected boolean pBit;
	protected boolean rBit;
	protected boolean mBit;
	protected int address;

	// Konstruktor
	public PTEntry() {
		this.pBit = false;
		this.rBit = false;
		this.mBit = false;
		this.address = -1;
	}

	// Setter & Getter
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

	public int getAddress() {
		return this.address;
	}

	public void setAddress(int address) {
		this.address = address;
	}
}
