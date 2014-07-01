package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;

/** Shorest Remaining Processing Time */
public class MachineSRPT implements IMachineRule {

//    @Override
    public double calPrio(Entity e) {
        return -e.getRemainingTime();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
