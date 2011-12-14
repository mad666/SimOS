/*
 * MMU.java
 *
 * Created on 16. Dezember 2007, 17:00
 *
 */

package Hardware;

import MainBoot.BootLoader;
import MainBoot.SysLogger;
import MemoryManagement.PCB;
import MemoryManagement.Page;
import MemoryManagement.PageTable;
import MemoryManagement.ProcessManager;
import Scheduler.Scheduler;

public class MMU {
	
	private MainMemory memory;
	private RegisterSet regSet;
	private PageTable pageTable = new PageTable(BootLoader.VIRTMEMSIZE);
	private PCB pcb;
	private ProcessManager processManager;
	private SecondaryStorage secondaryStorage;
	

	static public class AccessViolation extends Exception { };

	
	
	//Konstruktoren
	public MMU(MainMemory memory, SecondaryStorage secondaryStorage) {
		this.memory = memory;
		this.secondaryStorage = secondaryStorage;
		this.pcb = new PCB(-2,0,"");	//weil -1 idleProcess
	}
	
	
	
	//Getter & Setter
	public void setRegisterSet(RegisterSet regSet) {
		this.regSet = regSet;
	}
	public void setProcessManager(ProcessManager processManager) {
		this.processManager = processManager;
	}

	
	
	//Funktionen
	public void setMemoryCell(String address, String value, int pid)
			throws AccessViolation {
		setMemoryCell(Integer.parseInt(address), value, pid);
	}
	public void setMemoryCell(int address, String value, int pid) 
			throws AccessViolation {
		checkProcess(pid);
		setContent(resolveAddress(address), value);
	}

	public String getMemoryCell(String address, int pid) throws AccessViolation {
		return getMemoryCell(Integer.parseInt(address), pid);
	}
	public String getMemoryCell(int address, int pid) throws AccessViolation {
		return getContent(resolveAddress(address), pid);
	}
	
	public void setContent(String address, String value) {
		setContent(Integer.parseInt(address), value);
	}
	public void setContent(int address, String value) {

	}
	public void setContent(Page page) {
		
	}

	public String getContent(String address, int pid) {
		return getContent(Integer.parseInt(address), pid);
	}
	public String getContent(int address, int pid) {
		String index=Integer.toString(address).substring(0, 3);
		String offset=Integer.toString(address).substring(4, 7);
		int ind = Integer.parseInt(index);
		int off = Integer.parseInt(offset);
		//wenn seite nicht eingelagert
		if(inMemory(ind));
		
		return "";
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
	
	//auf neuen Prozess prüfen
	public boolean checkProcess(int pid) {
		//nur für den ersten durchlauf
		if(this.pcb.getPid()==-2) {
			this.pcb = processManager.getPCB(pid);
			this.pageTable = pcb.getPageTable();
			return true;
		}
		//wenn Prozess unverändert
		else if(this.pcb.getPid()==pid) {
			return false;
		}
		//neuer Prozess
		//Seitentabelle des alten sichern
		else {
			this.pcb.setPageTable(this.pageTable);
			this.pcb = processManager.getPCB(pid);
			this.pageTable = pcb.getPageTable();
			return true;
		}
	}
	
	//prüfen ob Seite eingelagert
	public boolean inMemory(int index) {
		if(pcb.getPageTable().getFrameID(index)==-1) return false;
		else return true;
	}
	
	//Seite ersetzen
	public int replacePage() {

		return 0;
	}
	
	//Alle rBits auf 0 setzen
	public void resetRBits() {
		for(int index = 0; index < BootLoader.MEMSIZE;index++) {
			memory.getFrame(index).getFrameContent().setrBit(false);
		}
		
	}
	
//	identisch mit setMemoryCell
//	public void setAbsoluteAddress(int address, String value, PCB pcb) {
//		try {
//			setContent(resolveAddress(address), value);
//		} catch (AccessViolation a) {
//			System.err.println(a);
//		}
//	}
	
//	Unnötig
//	public void dumpMemory(int limit) {
//		SysLogger.writeLog(1, "MMU.dumpMemory");
//		for (int i = 0; i < limit; i++) {
//			SysLogger.writeLog(1, i + ": " + memory.getContent(i));
//		}
//	}
	
}
