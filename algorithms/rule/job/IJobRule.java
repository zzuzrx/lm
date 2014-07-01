package com.lm.algorithms.rule.job;

import com.lm.algorithms.rule.IRule;
import com.lm.domain.Machine;
import com.lm.domain.Operation;

/**
 * @Description 工件选机器调度规则

 * @author:lm

 * @time:2013-11-6 下午05:39:21

 */
public interface IJobRule extends IRule {
    /**
     * 根据启发式规则选择该指派的机器
     * 
     * @param operation
     *            待调度工序
     * @param machines
     *            可处理机器集合
     * @return 选择调度该工序的机器
     */
	public double calPrio(Operation operation, Machine m);
}
