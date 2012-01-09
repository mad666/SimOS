package MemoryManagement;

import Hardware.MainMemory;
import Hardware.SecondaryStorage;
import MainBoot.BootLoader;
import MainBoot.SysLogger;
import java.io.*;

public class MemoryManager implements MemoryManagerIF {
	// Attribute
	private MainMemory memory;
	private SecondaryStorage secondaryStorage;
	private ProcessManager processManager;
	private InvPTEntry[] invPageTable;

	// Konstruktor
	/** Creates a new instance of MemoryManager */
	public MemoryManager(MainMemory memory, SecondaryStorage secondaryStorage) {
		this.memory = memory;
		this.secondaryStorage = secondaryStorage;
		this.invPageTable = new InvPTEntry[BootLoader.FRAMECOUNT];
		for (int i = 0; i < BootLoader.FRAMECOUNT; i++) {
			invPageTable[i] = new InvPTEntry();
		}
	}

	// Setter & Getter
	public void setProcessManager(ProcessManager processmanager) {
		this.processManager = processmanager;
	}

	// Funktionen

	// eine Zeile in den Speicher schreiben
	// wird nur aufgerufen, wenn die Seite bereits eingelagert ist und schreibt
	// den übergeben Wert in den Speicher
	public void setContent(int address, String line, int pid) {
		invPageTable[address / BootLoader.PAGESIZE].setrBit(true);
		memory.setContent(address, line);
		invPageTable[address / BootLoader.PAGESIZE].setmBit(true);

	}

	// eine Zeile aus dem Speicher lesen
	// wird nur aufgerufen, wenn die Seite bereits eingelagert ist und gibt die
	// Zeile aus der angegebenen Adresse zurück
	public String getContent(int address, int pid) {
		invPageTable[address / BootLoader.PAGESIZE].setrBit(true);
		return memory.getContent(address);
	}

	
	// virtuellen Speicher aus dem Sekundärspeicher löschen
	public void removeStorage(int index) {
		PCB aktPCB;
		int aktIndex;
		for (int i =1; i < processManager.PCBTable.size(); i++) {
			aktPCB = processManager.PCBTable.elements().nextElement();
			aktIndex = aktPCB.getStorageIndex();
			if (aktIndex > index) {
				aktIndex--;
				aktPCB.setStorageIndex(aktIndex);
			} // end If
		} // End for
		secondaryStorage.deleteElement(index);
	}
	
	// beim Beenden eines Prozesses soll der Speicher wieder freigegeben werden, daher p-Bit auf false, falls noch Seiten des Prozesses eingelagert sind
	public void freeMemory (int pid) {
		for (int frame = 0; frame < BootLoader.FRAMECOUNT; frame++) {
			if (invPageTable[frame].getPid()==pid) {
				invPageTable[frame].setpBit(false);
				SysLogger.writeLog(0,"MemoryManager.freeMemory: free frame: " + frame);
			} // end If
		} // end For
	}
	
	// r-Bits aller eingelagerten Seiten zurücksetzen
	public void resetRBits() {
		for (int frame = 0; frame < BootLoader.FRAMECOUNT; frame++) {
			if (invPageTable[frame].getpBit()) {
			invPageTable[frame].setrBit(false);
			processManager.getPCB(invPageTable[frame].getPid()).getPageTableEntry(invPageTable[frame].getAddress()).setrBit(false);
			} // end If
		} // end For
	}
	
