package com.lm.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lm.algorithms.SimpleScheduler;
import com.lm.util.Timer;

import ec.gp.GPIndividual;

/**
 * @Description TODO

 * @author:lm

 * @time:2013-11-6 下午11:42:15

 */
public class Operation {
/***************************属性域***********************************************************************/
	/** 工序ID */
    private final int id;
    /** 工序名称 */
    private final String name;
    /** 上一道工序 */
    private Operation prev;
    /** 下一道工序 */
    private Operation next;
    /**
     * 可选机器处理时间 key 机器id value 处理时间
     */
    private Map<Machine, Integer> procTimes;
//  private Map<Machine, Integer> setupTimes;
    /** 工件引用 */
    private Job job;
    /** 开始时间 */
    private int startTime;
    /** 结束时间 */
    private int endTime;
    /** 到达时间 */
    private int arrivalTime;
    /** 调度该工序的机器 */
    private Machine processingMachine;
    /** 调度后下一道工序的机器ID*/
    private int NextMachineID;
    /** 评分值**/
    private double score;
/***************************方法域***********************************************************************/	
	/**
	 * @Description construction of operation
	 * @param id
	 * @param name
	 * @exception:
	 */
	public Operation(int id, String name) {
        this.id = id;
        this.name = name;
        startTime = 0;
        endTime = 0;
        prev = null;
        next = null;
        procTimes = new HashMap<Machine, Integer>();
//        setupTimes = new HashMap<Machine, Integer>();
    }
	
    public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	
    public int getProcessingTime(Machine m) {
        //TODO 需要考虑约束的生产准备时间
//        return procTimes.get(m) + setupTimes.get(m);
        return procTimes.get(m);
    }
    
    public int getFamily(){
        return job.getFamily();
    }

    /** 获取可加工机器列表 */
    public List<Machine> getProcessMachineList() {
        return new ArrayList<Machine>(procTimes.keySet());
    }

    /** 获取剩余时间 */
    public int getRemainingTime() {
        return job.getRemainingTime();
    }

    public int getDueDate() {
        return job.getDuedate();
    }

    public int getRelDate() {
        return job.getRelDate();
    }

    /**
     * @Description 得到工序到达机器的时间
     * @return
     */
    public int getArrivalTime() {
        if (arrivalTime < job.getRelDate()) { return job
                .getRelDate(); }
        return arrivalTime;
    }

    /**
     * @Description 获取在工件在系统的停留时间
     * @return
     */
    public int getTimeInSystem() {
        int tis = Timer.currentTime() - job.getRelDate();
        if (tis < 0) { throw new RuntimeException(
                "!!!Unarrival Job Have Got The Schedule!!!"); }
        return Timer.currentTime() - job.getRelDate();
    }

    /**
     * @Description reset operation's states
     */
    public void reset() {
        startTime = 0;
        endTime = 0;
        arrivalTime = 0;
        processingMachine = null;
    }
    
  

    /**
     * @Description 获取前一道工序 
     * @return
     */
    public Operation getPrev() {
        return prev;
    }
    public void setPrev(Operation prev) {
        this.prev = prev;
    }

    /**
     * @Description 获取下一道工序 
     * @return
     */
    public Operation getNext() {
        return next;
    }
    public void setNext(Operation next) {
        this.next = next;
    }

    public Map<Machine, Integer> getProcTimes() {
        return procTimes;
    }
    public void setProcTimes(Map<Machine, Integer> procTimes) {
        this.procTimes = procTimes;
    }

//    public Map<Machine, Integer> getSetupTimes() {
//        return setupTimes;
//    }
//
//    public void setSetupTimes(Map<Machine, Integer> setupTimes) {
//        this.setupTimes = setupTimes;
//    }

    public Job getJob() {
        return job;
    }
    public void setJob(Job job) {
        this.job = job;
    }

    public int getStartTime() {
        return startTime;
    }
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }
    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public Machine getProcessingMachine() {
        return processingMachine;
    }
    public void setProcessingMachine(Machine machine) {
        this.processingMachine = machine;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
	/**
	 * @Description 工序是否已达（可加工）
	 * @return
	 */
	public boolean isArrived(){
	    return arrivalTime <= Timer.currentTime();
	}
	
	/**
	 * @Description 获取 下一次要加工的机器ID
	 * @return
	 */
	public int GetNextMachineID(){
		return NextMachineID;
	}
	public void SetNextMachineID(int id){
		NextMachineID=id;
	}
	
    public int getSetupTime(Machine m){
        if (!m.isFamilySameWithLast(getFamily())) {
            return 0;
        }
        return 0;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {//remove会用到，所以注意这个equals
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Operation other = (Operation) obj;
		if (id != other.id) return false;
		if (name == null) {
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		else if(this.job.getId() != ((Operation)obj).getJob().getId()) return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
