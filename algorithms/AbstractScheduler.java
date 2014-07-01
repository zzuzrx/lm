package com.lm.algorithms;

import java.io.IOException;

import com.lm.domain.CellSet;
import com.lm.domain.Job;
import com.lm.domain.JobSet;
import com.lm.domain.MachineSet;
import com.lm.util.Timer;

/**
 * @Description schedule class for all job shop problems in evaluation process

 * @author:lm

 * @time:2013-11-6 下午05:46:43

 */
public abstract class AbstractScheduler {
/***************************属性域***********************************************************************/
	/**job's set **/
	public JobSet jobSet;
	/**machine's set **/
	protected MachineSet machineSet;
	/**cell's set **/
    protected CellSet cellSet;
    

/***************************方法域***********************************************************************/
	/**
	 * @Description construct functions
	 * @param machineSet
	 * @param jobSet
	 * @param cellSet
	 * @exception:
	 */
	public AbstractScheduler(MachineSet machineSet, JobSet jobSet,CellSet cellSet) {
		this.jobSet = jobSet;
		this.machineSet = machineSet;
		this.cellSet = cellSet;
		Timer.startTimer();
	}

	/**
	 * @throws IOException 
	 * @Description ths Concrete schedule process for different job shop problemss 
	 */
	public abstract void schedule() throws IOException;
	
	/**
	 * @Description reset the state for all attributes
	 */
	public void reset() {
		jobSet.reset();
		machineSet.reset();
		cellSet.reset();
	}
	
	/** 
	public int getMakespan() {
		int max = Integer.MIN_VALUE;
		for (Job job : jobSet) {
			max = Math.max(max, job.getFinishedTime());
		}
		return max;
	}

	public double getTotalWeightedTardiness() {
		//统计twt结果
		double twt = 0.0f;
		for (Job job : jobSet) {
			twt += job.getTardiness() * job.getWeight();
		}
		return twt;
	}
	 **/
}
