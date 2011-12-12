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

public class MainMemory {
  
  private ArrayList<Frame> memory = new ArrayList<Frame>();  
  private int size;
  
  /** Creates a new instance of SimpleMainMemory */
  public MainMemory( int size ) {
    this.size = size;
    for( int i = 0; i < size; i++ ) {
      memory.add(i, new Frame(i));
    }
  }
  
  public String getContent( int address ){
    return mmu.getContent(address);
  }
  
  public void setContent( String value ){
    mmu.setContent(value );
  }
  
  public int getSize() {
    return size;
  }

}
