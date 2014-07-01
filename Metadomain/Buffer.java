package com.lm.Metadomain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * @Description job's buffer class

 * @author:lm

 * @time:2013-11-6 下午06:04:05

 */
public class Buffer implements Iterable<Operation>{
/***************************属性域***********************************************************************/
	/** 缓冲区工序集合 **/
    private List<Operation> operations;
    /** BUFFER所属的机器 **/
    public Machine machine;
    
/***************************方法域***********************************************************************/

    /**
     * @Description construction of Buffer
     * @exception:
     */
    public Buffer() {
		// TODO Auto-generated constructor stub
    	operations = new ArrayList<Operation>();
	}

    /**
     * @Description construction of Buffer with params
     * @param machine
     * @exception:
     */
    public Buffer(Machine machine) {
        operations = new ArrayList<Operation>();
        this.machine = machine;
    }

    /**
     * @Description get the current job operations in buffer
     * @return
     */
    public List<Operation> getOperations() {
		return operations;
	}

	/**
	 * @Description set the current job operations in buffer
	 * @param operations
	 */
	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}

    /**
     * @Description 往缓冲区添加工序 
     * @param oper
     */
    public void addOperation(Operation oper) {
        operations.add(oper);
    }

    /**
     * @Description  清空缓冲区
     */
    public void clear() {
        operations.clear();
    }

    /**
     * @Description 获取缓冲区第index个工序
     * @param index
     * @return
     */
    public Operation get(int index) {
        if (index < 0 || index >= operations.size()) { throw new ArrayIndexOutOfBoundsException(
                "Buffer size is " + operations.size() + " but index is "
                        + index); }
        return operations.get(index);
    }

    /**
     * @Description 判断缓冲区是否为空
     * @return
     */
    public boolean isEmpty() {
        return getArrivedOperations().size() <= 0;
    }

    /**
     * @Description 从缓冲区删除实体
     * @param entity
     */
    public void removeOperation(Entity entity) {
        for (Operation operation : entity.getOperations()) {
            operations.remove(operation);
        }
    }

    /**
     * @Description 从缓冲区删除工序 
     * @param opera
     */
    public void removeOperation(Operation opera) {
            operations.remove(opera);
    }

    /**
     * @Description 获取缓冲区工序数量
     * @return
     */
    public int size() {
        return operations.size();
    }

    /**
     * @Description 获取已达工序集合
     * @return
     */
    public List<Operation> getArrivedOperations() {
        List<Operation> ret = new ArrayList<Operation>();
        for (Operation o : operations) {
            if(o.isArrived()){
                ret.add(o);
            }
        }
        return ret;
    }

    /* (non-Javadoc) override iterator Function
     * return operations.iterator();
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Operation> iterator() {
        return operations.iterator();
    }

	/**
	 * @Description 清空缓冲区内容
	 */
	public void operationClear() {
		// TODO Auto-generated method stub
		operations.clear();
	}

}
