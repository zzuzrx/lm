package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;

public class MachineWSPT implements IMachineRule {

//	@Override
    public double calPrio(Entity e) {
        return e.getWeight() / e.getProcessingTime();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
