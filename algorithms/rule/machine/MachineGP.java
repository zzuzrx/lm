package com.lm.algorithms.rule.machine;

import com.lm.algorithms.SimpleScheduler;
import com.lm.domain.Entity;
import ec.gp.GPIndividual;

/**
 * GP引入的节点
 */
public class MachineGP implements IMachineRule {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public double calPrio(Entity e) {
    	 // 端点变量赋值
        SimpleScheduler.GPInfo.seqProblem.weight = e.getWeight();
        SimpleScheduler.GPInfo.seqProblem.duedate = e.getDueDate();
        SimpleScheduler.GPInfo.seqProblem.procTime = e.getProcessingTime();
        SimpleScheduler.GPInfo.seqProblem.arrivalTime = e.getArrivalTime();
        SimpleScheduler.GPInfo.seqProblem.releaseTime = e.getRelDateTime();
        SimpleScheduler.GPInfo.seqProblem.remainingProcTime = e.getRemainingTime();
        SimpleScheduler.GPInfo.seqProblem.opNumber = e.getOpNumber();
//        SimpleScheduler.GPInfo.seqProblem.fullnessOfBatch = e.getFullnessOfBatch();

        // 计算表达式结果
        ((GPIndividual) SimpleScheduler.GPInfo.ind).trees[0].child.eval(
                SimpleScheduler.GPInfo.state, SimpleScheduler.GPInfo.threadnum,
                SimpleScheduler.GPInfo.input, SimpleScheduler.GPInfo.stack,
                (GPIndividual) SimpleScheduler.GPInfo.ind,
                SimpleScheduler.GPInfo.seqProblem);
        double result = SimpleScheduler.GPInfo.input.x;
        return result;
    }	
}
