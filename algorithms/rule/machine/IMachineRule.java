package com.lm.algorithms.rule.machine;

import com.lm.algorithms.rule.IRule;
import com.lm.domain.Entity;

/**
 * @Description 机器选择工件加工的调度规则

 * @author:lm

 * @time:2013-11-6 下午05:40:06

 */
public interface IMachineRule extends IRule {
    /**
     * 根据启发式规则选择调度的实体
     * 
     * @param machine
     *            空闲的机器
     * @param operations
     *            待调度的工序集合
     * @return 在该空闲机器上进行调度的实体
     */
    public double calPrio(Entity e);
}
