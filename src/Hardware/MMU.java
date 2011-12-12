/*
 * MMU.java
 *
 * Created on 16. Dezember 2007, 17:00
 *
 */

package Hardware;

import MainBoot.BootLoader;
import MainBoot.SysLogger;
import MemoryManagement.Page;
import MemoryManagement.PageTable;

public class MMU {
	private MainMemory memory;
	private RegisterSet regSet;

	static public class AccessViolation extends Exception {
	};

	/** Creates a new instance of MMU */
	public MMU(MainMemory memory) {
		this.memory = memory;
	}

	public void setRegisterSet(RegisterSet regSet) {
		this.regSet = regSet;
	}

	public void setMemoryCell(String address, String value)
			throws AccessViolation {
		setMemoryCell(Integer.parseInt(address), value);
	}

	public void setMemoryCell(int address, String value) throws AccessViolation {
		if (address < 0 || address > regSet.getLimit()) {
			SysLogger.writeLog(0, "MMU.setMemoryCell: access violation: "
					+ address);
			throw new AccessViolation();
		}
		int realAddress = address + regSet.getBase();
		memory.setContent(realAddress, value);
	}

	public void setAbsoluteAddress(int address, String value) {
		setContent(resolveAddress(address), value);
	}

	public int resolveAddress(String address) throws AccessViolation {
		return resolveAddress(Integer.parseInt(address));
	}
	
	public int resolveAddress(int address) throws AccessViolation {
		int i;
		if(BootLoader.PAGESIZE < BootLoader.VIRTMEMSIZE) {
			i=String.valueOf(BootLoader.VIRTMEMSIZE).length();
		}
		String sInd = String.format("%"+0+i+"d", resolvePageIndex(address));
		String sOff = String.valueOf(resolvePageIndex(address)) + String.valueOf(resolveOffset(address));
		//SysLogger.writeLog(0, "MMU.resolveAddress: access violation: "
		//		+ address);
		//throw new AccessViolation();
		return Integer.parseInt((sInd+sOff));
		
	}
	public int resolvePageIndex(String address) throws AccessViolation {
		return resolvePageIndex(Integer.parseInt(address));
	}

	public int resolvePageIndex(int address) throws AccessViolation {
		return address / BootLoader.PAGESIZE;
	}

	public int resolveOffset(String address) throws AccessViolation {
		return resolveOffset(Integer.parseInt(address));
	}

	public int resolveOffset(int address) throws AccessViolation {
		return address % BootLoader.PAGESIZE;
	}

	public String getMemoryCell(String address) throws AccessViolation {
		return getMemoryCell(Integer.parseInt(address));
	}

	public String getMemoryCell(int address) throws AccessViolation {
		return getContent(resolvePageIndex(address), resolveOffset(address));

	}

	public void dumpMemory(int limit) {
		SysLogger.writeLog(1, "MMU.dumpMemory");
		for (int i = 0; i < limit; i++) {
			SysLogger.writeLog(1, i + ": " + memory.getContent(i));
		}
	}

	public void setContent(Page page) {

	}

	public String getContent(String address) {
		return getContent(Integer.parseInt(address));
	}

	public String getContent(int address) {
		return "";
	}

	public int replacePage() {

		return 0;
	}
}
