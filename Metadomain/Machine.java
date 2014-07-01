package com.lm.Metadomain;

import java.util.ArrayList;
import java.util.List;

import com.lm.algorithms.rule.machine.IMachineRule;
import com.lm.algorithms.rule.machine.MachineSPT;
import com.lm.util.Timer;

public class Machine {
/***************************属性域***********************************************************************/
	/** 机器id */
	private final int id;
	/** 机器名字 */
	private final String name;
	/** 机器所在单元 */
	private int numInCell;
	/** 机器缓冲区 */
	private Buffer buffer;
	/** 机器容量，批处理机>1 */
	private int capacity;
	/** 该机器的最早释放时间 */
	private int nextIdleTime;	
	/** 上次处理对象的工件族 */
	private int lastFamily;
	/** 该机器的工作时间 */
	private int workingTime;
	/** 正在加工的实体 */
	private Entity processingEntity;
	/** 机器决策工件的优先级序列 即 MachineSegment for current machine**/
	private	int[] PriorSequence;


	/***************************方法域***********************************************************************/	
	/**
	 * @Description  construction of machine
	 * @param id
	 * @param name
	 * @param capacity
	 * @exception:
	 */
	public Machine(int id, String name,int capacity) {
		this.id = id;
		this.name = name;
		this.capacity = capacity;
		this.nextIdleTime = 0;
		this.buffer = new Buffer(this);
		processingEntity = null;
		workingTime = 0;
		lastFamily = -1;
	}

	/**
	 * @Description 把工序添加到缓冲区队列中
	 * @param o
	 */
	public void addOperationToBuffer(Operation o) {
		buffer.addOperation(o);
	}

	/** 机器工作时间--暂不使用（FIXIT 计算存在错误）*/
	public void addWorkingTime() {
		workingTime += Timer.currentTime()
				- processingEntity.getOperations().get(0).getStartTime();
	}

	/**
	 * @Description 当前正在加工的实体名称
	 * @return
	 */
	public String GetProcessingEntityName(){
		if(processingEntity==null) return "暂空"; 
		else return processingEntity.getOperations().get(0).toString();
	}
	
	/**
	 * @Description 返回构建的调度实体--当前只针对不分批的情况
	 * @return
	 */
	public List<Entity> constructEntity() {

			List<Entity> entities = new ArrayList<Entity>();
			for (Operation o : buffer.getArrivedOperations()) {
				List<Operation> operations = new ArrayList<Operation>();
				operations.add(o);
				entities.add(new Entity(this, operations));
			}
			return entities;
	}
	
	/**
	 * @Description get job's Buffer of machine
	 * @return
	 */
	public Buffer getBuffer() {
		return buffer;
	}
	/**
	 * @Description set job's Buffer of machine
	 * @param buffer
	 */
	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;
	}
	
	/**
	 * @Description get the Priors of parts
	 * @return
	 */
	public int[] getPriorSequence() {
		return PriorSequence;
	}

	/**
	 * @Description Set array priorSequence to be the Priors of parts
	 * @param priorSequence
	 */
	public void setPriorSequence(int[] priorSequence) {
		PriorSequence = priorSequence;
	}
	
	/**
	 * @Description 获取缓冲区大小
	 * @return
	 */
	public int getBufferSize() {
		return buffer.getArrivedOperations().size();
	}

	/**
	 * @Description get operate's capacity of machine
	 * @return
	 */
	public int getCapacity() {
		return capacity;
	}
	/**
	 * @Description set operate's capacity of machine
	 * @param capacity
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNextIdleTime() {
		return nextIdleTime;
	}
	public void setNextIdleTime(int nextIdleTime) {
		this.nextIdleTime = nextIdleTime;
	}
	
	public Entity getProcessingEntity() {
		return processingEntity;
	}
	public void setProcessingEntity(Entity processingEntity) {
		this.processingEntity = processingEntity;
	}

    public int getCellID() {
    	return numInCell;
    }
    public void setCellID(int numInCell) {
        this.numInCell = numInCell;
    }
    
    public void setLastFamily(int lastFamily) {
        this.lastFamily = lastFamily;
    }
    
    public boolean isFamilySameWithLast(int family){
        return family == lastFamily;
    }
    
	/**
	 * @Description 获得设备利用率
	 * @return
	 */
	public double getUtilization() {
		if (Timer.currentTime() == 0) {
			return 0;
		}
		return 1.0 * workingTime / Timer.currentTime();
	}

	/**
	 * @Description 查询缓冲区是否为空
	 * @return
	 */
	public boolean isBufferEmpty() {
		return buffer.isEmpty();
	}

	/**
	 * @Description 把工序从缓冲区删除
	 * @param entity
	 */
	public void removeOperationFromBuffer(Entity entity) {
		buffer.removeOperation(entity);
	}

	/**
	 * @Description 选择该调度的实体 
	 * @return
	 */
	public Entity selectEntity() {
	    List<Entity> entities = constructEntity();
        for(int PriorJob:PriorSequence){
        	for (Entity entity : entities) {
        		if ( entity.getOperations().get(0).getJob().getId() == PriorJob) {
        	        //return selectEntity == null ? entities.get(0) : selectEntity;
        			return entity;
        		}
        	}
        }
        throw new IllegalArgumentException("Wrong Select parts for machine");
//        System.out.println(rule.toString()+","+max);
        //return selectEntity == null ? entities.get(0) : selectEntity;
	}

	/**
	 * @Description 重置机器的状态
	 */
	public void reset() {
		processingEntity = null;
		nextIdleTime = 0;
		workingTime = 0;
		buffer.clear();
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Machine other = (Machine) obj;
		if (capacity != other.capacity)
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

    /**
     * 获取从当前机器到目标机器的转移时间
     * @param m 目标机器*/
    /**
    public int getTransferTime(Machine m){
        return this.cell.getTransferTime(this.numInCell, m.getNumInCell());
    }
    ***/
    
    /**
     * 获取当前工件族的处理时间
     * */
    /***
    public int getSetupTime(int family){
        
    }
    ***/
	/**
	public void setRuleToBuffer(ITransportorRule rule) {
		buffer.setRule(rule);
	}
	**/
}
