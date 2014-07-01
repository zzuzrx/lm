package com.lm.algorithms.rule.machine;

import com.lm.domain.Entity;
import com.lm.domain.Machine;
import com.lm.util.Timer;

/**
 * Current time - arrival time + remaining process time Largest first
 */
public class MachineAT_RPT implements IMachineRule {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }


    public double calPrio(Entity e) {
        // Current time - arrival time + remaining process time Largest first
        return Timer.currentTime() - e.getRelDateTime() + e.getRemainingTime();
    }

}
