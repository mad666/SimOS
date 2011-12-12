package Hardware;

import java.util.ArrayList;
import MemoryManagement.Page;

public class SecondaryStorage {
	ArrayList<Page[]> storage;
	
	//Element an Array anhängen
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
	
	//SeitenArray zu einem Prozess löschen
	public void deleteElementProc(int pid) {
		deleteElement(searchElement(pid));
	}
	
	//Getter & Setter
	public Page[] getStorage(int index) {
		return storage.get(index);
	}
	//SeitenArray an bestimmten Index einfügen
	public void setStorage(Page[] pages, int index) {
		storage.add(index, pages);
	}
}
