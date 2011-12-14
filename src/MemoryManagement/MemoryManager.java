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

	/** Creates a new instance of MemoryManager */
	public MemoryManager(MainMemory memory, SecondaryStorage secondaryStorage) {
		this.memory = memory;
		this.secondaryStorage = secondaryStorage;
	}

	public int loadProgram(String file, PCB pcb) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line = input.readLine();
			int size = 0;
			if (line != null) {
				// In der ersten Zeile steht der benötigte Speicherplatz.
				size = (Integer.valueOf(line)) / BootLoader.PAGESIZE;
				SysLogger.writeLog(0, "MemoryManager.loadProgram: " + file
						+ " with size " + size);

				PageTable pageTable = new PageTable(size);
				Page[] pages = new Page[size];
				for(int i = 0; i < size; i++) {
					pages[i] = new Page(i, pcb.getPid());
				}
				pcb.setPageTable(pageTable);
				pcb.getRegisterSet().setProgramCounter(0);

				int pageIndex = 0;
				int pageOff = 0;

				line = input.readLine();
				pageOff++;
				while (line != null) {
					if (pageOff > 0)
						pages[pageIndex].setPageContent(line, 0);
					while (pageOff < BootLoader.PAGESIZE) {
						line = input.readLine();
						pages[pageIndex].setPageContent(line, pageOff);
						pageOff++;
					}
					
					pageOff = 0;
					pageIndex++;
				}
				memory.setContent(pages[0]);
				secondaryStorage.addElement(pages);
			}
			input.close();
			
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}

		return 0;
	}

}
