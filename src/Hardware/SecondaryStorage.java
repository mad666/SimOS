package Hardware;

import java.util.ArrayList;

public class SecondaryStorage {
	// Attribute
	ArrayList<String[]> storage;

	// Konstruktoren

	/** Creates a new instance of SecondaryStorage */
	public SecondaryStorage() {
		this.storage = new ArrayList<String[]>();
	}

	public SecondaryStorage(int size) {
		this.storage = new ArrayList<String[]>(size);
	}

	// Getter & Setter
	public String[] getStorage(int index) {
		return storage.get(index);
	}

	// Funktionen

	// Element an bestimmten Index einfügen
	public void addElement(String[] lines, int index) {
		storage.add(index, lines);
	}

	// Element an Array anhängen
	public int addElement(String[] lines) {
		storage.add(lines);
		return storage.size() - 1;
	}

	// Element an bestimmtem Index entfernen
	public void deleteElement(int index) {
		storage.remove(index);
	}

	// einzelne Seite an über Index aktualisieren
	public void changeLine(int listIndex, int arrayIndex, String line) {
		getStorage(listIndex)[arrayIndex] = line;
	}

}
