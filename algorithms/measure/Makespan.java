package com.lm.algorithms.measure;

import com.lm.algorithms.AbstractScheduler;
import com.lm.algorithms.MetaHeuristicScheduler;
import com.lm.domain.Job;

/**
 * @Description use the makespan as Measurance

 * @author:lm

 * @time:2013-11-6 下午05:34:14

 */
public class Makespan implements IMeasurance,MetaIMeasurance {
//public class Makespan implements MetaIMeasurance {


//    @Override
    public double getMeasurance(AbstractScheduler scheduler) {
    		int max = Integer.MIN_VALUE;
    		for (com.lm.domain.Job job : scheduler.jobSet) {
    			max = Math.max(max, job.getFinishedTime());
    		}
    		return max;
    }
    
    public double getMeasurance(MetaHeuristicScheduler scheduler) {
		int max = Integer.MIN_VALUE;
		for (com.lm.Metadomain.Job job : scheduler.jobSet) {
			max = Math.max(max, job.getFinishedTime());
		}
		return max;
    }
}
