package MainBoot;
import MemoryManagement.*;
import Scheduler.*;
import Hardware.*;
import Hardware.MMU.AccessViolation;

import java.io.*;

public class BootLoader {
  public final static int MEMSIZE = 8;
  public final static int PAGESIZE = 4;
  public final static int VIRTMEMSIZE = 32;
  
  static public class ShutdownException extends Exception{};
  
  public static void main(String [] args) throws IOException, AccessViolation {
    SysLogger.openLog();
    /* Die Instanzen fuer die verschiedenen Programmteile werden hier erzeugt
     * und durchgereicht. */
    MainMemory memory = new MainMemory( MEMSIZE );
    SecondaryStorage secondaryStorage = new SecondaryStorage();
    MMU mmu = new MMU( memory, secondaryStorage ); // Nur die MMU hat Zugriff auf den Hauptspeicher
    CPU cpu = new CPU( mmu );
    MemoryManagerIF memoryManager = new MemoryManager( memory, secondaryStorage );
    ProcessManager processManager = new ProcessManager( memoryManager );
    
    SchedulerIF scheduler = new Scheduler( cpu, processManager );
    processManager.setScheduler( scheduler );
    
    cpu.setProcessManager( processManager );
    cpu.setScheduler( scheduler );
    mmu.setProcessManager(processManager);
    
    int pid = processManager.createProcess("init");
    SysLogger.writeLog( 0, "BootLoader: initial process created, pid: " + pid );

    SysLogger.writeLog( 0, "BootLoader: starting the cpu" );
    try {
      cpu.startTimer();
      cpu.operate();
    } catch( ShutdownException x ) {
      SysLogger.writeLog( 0, "BootLoader: shutting down" );
      processManager.destroyProcess(pid);
      SysLogger.closeLog();
    }
  }
}
