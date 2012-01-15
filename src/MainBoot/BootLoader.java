package MainBoot;

import MemoryManagement.*;
import Scheduler.*;
import Hardware.*;

import java.io.*;

public class BootLoader {
	// Konstanten für Speicherverwaltung
	public final static int FRAMECOUNT = 8;
	public final static int PAGESIZE = 4;
	public final static int VIRTMEMSIZE = 32;

	static public class ShutdownException extends Exception {
	};

	public static void main(String[] args) throws IOException {
		// Logdatei öffnen
		SysLogger.openLog();

		// Hardware Initialisierung
		MainMemory memory = new MainMemory(FRAMECOUNT * PAGESIZE);
		SecondaryStorage secondaryStorage = new SecondaryStorage();
		MMU mmu = new MMU(memory);
		CPU cpu = new CPU(mmu);

		// Software Initialisierung
		MemoryManagerIF memoryManager = new MemoryManager(memory,secondaryStorage);
		ProcessManager processManager = new ProcessManager(memoryManager);
		SchedulerIF scheduler = new Scheduler(cpu, processManager);

		// Übergabe Scheduler an Prozessmanager
		processManager.setScheduler(scheduler);
		
		// Übergabe Prozessmanager an den Memory Manager
		((MemoryManager) memoryManager).setProcessManager(processManager);

		// Übergabe der Software an die Hardware
		cpu.setProcessManager(processManager);
		cpu.setScheduler(scheduler);
		cpu.setMemoryManager((MemoryManager) memoryManager);
		mmu.setMemoryManager((MemoryManager) memoryManager);
		
		// Erzeugen des Init Prozess
		int pid = processManager.createProcess("init");
		SysLogger.writeLog(0, "BootLoader: initial process created, pid: " + pid);

		// Starten der CPU
		SysLogger.writeLog(0, "BootLoader: starting the cpu");
		try {
			cpu.operate();
		} catch (ShutdownException x) {
			SysLogger.writeLog(0, "BootLoader: shutting down");
			processManager.destroyProcess(pid);
			SysLogger.closeLog();
		}
	}
}
