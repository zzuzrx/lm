package com.lm.algorithms.rule.job;

import java.util.Map;

import com.lm.domain.Machine;
import com.lm.domain.Operation;

public class JobLU implements IJobRule {

    //@Override
    public double calPrio(Operation operation, Machine m) {
        return -m.getUtilization();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
