package Hardware;

import java.util.ArrayList;
import MemoryManagement.Page;

public class SecondaryStorage {
	
	ArrayList<Page[]> storage;
	
	
	
	//Konstruktoren
	public SecondaryStorage() {
		this.storage = new ArrayList<Page[]>();
	}
	public SecondaryStorage(int size) {
		this.storage = new ArrayList<Page[]>(size);
	}
	
	
	
	
	//Getter & Setter
	public Page[] getStorage(int index) {
		return storage.get(index);
	}
	//SeitenArray an bestimmten Index einf�gen
	public void setStorage(Page[] pages, int index) {
		storage.add(index, pages);
	}
	
	
	
	//Funktionen
	//Element an Array anh�ngen
	public void addElement(Page[] pages) {
		storage.add(pages);
	}
	
	//SeitenArray zu einem Prozess suchen (anhand der pid)
	public int searchElement(int pid) {
		for(int i = 0; i< storage.size();i++) {		
			if(pid==storage.get(i)[0].getPid()) return i;	//an Index i gefunden
		}
		return -1;	//nicht gefunden
	}
	
	//SeitenArray an Index i entfernen
	public void deleteElement(int index) {
		storage.remove(index);
	}
	
	//SeitenArray zu einem Prozess l�schen
	public void deleteElementByPid(int pid) {
		deleteElement(searchElement(pid));
	}
	
	//einzelne Seite aktualisieren �ber pid
	public void changePageByPid(int pid, int index, Page page) {
		changePageByIndex( searchElement(pid), index, page);
	}
	//einzelne Seite aktualisieren �ber index
	public void changePageByIndex(int listIndex, int arrayIndex, Page page ) {
		getStorage(listIndex)[arrayIndex] = page;
	}
	
	
}
