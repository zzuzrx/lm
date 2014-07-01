package com.lm.algorithms.rule.job;

import com.lm.domain.Machine;
import com.lm.domain.Operation;

/** First Available工件选机器，最先可用优先 */
public class JobFA implements IJobRule {

    //@Override
    public double calPrio(Operation operation, Machine m) {
        return -m.getNextIdleTime();
    }
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
