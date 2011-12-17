/*
 * MemoryManager.java
 *
 * Created on September 1, 2007, 12:31 PM
 *
 */

package MemoryManagement;

import Hardware.MainMemory;
import Hardware.SecondaryStorage;
import MainBoot.BootLoader;
import MainBoot.SysLogger;
import java.io.*;

public class MemoryManager implements MemoryManagerIF {

	private MainMemory memory;
	private SecondaryStorage secondaryStorage;
	private ProcessManager processManager;
	private InvPTEntry[] invPageTable;

	/** Creates a new instance of MemoryManager */
	public MemoryManager(MainMemory memory, SecondaryStorage secondaryStorage) {
		this.memory = memory;
		this.secondaryStorage = secondaryStorage;
		this.invPageTable = new InvPTEntry[BootLoader.FRAMECOUNT];
		for (int i = 0; i < BootLoader.FRAMECOUNT; i++) {
			invPageTable[i] = new InvPTEntry();
		}
	}

	public void setProcessManager(ProcessManager processmanager) {
		this.processManager = processmanager;
	}

	// wird nur aufgerufen, wenn die Seite bereits eingelagert ist
	public void setContent(int index, int offset, String line, int pid) {
		processManager.getPCB(pid).getPageTable()[index].setrBit(true);
		memory.setContent(
				processManager.getPCB(pid).getPageTable()[index].getAddress()
						+ offset, line);
		processManager.getPCB(pid).getPageTable()[index].setmBit(true);
	}

	public String getContent(int index, int offset, int pid) {
		processManager.getPCB(pid).getPageTable()[index].setrBit(true);
		return memory
				.getContent(processManager.getPCB(pid).getPageTable()[index]
						.getAddress() + offset);
	}

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

				pcb.getRegisterSet().setProgramCounter(0);

				int n = 0;
				String[] lines = new String[size];
				line = input.readLine();
				while (line != null) {
					lines[n] = line;
					n++;
					line = input.readLine();
				}
				pcb.setStorageIndex(secondaryStorage.addElement(lines));

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
			replacePage(0, pcb.getPid());

		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}

		return 0;
	}

	public void resetRBits() {

		for (int i = 0; i < BootLoader.FRAMECOUNT; i++) {
			invPageTable[i].setrBit(false);
			processManager.getPCB(invPageTable[i].getPid())
					.getPagteTableEntry(invPageTable[i].getPageIndex())
					.setrBit(false);
		}
	}

	public void replacePage(int index, int pid) {

	}
}
