package com.lm.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * @Description:工件集合的类

 * @author:lm

 * @time:2013-11-6 下午03:53:33

 */
public class JobSet implements Iterable<Job> {
/***************************属性域***********************************************************************/
	/** 工件集合 */
	private List<Job> jobs;

/***************************方法域***********************************************************************/	
    /**
     * @Description construction of JobSet
     * @exception:
     */
    public JobSet() {
        jobs = new ArrayList<Job>();
    }
    
    /**
     * @Description get size of JobSet
     * @return
     */
    public int size(){
    	return jobs.size();
    }

    /**
     * @Description add a job to JobSet
     * @param job
     */
    public void addJob(Job job) {
        jobs.add(job);
    }
    
    /**
     * @Description get the job in position index of JobSet
     * @param index
     * @return
     */
    public Job get(int index){
        return jobs.get(index);
    }

    /**
     * @Description  judge whether the JobSet completely scheduled or not
     * @return
     */
    public boolean isScheduleAll() {
        for (Job job : jobs) {
            if (job.isCompleted() == false) return false;
        }
        return true;
    }

    /**
     * @Description reset JobSet states
     */
    public void reset() {
        for (Job job : jobs) {
            job.reset();
        }
    }

//    @Override
    public Iterator<Job> iterator() {
        return jobs.iterator();
    }
}
