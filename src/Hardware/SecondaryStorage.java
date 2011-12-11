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
}
