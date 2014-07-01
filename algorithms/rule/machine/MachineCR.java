package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;
import com.lm.util.Timer;

/**
 * Critical Ratio = (due date - current time)/ RPT smallest first
 */
public class MachineCR implements IMachineRule {

//    @Override
	   public double calPrio(Entity e) {
	        // Critical Ratio = (due date - current time)/ RPT smallest first
		   if(e.getRemainingTime()!=0){
			   	return -1.0 * (e.getDueDate() - Timer.currentTime()) / e.getRemainingTime();
		   }else{
			return -100000;   
		   }
	    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
