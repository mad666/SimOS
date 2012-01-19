// Ge�ndert von Sebastian S�ger und Max Richter

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
	private int clockPointer = 0;
	private int pid;

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
	
	public void setRunningPid(int pid) {
		this.pid = pid;
	}
	
	public int getRunningPid () {
		return this.pid;
	}

	// Funktionen

	// eine Zeile in den Speicher schreiben
	// wird nur aufgerufen, wenn die Seite bereits eingelagert ist und schreibt
	// den �bergeben Wert in den Speicher
	public void setBitsWrite(int index) {
		invPageTable[index].setrBit(true);
		invPageTable[index].setmBit(true);
	}

	// eine Zeile aus dem Speicher lesen
	// wird nur aufgerufen, wenn die Seite bereits eingelagert ist und gibt die
	// Zeile aus der angegebenen Adresse zur�ck
	public void setBitsRead(int index) {
		invPageTable[index].setrBit(true);
	}

	
	// virtuellen Speicher aus dem Sekund�rspeicher l�schen
	public void removeStorage(int index) {
		// alle Prozesse �berpr�fen
		for (PCB pcb : processManager.PCBTable.values()) {
			int aktIndex = pcb.getStorageIndex();
			// nur bei Prozessen, die nach dem  zu l�schenden im Sekund�rspeicher sind,
			// muss die Position im PCB aktualisiert werden
			if (aktIndex > index) {
				aktIndex--;
				pcb.setStorageIndex(aktIndex);
			} // end If
		} // End for
		// virtuellen Speicher l�schen
		secondaryStorage.deleteElement(index);
	}
	
	// beim Beenden eines Prozesses soll der Speicher wieder freigegeben werden, 
	// daher p-Bit auf false, falls noch Seiten des Prozesses eingelagert sind
	public void freeMemory (int pid) {
		for (int frame = 0; frame < BootLoader.FRAMECOUNT; frame++) {
			if (invPageTable[frame].getPid()==pid) {
				invPageTable[frame].setpBit(false);
				SysLogger.writeLog(0,"MemoryManager.freeMemory: free frame: " + frame);
			} // end If
		} // end For
	}
	
	// r-Bits aller eingelagerten Seiten zur�cksetzen
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
			// Programmdatei �ffnen
			BufferedReader input = new BufferedReader(new FileReader(file));
			
			// In der ersten Zeile steht der ben�tigte Speicherplatz.
			String line = input.readLine();
			int size = 0;
			if (line != null) {
				size = Integer.valueOf(line);
				
				// pr�fen, ob die maximale G��e des virtuellen Speichers �berschritten wurde
				if (size > (BootLoader.VIRTMEMSIZE * BootLoader.PAGESIZE)) {
					SysLogger.writeLog(0, "MemoryManager.loadProgram: " + file
							+ " maximum Virtual Memory Size exceeded!");
					return -1;
				} // end If

				SysLogger.writeLog(0, "MemoryManager.loadProgram: " + file
						+ " with size " + size);

				// Programmz�hler initialisieren
				pcb.getRegisterSet().setProgramCounter(0);

				// virtuelle Speicher anlegen
				String[] virtMem = new String[size];
				
				// Programm einlesen und in virtuellen Speicher legen
				line = input.readLine();
				for (int index = 0; line != null; index++){
					virtMem[index] = line;
					line = input.readLine();
				} // end For
				
				// virtuellen Speicher in Sekund�rspeicher auslagern
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
				
				// Seitentabelle an PCB anh�ngen
				pcb.setPageTable(pagetable);
				
				// Prozess in Prozesstabelle einf�gen
				processManager.PCBTable.put(pcb.getPid(), pcb);
			} // end If
			input.close(); // Programmdatei schlie�en

			// erste Seite in Hauptspeicher einlagern
			int oldPid = getRunningPid();
			setRunningPid(pcb.getPid());
			replacePage(0);
			setRunningPid(oldPid);

		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
		return 0;
	}

	// Seitenersetzung

	// zu verdr�ngende Seite wird mit Clock Algotithmus ermittelt
	// Nummer der neu einzulagernde Seite wird �bergeben
	public void replacePage(int index) {

		boolean found = false;

		// pr�fen, ob ein Frame noch ungenutzt ist und ggf. Seite dort einlagern
		for (int frame = 0; frame < BootLoader.FRAMECOUNT && found != true; frame++) {
			// p-Bit nicht gesetzt = leerer Frame
			if (!invPageTable[frame].getpBit()) {
				SysLogger.writeLog(0,"MemoryManager.replacePage: found empty frame at: " + frame);
				// neue Seite im Hauptspeicher einlagern
				putPage(index, frame);
				found = true;
			} // end If
		} // end For

		// falls keine freier Frame gefunden wurde, nun Clock ausf�hren
		for (; clockPointer < BootLoader.FRAMECOUNT && found != true; clockPointer++) {

			// alten PCB ,alte Seitentabelle und Seitennummer im aktuellen Rahmen holen
			int aktPage = invPageTable[clockPointer].getAddress();
			PCB oldPcb = processManager.getPCB(invPageTable[clockPointer].getPid());
			PTEntry oldPageTable = oldPcb.getPageTableEntry(aktPage);

			// r-Bit nicht getzt zeigt ungenutze Seite im lezten Timerintervall an
			if (!invPageTable[clockPointer].getrBit()) {

				// alte Seite zur�cksichern, falls diese ge�ndert wurde
				if (invPageTable[clockPointer].getmBit()) {
					for (int line = 0; line < BootLoader.PAGESIZE; line++) {
						secondaryStorage.changeLine(oldPcb.getStorageIndex(), ((aktPage * BootLoader.PAGESIZE) + line), memory.getContent((clockPointer * BootLoader.PAGESIZE) + line));
					} // end For
					SysLogger.writeLog(0,"MemoryManager.replacePage: found unreferenced frame at: " + clockPointer);
					SysLogger.writeLog(0,"MemoryManager.replacePage: m-bit set, saving page");

					// da die alte Seite nun nicht mehr eingelagert ist, Bits in der alten
					// und m-Bit in invertierter Seitentabelle �ndern
					oldPageTable.setAddress(-1);
					oldPageTable.setmBit(false);
					oldPageTable.setpBit(false);
					invPageTable[clockPointer].setmBit(false);
				} // end If

				// falls Seite nicht gesichert werden muss, Bits in der
				// alten Seitentabelle Seitenatbelle �ndern
				else {
					oldPageTable.setAddress(-1);
					oldPageTable.setpBit(false);
					SysLogger.writeLog(0,"MemoryManager.replacePage: found unreferenced frame at: " + clockPointer);
					SysLogger.writeLog(0,"MemoryManager.replacePage: m-bit not set");
				} // end Else
				
				// neue Seite im Hauptspeicher einlagern
				putPage(index, clockPointer);
				found = true;
			} //end If
			
			// r-Bit in der alten Seitetabelle und invertierten Seitentabelle zur�cksetzen, falls dies gesetzt war
			else {
				invPageTable[clockPointer].setrBit(false);
				oldPageTable.setrBit(false);
				SysLogger.writeLog(0,"MemoryManager.replacePage: Frame " +  clockPointer + " still referreced, checking next frame");
			} // end Else

			// wenn Zeiger an Listenende angekommen ist, diesen wieder
			// zur�cksetzen f�r erneuten Durchlauf
			if (clockPointer == (BootLoader.FRAMECOUNT - 1)) {
				clockPointer = -1; // muss -1 sein, da hiernach noch inkrementiert wird
			} //end If
		}
	}
	
	public void putPage(int index, int frame) {
		// alle Zeilen der neuen Seite einlagern
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
		SysLogger.writeLog(0,"MemoryManager.replacePage: putting page " + index + " in frame " + frame);
	}
}
