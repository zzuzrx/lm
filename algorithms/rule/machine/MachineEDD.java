package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;

/** 机器选择工件，最早交货期优先 */
public class MachineEDD implements IMachineRule {

//    @Override
    public double calPrio(Entity e) {
        return -e.getDueDate();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
