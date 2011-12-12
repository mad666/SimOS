package MemoryManagement;

public class PageTable {
	public final static int MAXSIZE = 32;
	private int size;
	private Page[] pages;
	
	public void addPage(int pageIndex, int frameID) {
		
	}
	
	//Konstruktoren
	public PageTable(Page[] pages, int size) {
		//this.pages = pages.clone();
		//this.pages = pages;
		setSize(size);
	}
	
	//Getter & Setter
	public Page getPage(int pageIndex) {
		return pages[pageIndex];
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		if(size>MAXSIZE) this.size = MAXSIZE;	//auf maximale Gr��e beschr�nken
		else if(size<=0) this.size = 1;			//mindest Gr��e
		else this.size = size;
	}
	
	
}
