package com.lm.algorithms.rule.job;

import com.lm.domain.Machine;
import com.lm.domain.Operation;

/** earliest finish time 工件选择机器，最早完成时间 */
public class JobEFT implements IJobRule {

    //@Override
	public double calPrio(Operation operation , Machine m) {
	        return - (m.getNextIdleTime() + operation.getProcessingTime(m));
	}
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
