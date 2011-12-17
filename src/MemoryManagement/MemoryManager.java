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
		processManager.getPCB(pid).getPageTable()[index].setrBit(true);
		memory.setContent(
				processManager.getPCB(pid).getPageTable()[index].getAddress()
						+ offset, line);
		processManager.getPCB(pid).getPageTable()[index].setmBit(true);
	}

	// eine Zeile aus dem Speicher lesen
	// wird nur aufgerufen, wenn die Seite bereits eingelagert ist und gibt die
	// Zeile aus der angegebenen Adresse zurück
	public String getContent(int index, int offset, int pid) {
		processManager.getPCB(pid).getPageTable()[index].setrBit(true);
		return memory
				.getContent(processManager.getPCB(pid).getPageTable()[index]
						.getAddress() + offset);
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
				// virtuelle Speicher auslagern
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
		for (int i = 0; i < BootLoader.FRAMECOUNT; i++) {
			invPageTable[i].setrBit(false);
			processManager.getPCB(invPageTable[i].getPid())
					.getPagteTableEntry(invPageTable[i].getPageIndex())
					.setrBit(false);
		}
	}

	// Seitenersetzung
	// zu verdrängende Seite wird mit Clock Algotithmus ermittelt
	// die neu einzulagernde Seite wird übergeben
	public void replacePage(int index, int pid) {
		for (int i = 0; i< BootLoader.FRAMECOUNT; i++ ) {
			if (invPageTable[i].getPid() == -2) {
				for (int x = 0; x< BootLoader.PAGESIZE; x++) {
					memory.setContent(x, secondaryStorage.getStorage(processManager.getPCB(pid).getStorageIndex())[(index*BootLoader.PAGESIZE)+x]);
				}
				
			}
		}
		
	}
}
