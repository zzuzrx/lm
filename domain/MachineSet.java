package com.lm.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * @Description:机器集合的类

 * @author:lm

 * @time:2013-11-6 下午03:53:33

 */
public class MachineSet implements Iterable<Machine> {
/***************************属性域***********************************************************************/
	/** 机器集合 */
	private List<Machine> machines;
/***************************方法域***********************************************************************/	    
    /**
     * @Description  construction of MachineSet
     * @exception:
     */
    public MachineSet() {
        machines = new ArrayList<Machine>();
    }

    /**
     * @Description add a machine to MachineSet
     * @param machine
     */
    public void add(Machine machine) {
        machines.add(machine);
    }

    /**
     * @Description get the machine in position index of MachineSet
     * @param index
     * @return
     */
    public Machine get(int index) {
        return machines.get(index);
    }

    

    /**
     * @Description judge whether the MachineSet in are all idle by time t
     * @param t
     * @return
     */
    public boolean isIdleAll(int t) {
        for (Machine m : machines) {
            if (m.getNextIdleTime() > t) { return false; }
        }
        return true;
    }
    
    /**
     * @Description get size of MachineSet
     * @return
     */
    public int size(){
    	return machines.size();
    }

    /**
     * @Description reset MachineSet states
     */
    public void reset() {
        for (Machine machine : machines) {
            machine.reset();
        }
    }

//    @Override
    public Iterator<Machine> iterator() {
        return machines.iterator();
    }
}
