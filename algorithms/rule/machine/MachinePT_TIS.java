package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;

/**
 * Process time / time in system smallest first
 */
public class MachinePT_TIS implements IMachineRule {
//    @Override
	  public double calPrio(Entity e) {
	        // Process time / time in system smallest first
	        int proc = e.getProcessingTime();
	        int tis = e.getTimeInSystem();
	        double pt_tis = 0;
	        if (tis == 0) {
	            pt_tis = Double.MAX_VALUE / 100;
	        }
	        else {
	            pt_tis = 1.0 * proc / tis;
	        }
	        return -pt_tis;
	    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
