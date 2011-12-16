/*
 * MemoryManager.java
 *
 * Created on September 1, 2007, 12:31 PM
 *
 */

package MemoryManagement;

import Hardware.MainMemory;
import Hardware.SecondaryStorage;
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
	}

	public void setProcessManager(ProcessManager processmanager) {
		this.processManager = processmanager;
	}
	
	public void setContent(int address, String line) {

	}

	public String getContent(int address) {
		String line = null;
		return line;
	}

	public int loadProgram(String file, PCB pcb) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line = input.readLine();
			int size = 0;
			if (line != null) {
				// In der ersten Zeile steht der benötigte Speicherplatz.
				size = Integer.valueOf(line);
				SysLogger.writeLog(0, "MemoryManager.loadProgram: " + file
						+ " with size " + size);

				pcb.getRegisterSet().setProgramCounter(0);

				int n = 0;
				line = input.readLine();
				while (line != null) {
					// Debug:
					// line = input.readLine();
					memory.setContent(n, line);
					n++;
					line = input.readLine();
				}
			}
			input.close();

		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}

		return 0;
	}

	public void resetRBits() {

	}

	public int replacePage() {
		int index = 0;
		return index;
	}
}
