package com.lm.domain;

import java.util.ArrayList;
import java.util.List;

import com.lm.algorithms.rule.machine.IMachineRule;
import com.lm.algorithms.rule.machine.MachineSPT;
import com.lm.util.Constants;
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
	/** 该机器使用的调度规则 */
	private IMachineRule rule;
	/** 正在加工的实体 */
	private Entity processingEntity;
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
		this.rule = new MachineSPT();
		processingEntity = null;
		workingTime = 0;
		/**
		if (capacity > 1) {
			buffer.setRule(new TransFIFO());//原来是SPT，改成了FIFO
		}
		**/
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
	 * @param machineToPartsFunc 
	 * @Description 选择该调度的实体
	 * @return
	 */
	public Entity selectEntity(double[][] machineToPartsFunc) {
	    List<Entity> entities = constructEntity();
        Entity selectEntity = null;
        double max = -Double.MAX_VALUE;
        for (Entity entity : entities) {
            double priority = rule.calPrio(entity);
            
            /**找到对应工件在Constants.MachineToParts中的位置，然后更新Func的值**/
            for(int i = 1; i < Constants.MachineToParts.length; i++){
            	for(int j = 1; j < Constants.MachineToParts[i].length; j++){
            		if(Constants.MachineToParts[i][j] == entity.getOperations().get(0).getJob().getId() ){
            			//是否更新过值，若没有，附上新值
            			if(machineToPartsFunc[i][j] == Double.MAX_VALUE) {
            				machineToPartsFunc[i][j] = -priority;
            			}
            			//若更新过，与新值的结果取平均
            			else{
            				machineToPartsFunc[i][j] = (-priority+machineToPartsFunc[i][j])/2;
            			}
            		}
            	}
            }
            
            if (priority > max) {
                max = priority;
                selectEntity = entity;
            }
        }
//        System.out.println(rule.toString()+","+max);
        //return selectEntity == null ? entities.get(0) : selectEntity;
        return selectEntity;
	}

	/**
	 * @Description Set machine's rule 
	 * @param rule
	 */
	public void setRule(IMachineRule rule) {
		this.rule = rule;
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
