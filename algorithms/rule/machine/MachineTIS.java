package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;

/** 机器选择工件，Time In System在系统时间越长越优先 */
public class MachineTIS implements IMachineRule {

//    @Overide
    public double calPrio(Entity e) {
        return e.getTimeInSystem();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
