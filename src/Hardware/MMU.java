/*
 * MMU.java
 *
 * Created on 16. Dezember 2007, 17:00
 *
 */

package Hardware;

import MainBoot.SysLogger;
import MemoryManagement.MemoryManager;
import MemoryManagement.PCB;

public class MMU {
	private MemoryManager memoryManager;
	private RegisterSet regSet;

	static public class AccessViolation extends Exception {
	};

	  /** Creates a new instance of MMU */
	public MMU() {
	}

	public void setRegisterSet(RegisterSet regSet) {
		this.regSet = regSet;
	}

	public void setMemoryManager(MemoryManager memoryManager) {
		this.memoryManager = memoryManager;
	}

	public void setMemoryCell(String address, String value, PCB pcb)
			throws AccessViolation {
		setMemoryCell(Integer.parseInt(address), value, pcb);
	}

	public void setMemoryCell(int address, String value, PCB pcb)
			throws AccessViolation {
		if (address < 0 || address > regSet.getLimit()) {
			SysLogger.writeLog(0, "MMU.setMemoryCell: access violation: "
					+ address);
			throw new AccessViolation();
		}
		int realAddress = address + regSet.getBase();

		memoryManager.setContent(realAddress, value);
	}

	public String getMemoryCell(String address, PCB pcb) throws AccessViolation {
		return getMemoryCell(Integer.parseInt(address), pcb);
	}

	public String getMemoryCell(int address, PCB pcb) throws AccessViolation {
		if (address < 0 || address > regSet.getLimit()) {
			SysLogger.writeLog(0, "MMU.getMemoryCell: access violation: "
					+ address);
			throw new AccessViolation();
		}
		int realAddress = address + regSet.getBase();
		return memoryManager.getContent(realAddress);
	}

	public void setAbsoluteAddress(int address, String value, PCB pcb) {
		memoryManager.setContent(address, value);
	}

	public int resolveAddress(String address) throws AccessViolation {
		return resolveAddress(Integer.parseInt(address));
	}

	public int resolveAddress(int address) throws AccessViolation {
		if (address < 0 || address > regSet.getLimit()) {
			SysLogger.writeLog(0, "MMU.resolveAddress: access violation: "
					+ address);
			throw new AccessViolation();
		}
		return address + regSet.getBase();
	}

	public boolean inMemory(int index) {
		return true;
	}
	// public void dumpMemory( int limit ) {
	// SysLogger.writeLog( 1, "MMU.dumpMemory" );
	// for( int i = 0; i < limit; i++ ) {
	// SysLogger.writeLog( 1, i + ": " + memory.getContent(i) );
	// }
	// }

}
