package com.lm.Metadomain;

import java.util.Iterator;
import java.util.List;
import com.lm.util.Constants;
import com.lm.util.Timer;
import com.lm.Metadomain.Operation;

/**
 * @Description 被调度的工件实体的类
 
 * 为了满足后续对于批处理工序的扩展，把machine中每一次调度的一个或多个工件作为一个实体来看待

 * @author:lm

 * @time:2015-04-11上午10:10:47

 */
public class Entity implements Iterable<Operation>{
/***************************属性域***********************************************************************/
    /** 该实体所含工序 */
    private List<Operation> operations;
    /** 该实体的处理时间 */
    private int processingTime;
    /** 该实体的准备时间*/
    private int setupTime;
    /** 该实体的权重*/
    private double weight;
    /** 调度该实体的机器 */
    private Machine machine;
    /** 该实体的交货期 */
    private int duedate = -1;
    /** 该实体的到达时间 */
    private int arrivalTime = -1;
    /** 该实体进入系统的时间 */
    private int relDate = -1;
    /** 该实体剩余的加工时间 */
    private int remainingTime = -1;

/***************************方法域***********************************************************************/    
    /**
     * @Description construction of entity
     * @param machine
     * @param operations
     * @exception:
     */
    public Entity(Machine machine, List<Operation> operations) {
        this.operations = operations;
        this.machine = machine;
        calProcessingTime();
        calWeight();
    }

    /**
     * @Description get average operations of entity as entity's operations size of 计算过程有点疑惑
     * @return
     */
    public double getOpNumber() {
        double total = 0;
        for (Operation op : operations) {
            double rOp = op.getJob().getOperationNum() - op.getId() + 1;
            total += rOp;
        }
        return total / operations.size();
    }
    
    /**
     * @Description get the earliest arrival time for entity which be seed as the arrival time of this entity
     * @return
     */
    public int getArrivalTime() {
        if (arrivalTime != -1) {
        	return arrivalTime; 
        }
        int min = Integer.MAX_VALUE;
        for (Operation operation : operations) {
            min = Math.min(min, operation.getArrivalTime());
        }
        arrivalTime = min;
        return min;
    }

    /**
     * @Description get the duedate of entity
     * @return
     */
    public int getDueDate() {
        if (duedate != -1) { 
        	return duedate; 
        }
        int min = Integer.MAX_VALUE;
        for (Operation operation : operations) {
            min = Math.min(min, operation.getDueDate());
        }
        duedate = min;
        return min;
    }

    /**
     * @Description get the time of entity that it join the system
     * @return
     */
    public int getRelDateTime() {
        if (relDate != -1) { 
        	return relDate; 
        }
        int min = Integer.MAX_VALUE;
        for (Operation operation : operations) {
            min = Math.min(min, operation.getRelDate());
        }
        relDate = min;
        return min;
    }

    /**
     * @Description get the remaining time of entity 
     * @return
     */
    public int getRemainingTime() {
        if (remainingTime != -1) { 
        	return remainingTime; 
        }
        int max = Integer.MIN_VALUE;
        for (Operation operation : operations) {
            max = Math.max(max, operation.getRemainingTime());
        }
        remainingTime = max;
        return remainingTime;
    }

    /**
     * @Description get current system time
     * @return
     */
    public int getTimeInSystem() {
        return Timer.currentTime() - getRelDateTime();
    }

    /**
     * @Description set start time for entity
     * @param time
     */
    public void setStartTime(int time) {
        for (Operation operation : operations) {
            operation.setStartTime(time);
        }
    }

    /**
     * @Description get setup time for entity :获取实体准备时间
     * @param MachineID
     * @return
     */
    public int GetSetupTime(int MachineID) {
    	//一般来说，entity均为同一工件族，那么entity的setupTime==实体中第一个工件的setupTime
    	return Constants.setupTime[operations.get(0).getFamily()][MachineID];
    }
    
    /**
     * @Description set end time for entity
     * @param time
     */
    public void setEndTime(int time) {
        for (Operation operation : operations) {
            operation.setEndTime(time);
        }
    }

    /**
     * @Description set 'machine' to operate the entity 
     * @param machine
     */
    public void setProcessingMachine(Machine machine) {
        for (Operation operation : operations) {
            operation.setProcessingMachine(machine);
        }
    }

    /**
     * @Description process of scheduling All operations (调度实体中的所有工序)
     */
    public void scheduleOperations() {
        for (Operation operation : operations) {
            Job parentJob = operation.getJob();
            if (!parentJob.isCompleted()) {
                parentJob.scheduleOperation();
            }
        }
    }

    /**
     * @Description calculate the processing time of entity
     */
    private void calProcessingTime() {
//      int maxSetupTime = Integer.MIN_VALUE;
        int maxProcessTime = Integer.MIN_VALUE;
        for (Operation operation : operations) {
            maxProcessTime = Math.max(maxProcessTime, operation.getProcTimes()
                    .get(machine));
//            maxSetupTime = Math.max(maxSetupTime, operation.getSetupTimes()
//                    .get(machine));
        }
        processingTime = maxProcessTime;
//      processingTime = maxProcessTime + maxSetupTime;
    }
    
    /**
     * @Description calculate the weight of entity
     */
    private void calWeight(){
    	double maxWeight = Double.MIN_VALUE;
    	for (Operation oper : operations) {
		maxWeight = Math.max(maxWeight, oper.getJob().getWeight());
	}
    	this.weight = maxWeight;
    }

    /**
     * @Description get all Operations of entity
     * @return
     */
    public List<Operation> getOperations() {
        return operations;
    }

    /**
     * @Description get processing time
     * @return
     */
    public int getProcessingTime() {
        return processingTime;
    }

    /**
     * @Description get the machine 
     * @return
     */
    public Machine getMachine() {
        return machine;
    }

    /**
     * @Description get the weight
     * @return
     */
    public double getWeight() {
	return weight;
    }

    @Override
    public String toString(){
	return getOperations().get(0).toString();
    }
    
    public Iterator<Operation> iterator() {
        return operations.iterator();
    }
}
