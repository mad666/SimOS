package Hardware;

import MainBoot.BootLoader;
import MainBoot.SysLogger;
import MemoryManagement.MemoryManager;
import MemoryManagement.PCB;
import MemoryManagement.PTEntry;

public class MMU {
	// Attribute
	private MemoryManager memoryManager;
	private MainMemory memory;
	private RegisterSet regSet;

	// Innere Anonyme Klasse für Zugriffsfehler
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

	// Funktionen

	// einzelne Zeile in Haupspeicher schreiben
	// Adresse wird aufgelöst und an MemeoryManager übergeben
	public void setMemoryCell(String address, String line, PCB pcb)
			throws AccessViolation {
		setMemoryCell(Integer.parseInt(address), line, pcb);
	}

	// einzelne Zeile in Haupspeicher schreiben
	// Adresse wird aufgelöst und an MemeoryManager übergeben
	public void setMemoryCell(int address, String line, PCB pcb)
			throws AccessViolation {
//		if (address < 0 || address > regSet.getLimit()) {
//			SysLogger.writeLog(0, "MMU.setMemoryCell: access violation: "
//					+ address);
//			throw new AccessViolation();
//		}
		int index = address / BootLoader.PAGESIZE;
		int offset = address % BootLoader.PAGESIZE;
		if (!inMemory(pcb.getPageTable(), index)) {
			SysLogger.writeLog(0, "MMU.setMemoryCell: page fault for process: " + pcb.getPid() + " address: " + address + " [page: " + index + " offset: " + offset + "]");
			memoryManager.replacePage(index, pcb.getPid());
		} else {
			pcb.getPageTableEntry(index).setrBit(true);
		}
		pcb.getPageTableEntry(index).setmBit(true);
		memoryManager.setBitsWrite(pcb.getPageTableEntry(index).getAddress());
		int memAddress = (pcb.getPageTableEntry(index).getAddress() * BootLoader.PAGESIZE) + offset;
		memory.setContent(memAddress, line);
	}

	// einzelne Zeile aus Hauptspeicher lesen
	// Adresse wird aufgelöst und an MemeoryManager übergeben
	public String getMemoryCell(String address, PCB pcb) throws AccessViolation {
		return getMemoryCell(Integer.parseInt(address), pcb);
	}

	// einzelne Zeile aus Hauptspeicher lesen
	// Adresse wird aufgelöst und an MemeoryManager übergeben
	public String getMemoryCell(int address, PCB pcb) throws AccessViolation {
//		if (address < 0 || address > regSet.getLimit()) {
//			SysLogger.writeLog(0, "MMU.getMemoryCell: access violation: "
//					+ address);
//			throw new AccessViolation();
//		}
		int index = address / BootLoader.PAGESIZE;
		int offset = address % BootLoader.PAGESIZE;
		if (!inMemory(pcb.getPageTable(), index)) {
			SysLogger.writeLog(0, "MMU.getMemoryCell: page fault for process: " + pcb.getPid() + " address: " + address + " [page: " + index + " offset: " + offset + "]");
			memoryManager.replacePage(index, pcb.getPid());
		} else {
			pcb.getPageTableEntry(index).setrBit(true);
			memoryManager.setBitsRead(pcb.getPageTableEntry(index).getAddress());
		}
		int memAddress = (pcb.getPageTableEntry(index).getAddress() * BootLoader.PAGESIZE) + offset;
		return memory.getContent(memAddress);
	}

	// einzelne Zeile in Haupspeicher lesen
	// Adresse wird aufgelöst und an MemeoryManager übergeben
	// früher musste die Adresse nicht mehr aufgelöst werden, da sie bereits im
	// Event bekannt war, ist auf Grund der neuen Speicherverwaltung zu
	// kompliziert
	public void setAbsoluteAddress(int address, String line, PCB pcb) {
		try {
			setMemoryCell(address, line, pcb);
		} catch (AccessViolation e) {
			e.printStackTrace();
		}
	}

	// diese beiden Methoden werden in der CPU benötigt, lösen die Adresse aber
	// nicht wirklich auf sondern geben nur den eingabewert zurück
	public int resolveAddress(String address) throws AccessViolation {
		return resolveAddress(Integer.parseInt(address));
	}

	public int resolveAddress(int address) throws AccessViolation {
//		if (address < 0 || address > regSet.getLimit()) {
//			SysLogger.writeLog(0, "MMU.resolveAddress: access violation: "
//					+ address);
//			throw new AccessViolation();
//		}
		return address;
	}

	// prüft, ob sich die Seite in der sich die Zeile für den Zugriff befindet
	// im Hauptspeicher eingelagert ist
	public boolean inMemory(PTEntry[] pagetable, int index) {
		return pagetable[index].getpBit();
	}

	// Alte Methode, wurde nicht genutzt, möglichweise für Debugging gedaccht
	// public void dumpMemory( int limit ) {
	// SysLogger.writeLog( 1, "MMU.dumpMemory" );
	// for( int i = 0; i < limit; i++ ) {
	// SysLogger.writeLog( 1, i + ": " + memory.getContent(i) );
	// }
	// }

}
