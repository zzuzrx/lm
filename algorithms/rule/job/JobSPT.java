package com.lm.algorithms.rule.job;

import java.util.Map;

import com.lm.domain.Machine;
import com.lm.domain.Operation;

/** 工件按照SPT规则选择指派的机器 */
public class JobSPT implements IJobRule {

//    @Override
    public double calPrio(Operation operation, Machine m) {
        return -operation.getProcessingTime(m);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
