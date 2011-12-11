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
		this.size = size;
	}
	
	//Getter & Setter
	public Page getPage(int pageIndex) {
		return pages[pageIndex];
	}
	
}
