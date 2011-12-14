package Hardware;

import java.util.ArrayList;

public class RegisterSet {
    private int programCounter;
    private String register1;
    private String register2;
    private SysConsole console;
    private ArrayList<String> stack;
    
    /** Creates a new instance of Register */
    public RegisterSet() {
      stack = new ArrayList<String>();
    }
    
    public int getProgramCounter() {
        return programCounter;
    }
    
    public void setProgramCounter(int address) {
        programCounter = address;
    }
    
    public String getRegister1() {
        return register1;
    }

    public void setRegister1(String value) {
        register1 = value;
    }

    public String getRegister2() {
        return register2;
    }

    public void setRegister2(String value) {
        register2 = value;
    }    
    
    public SysConsole getConsole() {
      return console;
    }
    
    public void setConsole( SysConsole console ) {
      this.console = console;
    }
}

