package com.lm.algorithms.rule.transportor;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;
import com.lm.domain.Operation;
import com.lm.algorithms.rule.GPRuleBase;
/**
 * GP引入的节点
 */
public class TransGP3 extends GPRuleBase implements ITransportorRule {

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
    						 Add (RT ,DD)
    						,
    						 Mul( 
    							 Mul( PT ,DD)
    							,
    							 Sub( 0.7222472440182017 ,0.7222472440182017)
    							)
    						)
    					,
    					 Div(
    						 Add( 
    							 Sub( DD ,RT)
    							,
    							 Add( 
    								 Sub( PT ,0.7222472440182017)
    								,
    								 Sub( RT ,PT)
    								)
    							)
    						,
    						 Sub( 
    							 Sub( PT, DD)
    							,
    							 Sub( 0.7222472440182017 ,PT)
    							)
    						)
    					) 
    				,
    				 Sub(
    					 Mul(
    						 Sub(
    							 Div(
    								 Add( PT, RT)
    								,
    								 Mul(
    									 Add( RT ,DD)
    									,
    									 Mul(
    										 Mul( PT ,DD)
    										,
    										 Sub( 0.7222472440182017 ,0.7222472440182017)
    										)
    									)
    								)
    							,
    							 Div( DD ,DD)
    							)
    						,
    						 Add(
    							 Sub( PT ,PT)
    							,
    							 Mul( RT, RT)
    							)
    						)
    					,
    					 Mul( 
    						 Mul(
    							 Sub( DD, PT)
    							,
    							 Div( PT ,PT)
    							)
    						,
    						 Sub(
    							 Add( RT ,PT)
    							,
    							 Div(
    								 Add( 
    									 Add(
    										 PT
    										, 
    										 Sub( RT ,0.7222472440182017)
    										)
    									,
    									 Mul( 0.7222472440182017, DD)
    									)
    								,
    								 Add(
    									 Sub(
    										 Sub(
    											 Div( 0.7222472440182017, PT)
    											,
    											 Sub( RT, DD)
    											)
    										,
    										 PT
    										)
    									,
    									 Add( DD, 0.7222472440182017)
    									)
    								)
    							)
    						)
    					)
    				)
    		;
  }
}
