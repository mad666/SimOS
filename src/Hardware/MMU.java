// Ge�ndert von Sebastian S�ger und Max Richter

package Hardware;

import MainBoot.BootLoader;
import MainBoot.SysLogger;
import MemoryManagement.MemoryManager;
import MemoryManagement.PCB;
import MemoryManagement.PTEntry;

public class MMU {
	// Attribute
	private MainMemory memory;
	private RegisterSet regSet;
	private MemoryManager memoryManager;
	private PTEntry[] pageTable;



	// Innere Anonyme Klasse f�r Zugriffsfehler
	static public class AccessViolation extends Exception {
	};

	// Konstruktor
	/** Creates a new instance of MMU */
	public MMU(MainMemory memory) {
		this.memory = memory;
	}

	// Setter & Getter
	
	public void setRegisterSet(RegisterSet regSet) {
		this.regSet = regSet;
	}

	public void setMemoryManager(MemoryManager memoryManager) {
		this.memoryManager = memoryManager;
	}
	
	public void setPageTable(PTEntry[] pageTable) {
		this.pageTable = pageTable;
	}

	// Funktionen

	// einzelne Zeile in Haupspeicher schreiben
	// Adresse wird aufgel�st und an MemeoryManager �bergeben
	public void setMemoryCell(String address, String line)
			throws AccessViolation {
		setMemoryCell(Integer.parseInt(address), line);
	}

	// einzelne Zeile in Haupspeicher schreiben
	// Adresse wird aufgel�st und an MemeoryManager �bergeben
	public void setMemoryCell(int address, String line)
			throws AccessViolation {
		int index = address / BootLoader.PAGESIZE;
		int offset = address % BootLoader.PAGESIZE;
		if (!inMemory(pageTable, index)) {
			SysLogger.writeLog(0, "MMU.setMemoryCell: page fault, address: " + address + " [page: " + index + " offset: " + offset + "]");
			memoryManager.replacePage(index);
		} else {
			pageTable[index].setrBit(true);
		}
		pageTable[index].setmBit(true);
		memoryManager.setBitsWrite(pageTable[index].getAddress());
		int memAddress = (pageTable[index].getAddress() * BootLoader.PAGESIZE) + offset;
		memory.setContent(memAddress, line);
	}

	// einzelne Zeile aus Hauptspeicher lesen
	// Adresse wird aufgel�st und an MemeoryManager �bergeben
	public String getMemoryCell(String address) throws AccessViolation {
		return getMemoryCell(Integer.parseInt(address));
	}

	// einzelne Zeile aus Hauptspeicher lesen
	// Adresse wird aufgel�st und an MemeoryManager �bergeben
	public String getMemoryCell(int address) throws AccessViolation {
		int index = address / BootLoader.PAGESIZE;
		int offset = address % BootLoader.PAGESIZE;
		if (!inMemory(pageTable, index)) {
			SysLogger.writeLog(0, "MMU.getMemoryCell: page fault, address: " + address + " [page: " + index + " offset: " + offset + "]");
			memoryManager.replacePage(index);
		} else {
			pageTable[index].setrBit(true);
			memoryManager.setBitsRead(pageTable[index].getAddress());
		}
		int memAddress = (pageTable[index].getAddress() * BootLoader.PAGESIZE) + offset;
		return memory.getContent(memAddress);
	}

	// einzelne Zeile in Haupspeicher lesen
	// Adresse wird aufgel�st und an MemeoryManager �bergeben
	// fr�her musste die Adresse nicht mehr aufgel�st werden, da sie bereits im
	// Event bekannt war, ist auf Grund der neuen Speicherverwaltung zu
	// kompliziert
	public void setAbsoluteAddress(int address, String line) {
		try {
			setMemoryCell(address, line);
		} catch (AccessViolation e) {
			e.printStackTrace();
		}
	}

	// diese beiden Methoden werden in der CPU ben�tigt, l�sen die Adresse aber
	// nicht wirklich auf sondern geben nur den eingabewert zur�ck
	public int resolveAddress(String address) throws AccessViolation {
		return resolveAddress(Integer.parseInt(address));
	}

	public int resolveAddress(int address) throws AccessViolation {
		return address;
	}

	// pr�ft, ob sich die Seite in der sich die Zeile f�r den Zugriff befindet
	// im Hauptspeicher eingelagert ist
	public boolean inMemory(PTEntry[] pagetable, int index) {
		return pagetable[index].getpBit();
	}
}
