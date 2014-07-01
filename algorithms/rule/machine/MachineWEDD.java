package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;

public class MachineWEDD implements IMachineRule {

//	@Override
    public double calPrio(Entity e) {
        return e.getWeight() / e.getDueDate();
    }

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
