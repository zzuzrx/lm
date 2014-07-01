package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;

/** 机器按照SPT规则选择工序 */
public class MachineSPT implements IMachineRule {

//    @Override
    public double calPrio(Entity e) {
        return -e.getProcessingTime();
    }


    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
