package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;

/** 机器选择工件，先进先出优先 */
public class MachineFIFO implements IMachineRule {

//    @Override
    public double calPrio(Entity e) {
        return -e.getRelDateTime();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
