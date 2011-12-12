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
import Scheduler.Scheduler;

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
		setContent(realAddress, value);
	}

	public void setAbsoluteAddress(int address, String value) {
		try {
			setContent(resolveAddress(address), value);
		} catch (AccessViolation a) {
			System.err.println(a);
		}
	}

	public int resolveAddress(String address) throws AccessViolation {
		return resolveAddress(Integer.parseInt(address));
	}
	
	public int resolveAddress(int address) throws AccessViolation {
//		int i;
//		if(BootLoader.PAGESIZE < BootLoader.VIRTMEMSIZE) {
//			i=String.valueOf(BootLoader.VIRTMEMSIZE).length();
//		}
		//String sInd = String.format("%"+0+i+"d", resolvePageIndex(address));
		String sInd = String.format("%04d", resolvePageIndex(address));
		String sOff = String.format("%04d", resolveOffset(address));
		return Integer.parseInt(sInd+sOff);
		
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
		return getContent(address);

	}

//	public void dumpMemory(int limit) {
//		SysLogger.writeLog(1, "MMU.dumpMemory");
//		for (int i = 0; i < limit; i++) {
//			SysLogger.writeLog(1, i + ": " + memory.getContent(i));
//		}
//	}

	public void setContent(int address, String value) {

	}
	
	public void setContent(Page page) {
		
	}

	public String getContent(String address) {
		return getContent(Integer.parseInt(address));
	}

	public String getContent(int address) {
		String index=Integer.toString(address).substring(0, 3);
		String offset=Integer.toString(address).substring(4, 7);
		int ind = Integer.parseInt(index);
		int off = Integer.parseInt(offset);
		// wie kommen wir hier an die PID? Die CPU kennt diese auf jeden fall
		

		return "";
	}

	public int replacePage() {

		return 0;
	}
}
