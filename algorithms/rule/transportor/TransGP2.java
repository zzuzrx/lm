package com.lm.algorithms.rule.transportor;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;
import com.lm.domain.Operation;
import com.lm.algorithms.rule.GPRuleBase;
/**
 * GP引入的节点
 */
public class TransGP2 extends GPRuleBase implements ITransportorRule {

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
    					 Mul(
    						 Add(
    							 Sub( 
    								 Add( DD 
    									,
    									 Div( 
    										 Div(DD ,RT)
    									    ,
    										 0.7222472440182017
    										)
    									)
    								,
    								 Mul(
    									 RT 
    									,
    									 Sub( 0.7222472440182017 ,DD)
    									)
    								)
    							,
    							 Sub( RT ,0.7222472440182017)
    							)
    						,
    						 Mul( 
    							 Mul( PT ,DD)
    							, 
    							 Sub( 0.7222472440182017, 0.7222472440182017)
    							)
    						)
    					, 
    					 Div(
    						 Add( 
    							 Sub( DD ,RT)
    							 ,
    							 Add( 0.7222472440182017 ,RT)
    							)
    						, 
    						 Sub(
    							 Sub( PT, DD)
    							,
    							 Sub( 0.7222472440182017, PT)
    							)
    						)
    					)
    				,
    				 Sub( 
    					 Mul(
    						 Sub(
    							 Div( 0.7222472440182017, DD)
    							,
    							 Div( DD ,DD)
    							)
    						,
    						 Add(
    							 Sub( PT ,PT)
    							,
    							 Sub( 
    								 Add( RT, PT)
    								,
    								 Sub(PT, PT)
    								)
    							)
    						)
    					,
    					 Mul(
    						 Mul(
    							 Sub( DD,PT)
    							,
    							 Div( PT ,PT)
    							)
    						,
    						 Sub( 
    							 Add( RT ,PT) 
    							,
    							 Sub( PT, PT)
    							)
    						)
    					)
    				)
    		;
  }
}
