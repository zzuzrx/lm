package com.lm.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.lm.algorithms.rule.job.IJobRule;
import com.lm.algorithms.rule.job.JobFA;
import com.lm.util.ListHelper;

/**
 * @Description job class

 * @author:lm

 * @time:2013-11-6 下午11:07:15

 */
public class Job implements Iterable<Operation> {
/***************************属性域***********************************************************************/
	/** 工件ID */
    private int id;
    /** 工件名称 */
    private String name;
    /** 工件所含工序集合 */
    private List<Operation> operations;
    /** 工件释放时间 ,即进入系统的时间 */
    private int relDate;
    /** 工件权重 */
    private double weight;
    /** 工件交货期 */
    private int duedate;
    /** 工件数量 */
    private int amount;
    /** 工件大小 */
    private int size;
    /** 工件所属工件族 */
    private int family;
    /** 下次调度工序号 */
    private int nextScheduleNo;
    /** 是否完工 */
    private IJobRule rule;
    /** 剩余时间Cache */
    private Map<Operation, Integer> remainingTimeMap;
    /** 下一个加工的机器*/
    private int NextMachineID;

/***************************方法域***********************************************************************/    
    /**
     * @Description construction of job
     * @param id
     * @param name
     * @exception:
     */
    public Job(int id, String name) {
        this.id = id;
        this.name = name;
        this.weight = 1.0f;
        this.amount = 1;
        this.nextScheduleNo = 1;
        operations = new ArrayList<Operation>();
        remainingTimeMap = new HashMap<Operation, Integer>();
        rule = new JobFA();
    }

    /**
     * @Description 在单元内和单元外都可以调度下一道工序时，通过决策，决定下一步是跨单元还是不跨单元
     * @param operation
     * @return
     */
    public boolean selectCell(Operation operation) {
    	//当前对于这一个分支，默认优先在单元内加工
    	/**
    	Machine result=rule.selectMachine(operation);
    	setNextMachineID(result.getId());
    	**/
        return false;
    }
    
    /**
     * @Description 选择调度的机器
     * @param operation
     * @return
     */
    public Machine selectMachine(Operation operation) {
        // higher priority is preferred
        double max = -Double.MAX_VALUE;
        Machine selectedMachine = null;
        for (Machine machine : operation.getProcessMachineList()) {
            double score = rule.calPrio(operation, machine);
            if (score > max) {
                max = score;
                selectedMachine = machine;
            }
        }
    	setNextMachineID(selectedMachine.getId());
        return selectedMachine;
    }

	/**
	 * @Description 返回当前的调度工序
	 * @return
	 */
	public Operation getCurrentOperation() {
		// TODO Auto-generated method stub
		if (nextScheduleNo > operations.size()) { return null; }
		return operations.get(nextScheduleNo-1 -1);
	}
    
    /**
     * @Description 返回下一个调度工序
     * @return
     */
    public Operation getNextScheduleOperation() {
        if (nextScheduleNo > operations.size()) { return null; }
        return operations.get(nextScheduleNo - 1);
    }

    /**
     * @Description 返回上一道工序的机器
     * @return
     */
    public Machine getLastMachine() {
        if (nextScheduleNo == 1) { return null; }
        return operations.get(nextScheduleNo - 1 -1).getProcessingMachine();
    }

    /**
     * @Description 获取 tardiness
     * @return
     */
    public int getTardiness() {
        return Math.max(operations.get(operations.size() - 1).getEndTime()
                - duedate, 0);
    }

    /**
     * @Description 获取最后一道工序的完工时间
     * @return
     */
    public int getFinishedTime() {
        if (!isCompleted()) { // 未完成
            return -1;
        }
        return ListHelper.getLast(operations).getEndTime();
    }

    /**
     * @Description 获取剩余时间 
     * @return
     */
    public int getRemainingTime() {
        if (remainingTimeMap.get(getNextScheduleOperation()) != null) { return remainingTimeMap
                .get(getNextScheduleOperation()); }
        int remainingTime = 0;
        for (int index = nextScheduleNo-1; index < operations.size(); index++) {
            Operation oper = operations.get(index);
            int total = 0;
            int count = 0;
            for (Map.Entry<Machine, Integer> entry : oper.getProcTimes()
                    .entrySet()) {
                int processingTime = entry.getValue();
                //TODO 忽略了生产准备时间的计算
//                int setupTime = oper.getSetupTimes().get(machine);
//                total += processingTime + setupTime;
                total = processingTime;
                count++;
            }
            remainingTime += total / count;
        }
        remainingTimeMap.put(getNextScheduleOperation(), remainingTime);
        return remainingTime;
    }

    /**
     * @Description judge whether the job completely scheduled or not
     * @return
     */
    public boolean isCompleted() {
        if (nextScheduleNo > operations.size()) return true;
        else return false;
    }

    /**
     * @Description reset job states
     */
    public void reset() {
        nextScheduleNo = 1;
        for (Operation operation : operations) {
            operation.reset();
        }
    }

    /**
     * @Description schedule current opeartion
     */
    public void scheduleOperation() {
        /**
         * 计算工序到达时间的逻辑移到工件assigment逻辑中
         * {@link com.lqq.algorithms.SimpleScheduler#assignOperationToMachine(Job job)
         * assign}
         */
        nextScheduleNo++;// 后移下一道待调度工序
    }

    /**
     * @Description set operations to the job
     * @param operations
     */
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
        for (Operation operation : operations) {
            operation.setJob(this);
        }
    }
    

    /**
     * @Description 返回第几道工序
     * @param index--工序索引值
     * @return
     */
    public Operation getOperation(int index) {
        if (index < 0 || index >= operations.size()) { throw new ArrayIndexOutOfBoundsException(
                index); }
        return operations.get(index);
    }

    /**
     * @Description 返回工序数目
     * @return
     */
    public int getOperationNum() {
        return operations.size();
    }

    @Override
    public String toString() {
        return name;
    }

//    @Override
    public Iterator<Operation> iterator() {
        return operations.iterator();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRelDate() {
        return relDate;
    }

    public void setRelDate(int relDate) {
        this.relDate = relDate;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getDuedate() {
        return duedate;
    }

    public void setDuedate(int duedate) {
        this.duedate = duedate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getNextScheduleNo() {
        return nextScheduleNo;
    }

    public void setRule(IJobRule rule) {
        this.rule = rule;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFamily() {
        return family;
    }

    public void setFamily(int family) {
        this.family = family;
    }

    public int getNextMachineID() {
        return NextMachineID;
    }
    
    public void setNextMachineID(int id) {
        NextMachineID = id;
    }

}
