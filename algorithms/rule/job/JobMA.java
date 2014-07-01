package com.lm.algorithms.rule.job;

import com.lm.domain.Machine;
import com.lm.domain.Operation;

/**
 * 工件选机器，Most Available，机器缓冲区队列可用最长的有限 对于无限缓冲区大小的情况，则使用最短缓冲区有限
 */
public class JobMA implements IJobRule {

//    @Override
    public double calPrio(Operation operation, Machine m) {
        return -m.getBufferSize();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
