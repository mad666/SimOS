package MemoryManagement;

import MainBoot.BootLoader;

public class PageTable {
	private int size;
	private int[] table;

	
	
	//Konstruktoren
	public PageTable(int size) {
		setSize(size);
		table = new int[this.size];
		//wenn Seite nicht eingelagert, dann -1
		for (int i = 0; i<this.size;i++) {
			table[i] = -1;
		}
	}
	
	
	
	//Getter & Setter
	public int getFrameID(int pageIndex) {
		return table[pageIndex];
	}
	public void setFrameID(int pageIndex, int frameID) {
		this.table[pageIndex] = frameID;
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		if(size>BootLoader.VIRTMEMSIZE) this.size = BootLoader.VIRTMEMSIZE;	//auf maximale Größe beschränken
		else if(size<=0) this.size = 1;			//mindest Größe
		else this.size = size;
	}
	
	
	
	//Funktionen
}
