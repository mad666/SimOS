/*
 * SimpleMainMemory.java
 *
 * Created on September 1, 2007, 12:28 PM
 *
 */

package Hardware;
import java.util.ArrayList;

import MainBoot.BootLoader;
import MemoryManagement.Frame;
import MemoryManagement.Page;

public class MainMemory {
  
  private ArrayList<Frame> memory = new ArrayList<Frame>();  
  private int size;
  private MMU mmu;
  
  
  
  //Konstruktoren
  public MainMemory( int size, MMU mmu ) {
    this.size = size;
    this.mmu = mmu;
    for( int i = 0; i < size; i++ ) {
      memory.add(i, new Frame(i));
    }
  }
  public MainMemory( int size ) {
    this.size = size;
    for( int i = 0; i < size; i++ ) {
      memory.add(i, new Frame(i));
    }
  }
  
  
  
  //Getter & Setter
  public String getContent( int address, int pid ){
    return mmu.getContent(address, pid);
  }
  public void setContent( Page page ){
	mmu.setContent(page);
  }
  
  public int getSize() {
    return size;
  }
  
  public Frame getFrame(int index) {
	  return memory.get(index);
  }
  public void setFrame(int index, Page page) {
	  memory.add(index, new Frame(index, page));
  }
  
  
    
  //Funktionen
}
