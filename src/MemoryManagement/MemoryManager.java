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
	public void setContent(int index, int offset, String line, int pid) {
		processManager.getPCB(pid).getPageTableEntry(index).setrBit(true);
		invPageTable[processManager.getPCB(pid).getPageTableEntry(index)				.getAddress()].setrBit(true);
//		secondaryStorage.changeLine(processManager.getPCB(pid).getStorageIndex(), (index*BootLoader.PAGESIZE)+offset, line);
		memory.setContent(((processManager.getPCB(pid).getPageTableEntry(index)				.getAddress() * BootLoader.PAGESIZE) + offset), line);
		
		processManager.getPCB(pid).getPageTableEntry(index).setmBit(true);
		invPageTable[processManager.getPCB(pid).getPageTableEntry(index)				.getAddress()].setmBit(true);

	}

	// eine Zeile aus dem Speicher lesen
	// wird nur aufgerufen, wenn die Seite bereits eingelagert ist und gibt die
	// Zeile aus der angegebenen Adresse zurück
	public String getContent(int index, int offset, int pid) {
		processManager.getPCB(pid).getPageTableEntry(index).setrBit(true);
		invPageTable[processManager.getPCB(pid).getPageTableEntry(index)
				.getAddress()].setrBit(true);
		return memory.getContent((processManager.getPCB(pid)
				.getPageTableEntry(index).getAddress() * BootLoader.PAGESIZE)
				+ offset);
	}

	// Programm bei Prozesserstellung laden
	// liest die Befehle aus der Programmdatei, erstellt den virtuelle Speicher
	// und erstell die Seitentabelle
	public int loadProgram(String file, PCB pcb) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line = input.readLine();
			int size = 0;
			if (line != null) {
				// In der ersten Zeile steht der benötigte Speicherplatz.
				size = Integer.valueOf(line);
				if (size > (BootLoader.VIRTMEMSIZE * BootLoader.PAGESIZE)) {
					SysLogger.writeLog(0, "MemoryManager.loadProgram: " + file
							+ " maximum Virtual Memory Size exceeded!");
					return -1;
				}

				SysLogger.writeLog(0, "MemoryManager.loadProgram: " + file
						+ " with size " + size);

				// Programmzähler initialisieren
				pcb.getRegisterSet().setProgramCounter(0);

				// Programm einlesen und in virtuellen Speicher legen
				int count = 0;
				String[] lines = new String[size];
				line = input.readLine();
				while (line != null) {
					lines[count] = line;
					count++;
					line = input.readLine();
				}
				// virtuellen Speicher auslagern
				pcb.setStorageIndex(secondaryStorage.addElement(lines));

				// Seitentabelle erstellen
				PTEntry[] pagetable;
				if ((size % BootLoader.PAGESIZE) == 0)
					pagetable = new PTEntry[size / BootLoader.PAGESIZE];
				else
					pagetable = new PTEntry[(size / BootLoader.PAGESIZE) + 1];
				for (int i = 0; i < pagetable.length; i++) {
					pagetable[i] = new PTEntry();
				}
				pcb.setPageTable(pagetable);
				processManager.PCBTable.put(pcb.getPid(), pcb);
			}
			input.close();

			// erste Seite in Hauptspeicher einlagern
			replacePage(0, pcb.getPid());

		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
		return 0;
	}

	// r-Bits aller eingelagerten Seiten zurücksetzen
	public void resetRBits() {
		for (int frame = 0; frame < BootLoader.FRAMECOUNT; frame++) {
			invPageTable[frame].setrBit(false);
			processManager.getPCB(invPageTable[frame].getPid())
					.getPageTableEntry(invPageTable[frame].getPageIndex())
					.setrBit(false);
		}
	}

	// Seitenersetzung

	// zu verdrängende Seite wird mit Clock Algotithmus ermittelt
	// Nummer der neu einzulagernde Seite wird übergeben
	public void replacePage(int index, int pid) {

		boolean found = false;

		// prüfen, ob ein Frame noch ungenutzt ist und ggf. Seite dort einlagern
		for (int frame = 0; frame < BootLoader.FRAMECOUNT && found != true; frame++) {
			// pid = -2 zeigt ungenutzen Frame an
			if (invPageTable[frame].getPid() == -2) {
				// alle Zeilen der neuen Seite Seite einlagern
				for (int line = 0; line < BootLoader.PAGESIZE; line++) {
					memory.setContent((frame * BootLoader.PAGESIZE)+ line,	secondaryStorage.getStorage(processManager.getPCB(pid).getStorageIndex())[(index * BootLoader.PAGESIZE)+ line]);
				} // end For
				
				// Bits in neuer Seitentabelle und invertierter Seitentabelle setzen
				processManager.getPCB(pid).getPageTableEntry(index)
						.setpBit(true);
				processManager.getPCB(pid).getPageTableEntry(index)
						.setrBit(true);
				processManager.getPCB(pid).getPageTableEntry(index)
						.setAddress(frame);
				invPageTable[frame].setpBit(true);
				invPageTable[frame].setrBit(true);
				invPageTable[frame].setAddress(frame);
				invPageTable[frame].setPid(pid);
				invPageTable[frame].setPageIndex(index);
				found = true;
			} // end If
		} // end For

		// falls keine freier Frame gefunden wurde, nun Clock ausführen
		for (int frame = 0; frame < BootLoader.FRAMECOUNT && found != true; frame++) {

			// r-Bit = false zeigt ungenutze Seite im lezten Timerintervall an
			if (invPageTable[frame].getrBit() == false) {

				// alte Seite zurücksichern, falls diese geändert wurde
				if (invPageTable[frame].getmBit() == true) {
					for (int line = 0; line < BootLoader.PAGESIZE; line++) {
						secondaryStorage.changeLine((processManager
								.getPCB(invPageTable[frame].getPid())
								.getStorageIndex()), ((invPageTable[frame]
								.getPageIndex() * BootLoader.PAGESIZE) + line),
								memory.getContent(frame + line));

					} // end For

					// da die alte Seite nun nicht mehr eingelagert ist, Bits in der alten
					// Seitenatbelle setzen
					processManager
							.getPCB(invPageTable[frame].getPid())
							.getPageTableEntry(
									invPageTable[frame].getPageIndex())
							.setAddress(-1);
					processManager
							.getPCB(invPageTable[frame].getPid())
							.getPageTableEntry(
									invPageTable[frame].getPageIndex())
							.setmBit(false);
					processManager
							.getPCB(invPageTable[frame].getPid())
							.getPageTableEntry(
									invPageTable[frame].getPageIndex())
							.setpBit(false);
				} // end If

				// falls Seite nicht gesichert werden muss, nur Bits in der
				// alten Seitenatbelle setzen
				else {
					processManager
							.getPCB(invPageTable[frame].getPid())
							.getPageTableEntry(
									invPageTable[frame].getPageIndex())
							.setAddress(-1);
					processManager
							.getPCB(invPageTable[frame].getPid())
							.getPageTableEntry(
									invPageTable[frame].getPageIndex())
							.setpBit(false);
				} // end Else
				
				// alle Zeilen der neuen Seite einlagern
				for (int line = 0; line < BootLoader.PAGESIZE; line++) {
					memory.setContent(
							line,
							secondaryStorage.getStorage(processManager.getPCB(
									pid).getStorageIndex())[(index * BootLoader.PAGESIZE)
									+ line]);
				} // end For
				
				// Bits in der neuen Seitentabelle und invertierter Seitentabelle setzen
				processManager.getPCB(pid).getPageTableEntry(index)
						.setpBit(true);
				processManager.getPCB(pid).getPageTableEntry(index)
						.setrBit(true);
				processManager.getPCB(pid).getPageTableEntry(index)
						.setAddress(frame);
				invPageTable[frame].setpBit(true);
				invPageTable[frame].setrBit(true);
				invPageTable[frame].setAddress(frame);
				invPageTable[frame].setPid(pid);
				invPageTable[frame].setPageIndex(index);
				found = true;

				// r-Bit in der alten Seitetabelle zurücksetzen, falls dies gesetzt war
			} //end If
			
			else {
				invPageTable[frame].setrBit(false);
				processManager.getPCB(invPageTable[frame].getPid())
						.getPageTableEntry(invPageTable[frame].getPageIndex())
						.setrBit(false);
			} // end Else

			// wenn zeiger an Listenende angekommen ist, diesen wieder
			// zurücksetzen für erneuten durchlauf
			if (frame == (BootLoader.FRAMECOUNT - 1)) {
				frame = 0;
			} //end If
		}
	}
}
