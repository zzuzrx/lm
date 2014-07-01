package com.lm.algorithms.rule.transportor;

import java.util.ArrayList;
import java.util.List;

import com.lm.domain.Cell;
import com.lm.domain.Machine;
import com.lm.domain.Operation;
import com.lm.util.Constants;
/** Operation Time + Transfer Time */
public class TransOperAndTrans implements ITransportorRule {

	/**当前未完成的部分——添加每个entity中每个工件的nextCell属性
	 * 相应的，是不是在Main里面添加 一种方法：输入（工序、机器ID号） 处理过程：将该工件加入到机器的队列当中
	 * **/
    //@Override
    public List<Operation> constructTransBatch(Cell c) {
        List<Operation> transBatch = new ArrayList<Operation>();
    	/**
        int count = 0;
        List<Operation> opers = new ArrayList<Operation>();
        for (Operation o : operations) {
            if (count >= m.getCapacity()) {
                entities.add(new Entity(m, opers));
                opers = new ArrayList<Operation>();
                count = 0;
            }
            opers.add(o);
            count++;
        }
        if (!opers.isEmpty()) {
            entities.add(new Entity(m, opers));
        }
        **/
        /**test版本，取第一个有工件队列的前两个*/
        int t;
        for (t = 0; c.getBuffer(t).size()==0; t++); 
        for (int i=0;i<c.getBuffer(t).size()&&i<c.getVehicle().getCapacity();i++){
        	transBatch.add(c.getBuffer(t).get(i));
        }
        
        return transBatch;
    }

    public double calPrio(Operation e,int CurCellID,int NextCellID){
        /**单元间转移时间**/
    	double timeForCell=Constants.transferTime[CurCellID][NextCellID];
//    	System.out.println(timeForCell);
    	/**工序在单元上的加工时间**/
    	double timeForOper;
    	List<Machine> a=e.getProcessMachineList();
    	int MachineIndex=0;
    	while(a.get(MachineIndex).getCellID()!=NextCellID){
    		MachineIndex++;
    	}
    	timeForOper=e.getProcessingTime(a.get(MachineIndex));
//    	System.out.println(timeForOper);
    	return -timeForCell-timeForOper;
    }
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
