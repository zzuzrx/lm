package com.lm.algorithms.rule.transportor;

import java.util.List;

import com.lm.algorithms.SimpleScheduler;
import com.lm.util.Constants;
import com.lm.util.Timer;
import com.lm.util.Utils;

import ec.gp.GPIndividual;

import com.lm.domain.Machine;
import com.lm.domain.Operation;
/** FIFO : RealDate*/
public class TransGP implements ITransportorRule {
    
	public double calPrio(Operation e,int CurCellID,int NextCellID){
	    
		//计算opertion的时间
    	List<Machine> a=e.getProcessMachineList();
    	int MachineIndex=0;
    	while(a.get(MachineIndex).getCellID()!=NextCellID){
    		MachineIndex++;
    	}
    	double  timeForOper=e.getProcessingTime(a.get(MachineIndex));

    	//    	System.out.println("加工时间"+timeForOper);
    	//    	System.out.println("转移时间"+"["+CurCellID+","+NextCellID+"]--"+Constants.transferTime[CurCellID][NextCellID]);
//    	 		System.out.println(e.getDueDate());
    	// 端点变量赋值
		SimpleScheduler.GPInfo.batProblem.duedate=e.getDueDate();
		SimpleScheduler.GPInfo.batProblem.procTime=timeForOper;
		SimpleScheduler.GPInfo.batProblem.transTime=Constants.transferTime[CurCellID][NextCellID];
//		SimpleScheduler.GPInfo.batProblem.releaseTime=e.getRelDate();
		
		// 计算表达式结果
        ((GPIndividual) SimpleScheduler.GPInfo.ind).trees[0].child.eval(
                SimpleScheduler.GPInfo.state, SimpleScheduler.GPInfo.threadnum,
                SimpleScheduler.GPInfo.input, SimpleScheduler.GPInfo.stack,
                (GPIndividual) SimpleScheduler.GPInfo.ind,
                SimpleScheduler.GPInfo.batProblem);
        double result = SimpleScheduler.GPInfo.input.x;
        
//        System.out.println(result);
        if(result< -10000000) result=-10000000;
        else if(result > 10000000) result=10000000;
        
        return result;
    }
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
