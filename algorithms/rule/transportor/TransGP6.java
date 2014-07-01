package com.lm.algorithms.rule.transportor;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;
import com.lm.domain.Operation;
import com.lm.algorithms.rule.GPRuleBase;
/**
 * GP引入的节点
 */
public class TransGP6 extends GPRuleBase implements ITransportorRule {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public double calPrio(Operation e,int CurCellID,int NextCellID){

   	List<Machine> a=e.getProcessMachineList();
   	int MachineIndex=0;
   	while(a.get(MachineIndex).getCellID()!=NextCellID){
   		MachineIndex++;
   	}
   	double PT=e.getProcessingTime(a.get(MachineIndex));
    double DD=e.getDueDate();
    double RT=e.getRelDate();
    return 
    		Div(
    				 Add(
    					 Div(
    						 Sub(
    							 Add(RT ,PT)
    							,
    							 Mul(
    								 Mul(
    									 PT
    									,
    									 Sub( DD ,RT)
    									)
    								,
    								 Sub( 0.7222472440182017 ,0.7222472440182017)
    								)
    							)
    						,
    						 Add(
    							 Add(
    								 PT
    								,
    								 Div(
    									 Sub( DD ,PT)
    									,
    									 Add(
    										 Add(
    											 Mul(
    												 Add(RT, DD)
    												,
    												 Mul(
    													 Mul( PT ,DD)
    													,
    													 Sub( 0.7222472440182017,0.7222472440182017)
    													)
    												)
    											,
    											 Div(
    												 Add(
    													 Sub( DD ,RT)
    													,
    													 Sub( PT ,RT)
    													)
    												,
    												 Sub(
    													 Sub( PT ,DD)
    													,
    													 Sub( 0.7222472440182017, PT)
    													)
    												)
    											)
    										,
    										 Add( DD, PT)
    										)
    									)
    								)
    							,
    							 Add( DD, PT)
    							)
    						)
    					,
    					 Add( 0.7222472440182017, 0.7222472440182017)
    					)
    				,
    				 Add(
    					 Mul( PT ,RT)
    					,
    					 Add( DD, RT)
    					)
    				)
    		;
  }
}
