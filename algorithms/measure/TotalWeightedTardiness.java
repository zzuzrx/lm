package com.lm.algorithms.measure;

import com.lm.algorithms.AbstractScheduler;
import com.lm.algorithms.MetaHeuristicScheduler;
import com.lm.domain.Job;
import com.lm.util.Utils;

/**
 * @Description use TWT as Measurance

 * @author:lm

 * @time:2013-11-6 下午05:35:24

 */
public class TotalWeightedTardiness implements IMeasurance,MetaIMeasurance {

    //@Override
    public double getMeasurance(AbstractScheduler scheduler) {
		double twt = 0.0f;
		for (com.lm.domain.Job job : scheduler.jobSet) {
			//System.out.println("Job"+job.getId()+":"+job.getFinishedTime());
			twt += job.getTardiness() * job.getWeight();
		}
		return twt;
    }

    public double getMeasurance(MetaHeuristicScheduler scheduler) {
		double twt = 0.0f;
		for (com.lm.Metadomain.Job job : scheduler.jobSet) {
			//System.out.println("Job"+job.getId()+":"+job.getFinishedTime());
			twt += job.getTardiness() * job.getWeight();
		}
		return twt;
    }
}