	// Programm bei Prozesserstellung laden
	// liest die Befehle aus der Programmdatei, erstellt den virtuelle Speicher
	// und erstell die Seitentabelle
	public int loadProgram(String file, PCB pcb) {
		try {
			
			// Programmdatei öffnen
			BufferedReader input = new BufferedReader(new FileReader(file));
			
			// In der ersten Zeile steht der benötigte Speicherplatz.
			String line = input.readLine();
			int size = 0;
			if (line != null) {
				size = Integer.valueOf(line);
				
				// prüfen, ob die maximale Göße des virtuellen Speichers überschritten wurde
				if (size > (BootLoader.VIRTMEMSIZE * BootLoader.PAGESIZE)) {
					SysLogger.writeLog(0, "MemoryManager.loadProgram: " + file
							+ " maximum Virtual Memory Size exceeded!");
					return -1;
				} // end If

				SysLogger.writeLog(0, "MemoryManager.loadProgram: " + file
						+ " with size " + size);

				// Programmzähler initialisieren
				pcb.getRegisterSet().setProgramCounter(0);

				// Programm einlesen und in virtuellen Speicher legen
//				int count = 0;
//				String[] lines = new String[size];
//				line = input.readLine();
//				while (line != null) {
//					lines[count] = line;
//					count++;
//					line = input.readLine();
//				} // end While
				
				// virtuelle Speicher anlegen
				String[] virtMem = new String[size];
				
				// Programm einlesen und in virtuellen Speicher legen
				line = input.readLine();
				for (int index = 0; line != null; index++){
					virtMem[index] = line;
					line = input.readLine();
				} // end For
				
				// virtuellen Speicher in Sekundärspeicher auslagern
				pcb.setStorageIndex(secondaryStorage.addElement(virtMem));

				// Seitentabelle erstellen
				PTEntry[] pagetable;
				if ((size % BootLoader.PAGESIZE) == 0) {
					pagetable = new PTEntry[size / BootLoader.PAGESIZE];
				} // end If
			
				else {
					pagetable = new PTEntry[(size / BootLoader.PAGESIZE) + 1];
				} // end Else
				
				// Seitentabelle initialisieren
				for (int entry = 0; entry < pagetable.length; entry++) {
					pagetable[entry] = new PTEntry();
				} // end For
				
				// Seitentabelle an PCB anhängen
				pcb.setPageTable(pagetable);
				
				// Prozess in Prozesstabelle einfügen
				processManager.PCBTable.put(pcb.getPid(), pcb);
			} // end If
			input.close(); // Programmdatei schließen

			// erste Seite in Hauptspeicher einlagern
			replacePage(0, pcb.getPid());

		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
		return 0;
	}

	// Seitenersetzung

	// zu verdrängende Seite wird mit Clock Algotithmus ermittelt
	// Nummer der neu einzulagernde Seite wird übergeben
	public void replacePage(int index, int pid) {

		boolean found = false;

		// prüfen, ob ein Frame noch ungenutzt ist und ggf. Seite dort einlagern
		for (int frame = 0; frame < BootLoader.FRAMECOUNT && found != true; frame++) {
			// p-Bit nicht gesetzt = leerer Frame
			if (!invPageTable[frame].getpBit()) {
				// alle Zeilen der neuen Seite Speicher einlagern
				for (int line = 0; line < BootLoader.PAGESIZE; line++) {
					memory.setContent((frame * BootLoader.PAGESIZE) + line, secondaryStorage.getStorage(processManager.getPCB(pid).getStorageIndex())[(index * BootLoader.PAGESIZE) + line]);
				} // end For
				
				// Bits in neuer Seitentabelle und invertierter Seitentabelle setzen
				processManager.getPCB(pid).getPageTableEntry(index).setAddress(frame);
				processManager.getPCB(pid).getPageTableEntry(index).setpBit(true);
				processManager.getPCB(pid).getPageTableEntry(index).setrBit(true);
				invPageTable[frame].setAddress(index);
				invPageTable[frame].setpBit(true);
				invPageTable[frame].setrBit(true);
				invPageTable[frame].setPid(pid);
				found = true;
				SysLogger.writeLog(0,"MemoryManager.replacePage: found empty frame at: " + frame);
				SysLogger.writeLog(0,"MemoryManager.replacePage: putting page " + index + " in frame " + frame);
			} // end If
		} // end For

		// falls keine freier Frame gefunden wurde, nun Clock ausführen
		for (int frame = 0; frame < BootLoader.FRAMECOUNT && found != true; frame++) {

			// r-Bit nicht getzt zeigt ungenutze Seite im lezten Timerintervall an
			if (!invPageTable[frame].getrBit()) {

				// alte Seite zurücksichern, falls diese geändert wurde
				if (invPageTable[frame].getmBit()) {
					for (int line = 0; line < BootLoader.PAGESIZE; line++) {
						secondaryStorage.changeLine((processManager.getPCB(invPageTable[frame].getPid()).getStorageIndex()), ((invPageTable[frame].getAddress() * BootLoader.PAGESIZE) + line),	memory.getContent((frame * BootLoader.PAGESIZE) + line));
					} // end For
					SysLogger.writeLog(0,"MemoryManager.replacePage: found unreferenced frame at: " + frame);
					SysLogger.writeLog(0,"MemoryManager.replacePage: m-bit set, saving page");

					// da die alte Seite nun nicht mehr eingelagert ist, Bits in der alten
					// und invertierter Seitentabelle setzen
					processManager.getPCB(invPageTable[frame].getPid()).getPageTableEntry(invPageTable[frame].getAddress()).setAddress(-1);
					processManager.getPCB(invPageTable[frame].getPid()).getPageTableEntry(invPageTable[frame].getAddress()).setmBit(false);
					processManager.getPCB(invPageTable[frame].getPid()).getPageTableEntry(invPageTable[frame].getAddress()).setpBit(false);
					invPageTable[frame].setAddress(-1);
					invPageTable[frame].setmBit(false);
					invPageTable[frame].setpBit(false);
				} // end If

				// falls Seite nicht gesichert werden muss, Bits in der
				// alten und invertierter Seitentabelle Seitenatbelle setzen
				else {
					processManager.getPCB(invPageTable[frame].getPid()).getPageTableEntry(invPageTable[frame].getAddress()).setAddress(-1);
					processManager.getPCB(invPageTable[frame].getPid()).getPageTableEntry(invPageTable[frame].getAddress()).setpBit(false);
					invPageTable[frame].setAddress(-1);
					invPageTable[frame].setpBit(false);
					SysLogger.writeLog(0,"MemoryManager.replacePage: found unreferenced frame at: " + frame);
					SysLogger.writeLog(0,"MemoryManager.replacePage: m-bit not set");
				} // end Else
				
				// alle Zeilen der neuen Seite einlagern
				for (int line = 0; line < BootLoader.PAGESIZE; line++) {
					memory.setContent((frame * BootLoader.PAGESIZE) + line,secondaryStorage.getStorage(processManager.getPCB(pid).getStorageIndex())[(index * BootLoader.PAGESIZE) + line]);
				} // end For
				
				// Bits in der neuen Seitentabelle und invertierter Seitentabelle setzen
				processManager.getPCB(pid).getPageTableEntry(index).setpBit(true);
				processManager.getPCB(pid).getPageTableEntry(index).setrBit(true);
				processManager.getPCB(pid).getPageTableEntry(index).setAddress(frame);
				invPageTable[frame].setpBit(true);
				invPageTable[frame].setrBit(true);
				invPageTable[frame].setPid(pid);
				invPageTable[frame].setAddress(index);
				found = true;
				SysLogger.writeLog(0,"MemoryManager.replacePage: putting page " + index + " in frame " + frame);
			} //end If
			
			// r-Bit in der alten Seitetabelle und invertierten Seitentabelle zurücksetzen, falls dies gesetzt war
			else {
				invPageTable[frame].setrBit(false);
				processManager.getPCB(invPageTable[frame].getPid()).getPageTableEntry(invPageTable[frame].getAddress()).setrBit(false);
				SysLogger.writeLog(0,"MemoryManager.replacePage: Frame " +  frame + " still referreced, checking next frame");
			} // end Else

			// wenn zeiger an Listenende angekommen ist, diesen wieder
			// zurücksetzen für erneuten Durchlauf
			if (frame == (BootLoader.FRAMECOUNT - 1)) {
				frame = 0;
			} //end If
		}
	}
}
